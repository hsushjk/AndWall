package com.arslan.shizuwall.firewall

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.VpnService
import android.util.Log
import com.arslan.shizuwall.WorkingMode
import com.arslan.shizuwall.ui.MainActivity
import com.arslan.shizuwall.services.VpnFirewallService
import com.arslan.shizuwall.utils.AppKey
import com.arslan.shizuwall.utils.FirewallUtils

/**
 * Runs the firewall as a local VPN: blocked apps are routed into a tun device
 * whose packets are dropped.
 */
object VpnFirewallController {

    private const val TAG = "VpnFirewallController"

    internal const val ACTION_APPLY = "com.arslan.shizuwall.action.VPN_APPLY"
    internal const val ACTION_STOP = "com.arslan.shizuwall.action.VPN_STOP"
    internal const val EXTRA_PACKAGES = "com.arslan.shizuwall.extra.VPN_PACKAGES"

    private const val KEY_BLOCKED_APPS = "vpn_blocked_apps"
    private const val KEY_ARMED = "vpn_armed"
    private const val KEY_RUNNING = "vpn_running"

    /** Set only while this process owns the tunnel; the persisted running flag can be stale after a force stop. */
    @Volatile
    private var tunnelEstablished = false

    private fun prefs(context: Context) =
        context.getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE)

    /** Returns the system consent intent, or null when the permission is already granted. */
    fun consentIntent(context: Context): Intent? = runCatching { VpnService.prepare(context) }.getOrNull()

    fun hasConsent(context: Context): Boolean = consentIntent(context) == null

    fun isArmed(context: Context): Boolean = prefs(context).getBoolean(KEY_ARMED, false)

    fun setArmed(context: Context, armed: Boolean) {
        prefs(context).edit().putBoolean(KEY_ARMED, armed).apply()
    }

    fun isRunning(context: Context): Boolean = prefs(context).getBoolean(KEY_RUNNING, false)

    internal fun markRunning(context: Context, running: Boolean) {
        prefs(context).edit().putBoolean(KEY_RUNNING, running).apply()
    }

    /** Called by the service when the tun device is created or torn down. */
    internal fun markTunnelEstablished(established: Boolean) {
        tunnelEstablished = established
    }

    fun isTunnelEstablishedInProcess(): Boolean = tunnelEstablished

    /** Currently blocked apps, stored as [AppKey] values. */
    fun loadBlockedPackages(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_BLOCKED_APPS, emptySet())?.toSet() ?: emptySet()

    internal fun saveBlockedPackages(context: Context, packages: Set<String>) {
        prefs(context).edit().putStringSet(KEY_BLOCKED_APPS, packages.toSet()).apply()
    }

    fun clearBlockedPackages(context: Context) {
        prefs(context).edit().remove(KEY_BLOCKED_APPS).apply()
    }

    internal fun extractPackages(intent: Intent): List<String>? =
        intent.getStringArrayListExtra(EXTRA_PACKAGES)

    /**
     * Applies a blocked set; starts the tunnel when non-empty and stops it when empty.
     * A tunnel with no allowed apps would capture everything, so it is never started.
     */
    fun apply(context: Context, blockedApps: Set<String>): Boolean {
        // Work profiles are not captured by a per-user VpnService.
        val supported = blockedApps.filterNot { AppKey.isSecondary(it) }.toSet()
        saveBlockedPackages(context, supported)
        Log.d(TAG, "apply blocked=${blockedApps.size} supported=${supported.size} consent=${hasConsent(context)}")
        if (supported.isEmpty()) {
            stopTunnel(context)
            return true
        }
        if (!hasConsent(context)) {
            Log.w(TAG, "VPN consent is missing, tunnel not started")
            return false
        }
        val intent = Intent(context, VpnFirewallService::class.java).apply {
            action = ACTION_APPLY
            putStringArrayListExtra(EXTRA_PACKAGES, ArrayList(supported))
        }
        val started = startService(context, intent)
        if (started) {
            // The service clears this flag when the tunnel stops or fails.
            tunnelEstablished = true
        }
        return started
    }

    /** Turns the backend off completely. */
    fun disable(context: Context) {
        setArmed(context, false)
        clearBlockedPackages(context)
        stopTunnel(context)
    }

    fun stopTunnel(context: Context) {
        if (isRunning(context)) {
            val intent = Intent(context, VpnFirewallService::class.java).apply {
                action = ACTION_STOP
            }
            startService(context, intent)
        }
        markRunning(context, false)
        tunnelEstablished = false
    }

    /**
     * Restores the tunnel after a process restart, or clears the stale enabled state
     * when the tunnel is gone and cannot be recreated.
     *
     * @return whether the firewall is enabled.
     */
    fun reconcile(context: Context, prefs: SharedPreferences): Boolean {
        val workingMode = WorkingMode.fromName(prefs.getString(MainActivity.KEY_WORKING_MODE, WorkingMode.SHIZUKU.name))
        if (workingMode != WorkingMode.VPN) {
            return FirewallUtils.loadFirewallEnabled(prefs)
        }
        if (!FirewallUtils.loadFirewallEnabled(prefs)) {
            if (tunnelEstablished) stopTunnel(context)
            return false
        }
        if (tunnelEstablished) return true
        val activePackages = FirewallUtils.loadActivePackages(prefs)
        if (activePackages.isEmpty()) return true
        if (hasConsent(context) && apply(context, activePackages)) {
            Log.i(TAG, "Restored the VPN firewall after the app process was restarted")
            return true
        }
        Log.w(TAG, "The VPN firewall is not running anymore, clearing the stale state")
        setArmed(context, false)
        clearBlockedPackages(context)
        markRunning(context, false)
        FirewallUtils.saveFirewallEnabled(context, prefs, false)
        FirewallUtils.saveActivePackages(prefs, emptySet())
        return false
    }

    fun requestStop(context: Context) {
        val intent = Intent(context, VpnFirewallService::class.java).apply {
            action = ACTION_STOP
        }
        startService(context, intent)
    }

    internal fun startService(context: Context, intent: Intent): Boolean {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            true
        } catch (throwable: Throwable) {
            Log.e(TAG, "Unable to start the VPN firewall service", throwable)
            false
        }
    }
}