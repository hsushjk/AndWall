package com.arslan.shizuwall.shell

import com.arslan.shizuwall.firewall.FirewallCommands
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VpnCommandPlannerTest {

    private val installed = setOf("com.example.a", "com.example.b", "10:com.example.work")

    private fun plan(
        commands: List<String>,
        armed: Boolean = false,
        blocked: Set<String> = emptySet(),
    ) = VpnCommandPlanner.plan(
        commands = commands,
        armed = armed,
        blocked = blocked,
        packageExists = { it in installed },
    )

    @Test
    fun `chain enable arms the backend`() {
        val result = plan(listOf(FirewallCommands.CHAIN3_ENABLE))

        assertTrue(result.armed)
        assertTrue(result.results.single().success)
        assertTrue(result.blocked.isEmpty())
    }

    @Test
    fun `chain disable disarms the backend`() {
        val result = plan(listOf(FirewallCommands.CHAIN3_DISABLE), armed = true)

        assertFalse(result.armed)
        assertTrue(result.results.single().success)
    }

    @Test
    fun `block command adds package and records its index`() {
        val result = plan(listOf(FirewallCommands.CHAIN3_ENABLE, FirewallCommands.block("com.example.a")))

        assertEquals(setOf("com.example.a"), result.blocked)
        assertEquals(listOf(1), result.blockIndexes)
        assertTrue(result.results.all { it.success })
    }

    @Test
    fun `unblock command removes package`() {
        val result = plan(
            commands = listOf(FirewallCommands.unblock("com.example.a")),
            armed = true,
            blocked = setOf("com.example.a", "com.example.b"),
        )

        assertEquals(setOf("com.example.b"), result.blocked)
        assertTrue(result.results.single().success)
    }

    @Test
    fun `work profile packages are skipped instead of failing`() {
        val result = plan(listOf(FirewallCommands.block("10:com.example.work")), armed = true)

        assertTrue(result.results.single().success)
        assertEquals("skipped work profile app", result.results.single().stdout)
        assertTrue(result.blocked.isEmpty())
    }

    @Test
    fun `missing package fails the command and is not blocked`() {
        val result = plan(listOf(FirewallCommands.block("com.example.missing")), armed = true)

        assertFalse(result.results.single().success)
        assertEquals("App is not installed", result.results.single().stderr)
        assertTrue(result.blocked.isEmpty())
    }

    @Test
    fun `unsupported command fails the command`() {
        val result = plan(listOf("cmd connectivity set-chain3-enabled maybe"), armed = true)

        assertFalse(result.results.single().success)
        assertEquals(
            "Unsupported shell command in VPN mode: cmd connectivity set-chain3-enabled maybe",
            result.results.single().stderr,
        )
    }

    @Test
    fun `commands are applied in order`() {
        val result = plan(
            commands = listOf(
                FirewallCommands.CHAIN3_ENABLE,
                FirewallCommands.block("com.example.a"),
                FirewallCommands.block("com.example.b"),
                FirewallCommands.unblock("com.example.a"),
            ),
        )

        assertTrue(result.armed)
        assertEquals(setOf("com.example.b"), result.blocked)
        assertEquals(listOf(1, 2), result.blockIndexes)
        assertEquals(4, result.results.size)
        assertTrue(result.results.all { it.success })
    }

    @Test
    fun `empty command list keeps the current state`() {
        val result = plan(emptyList(), armed = true, blocked = setOf("com.example.a"))

        assertTrue(result.armed)
        assertEquals(setOf("com.example.a"), result.blocked)
        assertTrue(result.results.isEmpty())
    }
}