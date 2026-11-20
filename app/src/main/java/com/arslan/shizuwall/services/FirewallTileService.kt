package com.arslan.shizuwall.services

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.arslan.shizuwall.R
import com.arslan.shizuwall.WorkingMode
import com.arslan.shizuwall.firewall.VpnFirewallController
import com.arslan.shizuwall.ui.MainActivity
import com.arslan.shizuwall.utils.FirewallUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FirewallTileService : TileService() {

    private lateinit var sharedPreferences: SharedPreferences
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == MainActivity.KEY_FIREWALL_ENABLED ||
            key == MainActivity.KEY_ACTIVE_PACKAGES ||
            key == MainActivity.KEY_FIREWALL_SAVED_ELAPSED ||
            key == MainActivity.KEY_FIREWALL_MODE
        ) {
            updateTile()
        }
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
        runCatching { sharedPreferences.registerOnSharedPreferenceChangeListener(prefsListener) }
    }

    override fun onStopListening() {
        super.onStopListening()
        runCatching { sharedPreferences.unregisterOnSharedPreferenceChangeListener(prefsListener) }
    }

    override fun onClick() {
        super.onClick()
        if (requestVpnConsentIfNeeded()) return
        val enable = !FirewallUtils.loadFirewallEnabled(sharedPreferences)
        scope.launch {
            FirewallUtils.toggle(this@FirewallTileService, sharedPreferences, enable)
            updateTile()
        }
    }

    /** Without VPN consent the app has to request it; a toast alone cannot enable the firewall. */
    private fun requestVpnConsentIfNeeded(): Boolean {
        val mode = WorkingMode.fromName(
            sharedPreferences.getString(MainActivity.KEY_WORKING_MODE, WorkingMode.SHIZUKU.name)
        )
        if (mode != WorkingMode.VPN || VpnFirewallController.hasConsent(this)) return false
        runCatching {
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(MainActivity.EXTRA_REQUEST_VPN_CONSENT, true)
                }
            )
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching { sharedPreferences.unregisterOnSharedPreferenceChangeListener(prefsListener) }
        job.cancel()
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        tile.state = if (FirewallUtils.loadFirewallEnabled(sharedPreferences)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = getString(R.string.firewall)
        tile.icon = Icon.createWithResource(this, R.drawable.ic_quick_tile)
        tile.updateTile()
    }
}
