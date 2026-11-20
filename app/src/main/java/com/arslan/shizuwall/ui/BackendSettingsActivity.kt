package com.arslan.shizuwall.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.arslan.shizuwall.FirewallMode
import com.arslan.shizuwall.R
import com.arslan.shizuwall.WorkingMode
import com.arslan.shizuwall.firewall.FirewallCommands
import com.arslan.shizuwall.firewall.VpnFirewallController
import com.arslan.shizuwall.shell.RootShellExecutor
import com.arslan.shizuwall.shell.ShellExecutor
import com.arslan.shizuwall.shell.ShellExecutorProvider
import com.arslan.shizuwall.utils.FirewallUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BackendSettingsActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rootView: ViewGroup

    private lateinit var radioGroupWorkingMode: RadioGroup
    private lateinit var radioShizukuMode: RadioButton
    private lateinit var radioVpnMode: RadioButton
    private lateinit var radioLadbMode: RadioButton
    private lateinit var radioRootMode: RadioButton
    private lateinit var cardSetLadb: com.google.android.material.card.MaterialCardView
    private lateinit var layoutSetLadb: android.widget.LinearLayout
    private lateinit var switchAutoEnableOnShizukuStart: androidx.appcompat.widget.SwitchCompat
    private lateinit var cardAutoEnableOnShizukuStart: com.google.android.material.card.MaterialCardView
    private lateinit var switchApplyRootRulesAfterReboot: androidx.appcompat.widget.SwitchCompat
    private lateinit var cardApplyRootRulesAfterReboot: com.google.android.material.card.MaterialCardView

    private var autoEnablePreviousState: Boolean = false
    private var rootReapplyPreviousState: Boolean = false
    private var suppressWorkingModeListener = false
    private var pendingVpnConsentMode: WorkingMode? = null

    private val vpnConsentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val requestedMode = pendingVpnConsentMode
        pendingVpnConsentMode = null
        if (requestedMode == null) return@registerForActivityResult
        if (result.resultCode == RESULT_OK) {
            applyWorkingMode(requestedMode)
        } else {
            Toast.makeText(this, getString(R.string.vpn_permission_denied), Toast.LENGTH_SHORT).show()
            val currentMode = WorkingMode.fromName(
                sharedPreferences.getString(MainActivity.KEY_WORKING_MODE, WorkingMode.SHIZUKU.name)
            )
            checkRadioFor(currentMode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE)

        enableEdgeToEdge()
        setContentView(R.layout.activity_settings_backend)

        rootView = findViewById(R.id.backendSettingsRoot)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setupSettingsChrome(R.id.backendSettingsRoot, toolbar)

        radioGroupWorkingMode = findViewById(R.id.radioGroupWorkingMode)
        radioShizukuMode = findViewById(R.id.radioShizukuMode)
        radioVpnMode = findViewById(R.id.radioVpnMode)
        radioLadbMode = findViewById(R.id.radioLadbMode)
        radioRootMode = findViewById(R.id.radioRootMode)
        cardSetLadb = findViewById(R.id.cardSetLadb)
        layoutSetLadb = findViewById(R.id.layoutSetLadb)
        switchAutoEnableOnShizukuStart = findViewById(R.id.switchAutoEnableOnShizukuStart)
        cardAutoEnableOnShizukuStart = findViewById(R.id.cardAutoEnableOnShizukuStart)
        switchApplyRootRulesAfterReboot = findViewById(R.id.switchApplyRootRulesAfterReboot)
        cardApplyRootRulesAfterReboot = findViewById(R.id.cardApplyRootRulesAfterReboot)

        loadSettings()
        setupListeners()

        if (savedInstanceState == null && intent.getBooleanExtra(CoachMarks.EXTRA_TOUR, false)) {
            CoachMarks.show(this, listOf(
                CoachMarks.Step({ radioGroupWorkingMode }, getString(R.string.onboarding_mode_selection_title), getString(R.string.onboarding_mode_selection_message), onTarget = {
                    startActivity(Intent(this, FirewallSettingsActivity::class.java).putExtra(CoachMarks.EXTRA_TOUR, true))
                    finish()
                })
            ))
        }
    }

    private fun loadSettings() {
        switchAutoEnableOnShizukuStart.isChecked = sharedPreferences.getBoolean(MainActivity.KEY_AUTO_ENABLE_ON_SHIZUKU_START, false)
        switchApplyRootRulesAfterReboot.isChecked = sharedPreferences.getBoolean(MainActivity.KEY_APPLY_ROOT_RULES_AFTER_REBOOT, false)

        val workingModeName = sharedPreferences.getString(MainActivity.KEY_WORKING_MODE, WorkingMode.SHIZUKU.name)
        val workingMode = WorkingMode.fromName(workingModeName)
        checkRadioFor(workingMode)

        updateWorkingModeDependentUi(workingMode, restoreAutoEnable = false)
    }

    private fun setupListeners() {
        switchAutoEnableOnShizukuStart.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(MainActivity.KEY_AUTO_ENABLE_ON_SHIZUKU_START, isChecked).apply()
            setResult(RESULT_OK)
        }

        switchApplyRootRulesAfterReboot.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(MainActivity.KEY_APPLY_ROOT_RULES_AFTER_REBOOT, isChecked).apply()
            setResult(RESULT_OK)
        }

        radioGroupWorkingMode.setOnCheckedChangeListener { _, checkedId ->
            if (suppressWorkingModeListener) return@setOnCheckedChangeListener

            val currentMode = WorkingMode.fromName(sharedPreferences.getString(MainActivity.KEY_WORKING_MODE, WorkingMode.SHIZUKU.name))
            val mode = when (checkedId) {
                R.id.radioVpnMode -> WorkingMode.VPN
                R.id.radioLadbMode -> WorkingMode.LADB
                R.id.radioRootMode -> WorkingMode.ROOT
                else -> WorkingMode.SHIZUKU
            }
            if (mode == currentMode) return@setOnCheckedChangeListener

            if (mode == WorkingMode.ROOT && !RootShellExecutor.hasRootAccess()) {
                showRootNotFoundDialog()
                checkRadioFor(currentMode)
                return@setOnCheckedChangeListener
            }

            if (mode == WorkingMode.VPN && !VpnFirewallController.hasConsent(this)) {
                val consentIntent = VpnFirewallController.consentIntent(this)
                if (consentIntent != null) {
                    pendingVpnConsentMode = mode
                    vpnConsentLauncher.launch(consentIntent)
                    return@setOnCheckedChangeListener
                }
            }

            applyWorkingMode(mode)
        }

        layoutSetLadb.setOnClickListener {
            if (!radioLadbMode.isChecked) {
                Toast.makeText(this, getString(R.string.working_mode_select_ladb_prompt), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                startActivity(Intent(this, com.arslan.shizuwall.LadbSetupActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.open_ladb_setup), Toast.LENGTH_SHORT).show()
            }
        }

        makeCardClickableForSwitch(switchAutoEnableOnShizukuStart)
        makeCardClickableForSwitch(switchApplyRootRulesAfterReboot)
    }

    private fun checkRadioFor(mode: WorkingMode) {
        suppressWorkingModeListener = true
        when (mode) {
            WorkingMode.VPN -> radioGroupWorkingMode.check(R.id.radioVpnMode)
            WorkingMode.LADB -> radioGroupWorkingMode.check(R.id.radioLadbMode)
            WorkingMode.ROOT -> radioGroupWorkingMode.check(R.id.radioRootMode)
            else -> radioGroupWorkingMode.check(R.id.radioShizukuMode)
        }
        suppressWorkingModeListener = false
    }

    private fun applyWorkingMode(mode: WorkingMode) {
        val currentMode = WorkingMode.fromName(
            sharedPreferences.getString(MainActivity.KEY_WORKING_MODE, WorkingMode.SHIZUKU.name)
        )
        if (mode == currentMode) {
            checkRadioFor(mode)
            return
        }

        val firewallEnabled = FirewallUtils.loadFirewallEnabled(sharedPreferences)
        val previousExecutor: ShellExecutor? =
            if (firewallEnabled) ShellExecutorProvider.forContext(this) else null

        if (mode == WorkingMode.VPN) {
            sharedPreferences.edit()
                .putBoolean(MainActivity.KEY_SHOW_OTHER_PROFILES, false)
                .putStringSet(MainActivity.KEY_MOBILE_ONLY_APPS, emptySet())
                .apply()
            val firewallMode = FirewallUtils.firewallMode(sharedPreferences)
            if (firewallMode.requiresForegroundDetection()) {
                sharedPreferences.edit()
                    .putString(MainActivity.KEY_FIREWALL_MODE, FirewallMode.WHITELIST.name)
                    .apply()
                Toast.makeText(this, getString(R.string.vpn_mode_requires_static_app_list), Toast.LENGTH_LONG).show()
            }
        }
        if (currentMode == WorkingMode.VPN && mode != WorkingMode.VPN) {
            VpnFirewallController.disable(this)
        }

        sharedPreferences.edit().putString(MainActivity.KEY_WORKING_MODE, mode.name).apply()
        if (mode != WorkingMode.ROOT) {
            RootShellExecutor.shutdown()
        }
        setResult(RESULT_OK)

        checkRadioFor(mode)
        TransitionManager.beginDelayedTransition(rootView, AutoTransition())
        updateWorkingModeDependentUi(mode, restoreAutoEnable = true)

        if (firewallEnabled) {
            Toast.makeText(this, getString(R.string.vpn_mode_backend_switch_firewall_disabled), Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    runCatching {
                        previousExecutor?.exec(FirewallCommands.CHAIN3_ENABLE)
                        val activePackages = FirewallUtils.loadActivePackages(sharedPreferences).toList()
                        previousExecutor?.execBatch(FirewallCommands.unblockAll(activePackages))
                        previousExecutor?.exec(FirewallCommands.CHAIN3_DISABLE)
                    }
                    FirewallUtils.saveFirewallEnabled(this@BackendSettingsActivity, sharedPreferences, false)
                    FirewallUtils.saveActivePackages(sharedPreferences, emptySet())
                }
                setResult(RESULT_OK)
            }
        }
    }

    private fun showRootNotFoundDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.working_mode_root)
            .setMessage(R.string.root_not_found_message)
            .setPositiveButton(R.string.ok, null)
            .setCancelable(true)
            .show()
    }

    private fun updateWorkingModeDependentUi(mode: WorkingMode, restoreAutoEnable: Boolean) {
        val isLadb = mode == WorkingMode.LADB
        val isShizuku = mode == WorkingMode.SHIZUKU
        val isRoot = mode == WorkingMode.ROOT

        cardSetLadb.visibility = if (isLadb) View.VISIBLE else View.GONE
        cardAutoEnableOnShizukuStart.visibility = if (isShizuku) View.VISIBLE else View.GONE
        switchAutoEnableOnShizukuStart.isEnabled = isShizuku
        cardApplyRootRulesAfterReboot.visibility = if (isRoot) View.VISIBLE else View.GONE
        switchApplyRootRulesAfterReboot.isEnabled = isRoot

        if (!isShizuku) {
            autoEnablePreviousState = switchAutoEnableOnShizukuStart.isChecked
            if (switchAutoEnableOnShizukuStart.isChecked) {
                switchAutoEnableOnShizukuStart.isChecked = false
                sharedPreferences.edit().putBoolean(MainActivity.KEY_AUTO_ENABLE_ON_SHIZUKU_START, false).apply()
            }
        } else if (restoreAutoEnable && autoEnablePreviousState) {
            switchAutoEnableOnShizukuStart.isChecked = true
            sharedPreferences.edit().putBoolean(MainActivity.KEY_AUTO_ENABLE_ON_SHIZUKU_START, true).apply()
        }

        if (!isRoot) {
            rootReapplyPreviousState = switchApplyRootRulesAfterReboot.isChecked
            if (switchApplyRootRulesAfterReboot.isChecked) {
                switchApplyRootRulesAfterReboot.isChecked = false
                sharedPreferences.edit().putBoolean(MainActivity.KEY_APPLY_ROOT_RULES_AFTER_REBOOT, false).apply()
            }
        } else if (restoreAutoEnable && rootReapplyPreviousState) {
            switchApplyRootRulesAfterReboot.isChecked = true
            sharedPreferences.edit().putBoolean(MainActivity.KEY_APPLY_ROOT_RULES_AFTER_REBOOT, true).apply()
        }
    }
}
