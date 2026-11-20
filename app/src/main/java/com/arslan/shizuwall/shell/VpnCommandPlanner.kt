package com.arslan.shizuwall.shell

import com.arslan.shizuwall.firewall.FirewallCommands
import com.arslan.shizuwall.utils.AppKey

/** Pure translation of firewall shell commands into VPN blocked app set changes. */
internal object VpnCommandPlanner {

    data class Result(
        val results: List<ShellResult>,
        val armed: Boolean,
        val blocked: Set<String>,
        val blockIndexes: List<Int>,
    )

    fun plan(
        commands: List<String>,
        armed: Boolean,
        blocked: Set<String>,
        packageExists: (String) -> Boolean,
        isSecondary: (String) -> Boolean = { AppKey.isSecondary(it) },
    ): Result {
        var nextArmed = armed
        val nextBlocked = blocked.toMutableSet()
        val results = ArrayList<ShellResult>(commands.size)
        val blockIndexes = mutableListOf<Int>()

        commands.forEachIndexed { index, command ->
            results.add(
                when {
                    command == FirewallCommands.CHAIN3_ENABLE -> {
                        nextArmed = true
                        ShellResult(0, "vpn backend armed", "")
                    }

                    command == FirewallCommands.CHAIN3_DISABLE -> {
                        nextArmed = false
                        ShellResult(0, "vpn backend disarmed", "")
                    }

                    else -> {
                        val parsed = FirewallCommands.parseNetworking(command)
                        when {
                            parsed == null -> ShellResult(1, "", "Unsupported shell command in VPN mode: $command")
                            isSecondary(parsed.key) -> ShellResult(0, "skipped work profile app", "")
                            parsed.networkingEnabled -> {
                                nextBlocked.remove(parsed.key)
                                ShellResult(0, "", "")
                            }

                            !packageExists(parsed.key) -> ShellResult(1, "", "App is not installed")

                            else -> {
                                nextBlocked.add(parsed.key)
                                blockIndexes.add(index)
                                ShellResult(0, "", "")
                            }
                        }
                    }
                }
            )
        }

        return Result(results, nextArmed, nextBlocked, blockIndexes)
    }
}