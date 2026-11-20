package com.arslan.shizuwall.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.arslan.shizuwall.ui.MainActivity
import com.arslan.shizuwall.R
import com.arslan.shizuwall.firewall.VpnFirewallController
import com.arslan.shizuwall.utils.AppKey
import com.arslan.shizuwall.utils.FirewallUtils
import java.io.FileInputStream
import java.io.IOException

/**
 * Local VPN firewall: captures only the blocked apps via [Builder.addAllowedApplication]
 * and drops every packet read from the tun device.
 */
class VpnFirewallService : VpnService() {

    companion object {
        private const val TAG = "VpnFirewallService"
        private const val CHANNEL_ID = "vpn_firewall_channel"
        private const val NOTIFICATION_ID = 2001

        private const val VPN_ADDRESS_V4 = "10.66.0.1"
        private const val VPN_ROUTE_V4 = "0.0.0.0"
        private const val VPN_ADDRESS_V6 = "fd00:1:66::1"
        private const val VPN_ROUTE_V6 = "::"
        private const val VPN_MTU = 1500
    }

    private var tun: ParcelFileDescriptor? = null
    private var drainThread: Thread? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundCompat(blockedCount())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand action=${intent?.action} extras=${intent?.extras?.keySet()}")
        when (intent?.action) {
            VpnFirewallController.ACTION_STOP -> {
                stopTunnel()
                return START_NOT_STICKY
            }

            VpnFirewallController.ACTION_APPLY, null -> {
                val blocked = intent?.let { VpnFirewallController.extractPackages(it) }
                    ?.toSet()
                    ?: VpnFirewallController.loadBlockedPackages(this)
                if (!applyBlockedPackages(blocked)) {
                    stopTunnel()
                    return START_NOT_STICKY
                }
            }
        }
        return START_STICKY
    }

    override fun onRevoke() {
        Log.w(TAG, "VPN permission was revoked by the system or another VPN")
        stopTunnel()
        markFirewallDisabled()
        super.onRevoke()
    }

    override fun onDestroy() {
        closeTunnel()
        VpnFirewallController.markTunnelEstablished(false)
        super.onDestroy()
    }

    private fun applyBlockedPackages(blockedApps: Set<String>): Boolean {
        val packages = resolvePackages(blockedApps)
        Log.d(TAG, "applyBlockedPackages requested=${blockedApps.size} resolved=${packages.size} $packages")
        if (packages.isEmpty()) {
            VpnFirewallController.saveBlockedPackages(this, emptySet())
            stopTunnel()
            return true
        }
        closeTunnel()
        val builder = Builder()
            .setSession(getString(R.string.vpn_firewall_session_name))
            .setMtu(VPN_MTU)
            .setBlocking(false)
            .addAddress(VPN_ADDRESS_V4, 32)
            .addRoute(VPN_ROUTE_V4, 0)
        runCatching {
            builder.addAddress(VPN_ADDRESS_V6, 128)
            builder.addRoute(VPN_ROUTE_V6, 0)
        }.onFailure { Log.w(TAG, "IPv6 is not available for the tunnel", it) }
        var added = 0
        packages.forEach { packageName ->
            try {
                builder.addAllowedApplication(packageName)
                added++
            } catch (throwable: Throwable) {
                Log.w(TAG, "Cannot capture $packageName", throwable)
            }
        }
        if (added == 0) {
            Log.w(TAG, "No blocked app could be captured, stopping the tunnel")
            stopTunnel()
            return false
        }
        val descriptor = try {
            builder.establish()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Unable to establish the tunnel", throwable)
            null
        }
        if (descriptor == null) {
            stopTunnel()
            return false
        }
        tun = descriptor
        VpnFirewallController.markRunning(this, true)
        VpnFirewallController.markTunnelEstablished(true)
        startDraining(descriptor)
        updateNotification(added)
        Log.d(TAG, "Tunnel established, capturing $added app(s)")
        return true
    }

    private fun resolvePackages(blockedApps: Set<String>): List<String> {
        return blockedApps.asSequence()
            .filterNot { AppKey.isSecondary(it) }
            .map { AppKey.packageOf(it) }
            .filterNot { it == packageName }
            .distinct()
            .filter { packageNameExists(it) }
            .toList()
    }

    private fun packageNameExists(packageName: String): Boolean {
        return try {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (throwable: Throwable) {
            Log.w(TAG, "$packageName is not installed anymore", throwable)
            false
        }
    }

    private fun startDraining(descriptor: ParcelFileDescriptor) {
        drainThread?.interrupt()
        val thread = Thread({
            val buffer = ByteArray(32 * 1024)
            try {
                FileInputStream(descriptor.fileDescriptor).use { input ->
                    while (!Thread.currentThread().isInterrupted) {
                        val read = input.read(buffer)
                        if (read < 0) break
                    }
                }
            } catch (_: IOException) {
                // Tunnel was closed while reading, expected on shutdown.
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }, "vpn-firewall-drain")
        thread.isDaemon = true
        thread.start()
        drainThread = thread
    }

    private fun closeTunnel() {
        drainThread?.interrupt()
        drainThread = null
        tun?.let { descriptor ->
            try {
                descriptor.close()
            } catch (_: IOException) {
                // Ignore, the descriptor is already gone.
            }
        }
        tun = null
    }

    private fun stopTunnel() {
        Log.d(TAG, "Stopping the tunnel")
        closeTunnel()
        VpnFirewallController.markRunning(this, false)
        VpnFirewallController.markTunnelEstablished(false)
        stopForegroundCompat()
        stopSelf()
    }

    private fun blockedCount(): Int = VpnFirewallController.loadBlockedPackages(this).size

    private fun markFirewallDisabled() {
        val prefs = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE)
        FirewallUtils.saveFirewallEnabled(this, prefs, false)
        FirewallUtils.saveActivePackages(prefs, emptySet())
        VpnFirewallController.setArmed(this, false)
        VpnFirewallController.clearBlockedPackages(this)
        VpnFirewallController.markRunning(this, false)
    }

    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.vpn_firewall_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.vpn_firewall_notification_channel_description)
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(blockedCount: Int) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.vpn_firewall_notification_title))
            .setContentText(getString(R.string.vpn_firewall_notification_text, blockedCount))
            .setSmallIcon(R.drawable.ic_quick_tile)
            .setOngoing(true)
            .setShowWhen(false)
            .setOnlyAlertOnce(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

    private fun startForegroundCompat(blockedCount: Int) {
        val notification = buildNotification(blockedCount)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotification(blockedCount: Int) {
        val manager = getSystemService(NotificationManager::class.java) ?: return
        manager.notify(NOTIFICATION_ID, buildNotification(blockedCount))
    }

    private fun stopForegroundCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
    }
}