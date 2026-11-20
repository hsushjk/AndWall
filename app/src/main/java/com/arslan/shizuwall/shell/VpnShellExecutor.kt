package com.arslan.shizuwall.shell

import android.content.Context
import android.util.Log
import com.arslan.shizuwall.firewall.FirewallCommands
import com.arslan.shizuwall.firewall.VpnFirewallController
import com.arslan.shizuwall.utils.AppKey

/**
 * Translates the connectivity-chain shell commands used by the firewall into
 * VPN blocked app set changes.
 */
class VpnShellExecutor(private val context: Context) : ShellExecutor {

    companion object {
        private const val TAG = "VpnShellExecutor"
    }

    override suspend fun exec(command: String): ShellResult = execBatch(listOf(command)).first()

    override suspend fun execBatch(commands: List<String>): List<ShellResult> {
        if (commands.isEmpty()) return emptyList()
        if (commands.any { !isVpnCommand(it) }) {
            return commands.map { command ->
                if (isVpnCommand(command)) {
                    execVpnCommands(listOf(command)).first()
                } else {
                    unsupported(command)
                }
            }
        }
        return execVpnCommands(commands)
    }

    private fun execVpnCommands(commands: List<String>): List<ShellResult> {
        val initiallyArmed = VpnFirewallController.isArmed(context) || VpnFirewallController.isRunning(context)
        val plan = VpnCommandPlanner.plan(
            commands = commands,
            armed = initiallyArmed,
            blocked = VpnFirewallController.loadBlockedPackages(context),
            packageExists = ::packageExists,
        )
        if (plan.armed != initiallyArmed) {
            VpnFirewallController.setArmed(context, plan.armed)
        }

        val started = if (plan.armed) {
            VpnFirewallController.apply(context, plan.blocked)
        } else {
            VpnFirewallController.clearBlockedPackages(context)
            VpnFirewallController.stopTunnel(context)
            true
        }

        val results = plan.results.toMutableList()
        if (plan.armed && !started) {
            Log.w(TAG, "VPN tunnel could not be started for ${plan.blocked.size} app(s)")
            plan.blockIndexes.forEach { index ->
                results[index] = ShellResult(1, "", "Unable to start the VPN firewall")
            }
        }
        return results
    }

    private fun isVpnCommand(command: String): Boolean {
        return command == FirewallCommands.CHAIN3_ENABLE ||
            command == FirewallCommands.CHAIN3_DISABLE ||
            FirewallCommands.isNetworkingCommand(command)
    }

    private fun unsupported(command: String): ShellResult {
        return ShellResult(1, "", "Unsupported shell command in VPN mode: $command")
    }

    private fun packageExists(key: String): Boolean {
        val packageName = AppKey.packageOf(key)
        return try {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: Throwable) {
            false
        }
    }
}