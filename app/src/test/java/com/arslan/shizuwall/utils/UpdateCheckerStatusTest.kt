package com.arslan.shizuwall.utils

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.Closeable
import java.net.InetAddress
import java.net.ServerSocket

/**
 * Exercises [UpdateChecker.check] against a tiny local HTTP stub so that all three
 * outcomes (FOUND / NO_RELEASE / FAILED) are covered without touching the network.
 */
class UpdateCheckerStatusTest {

    private var stub: StubServer? = null

    @After
    fun stopStub() {
        stub?.close()
        stub = null
    }

    private fun baseUrl(vararg routes: Pair<String, Pair<Int, String>>): String {
        val server = StubServer(routes.toMap())
        stub = server
        return server.baseUrl
    }

    @Test
    fun publishedReleaseIsFound() {
        val base = baseUrl(
            "/releases/latest" to (200 to """{"tag_name":"v4.8.0","html_url":"https://example.com/r/4.8.0"}""")
        )

        val result = UpdateChecker.check(base)

        assertEquals(UpdateChecker.Status.FOUND, result.status)
        val release = result.release
        assertNotNull(release)
        assertEquals("v4.8.0", release!!.tag)
        assertEquals("https://example.com/r/4.8.0", release.url)
    }

    @Test
    fun tagIsUsedWhileNoReleaseExists() {
        val base = baseUrl(
            "/releases/latest" to (404 to ""),
            "/releases" to (200 to "[]"),
            "/tags" to (200 to """[{"name":"v4.8.0"}]""")
        )

        val result = UpdateChecker.check(base)

        assertEquals(UpdateChecker.Status.FOUND, result.status)
        assertEquals("v4.8.0", result.release?.tag)
        assertEquals("https://github.com/hsushjk/AndWall/tree/v4.8.0", result.release?.url)
    }

    @Test
    fun reachableRepositoryWithoutReleasesOrTagsIsNoRelease() {
        val base = baseUrl(
            "/releases/latest" to (404 to ""),
            "/releases" to (200 to "[]"),
            "/tags" to (200 to "[]")
        )

        val result = UpdateChecker.check(base)

        assertEquals(UpdateChecker.Status.NO_RELEASE, result.status)
        assertNull(result.release)
    }

    @Test
    fun httpErrorStillCountsAsReachableRepository() {
        val base = baseUrl(
            "/releases/latest" to (500 to ""),
            "/releases" to (500 to ""),
            "/tags" to (500 to "")
        )

        val result = UpdateChecker.check(base)

        assertEquals(UpdateChecker.Status.NO_RELEASE, result.status)
        assertNull(result.release)
    }

    @Test
    fun connectionFailureIsReportedAsFailed() {
        // Nothing listens on port 1, so every request is refused locally.
        val result = UpdateChecker.check("http://127.0.0.1:1")

        assertEquals(UpdateChecker.Status.FAILED, result.status)
        assertNull(result.release)
    }

    /** Minimal HTTP/1.1 stub: answers GET paths from [routes], everything else is 404. */
    private class StubServer(private val routes: Map<String, Pair<Int, String>>) : Closeable {
        private val socket = ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"))

        val baseUrl: String = "http://127.0.0.1:${socket.localPort}"

        private val worker = Thread {
            while (!socket.isClosed) {
                try {
                    socket.accept().use { client ->
                        val input = client.getInputStream()
                        val reader = input.bufferedReader()
                        val requestLine = reader.readLine() ?: return@use
                        while (true) {
                            val header = reader.readLine() ?: break
                            if (header.isEmpty()) break
                        }
                        val path = requestLine.split(' ').getOrNull(1)?.substringBefore('?') ?: "/"
                        val (code, body) = routes[path] ?: (404 to "")
                        val payload = body.toByteArray(Charsets.UTF_8)
                        val head = buildString {
                            append("HTTP/1.1 $code Stub\r\n")
                            append("Content-Type: application/json\r\n")
                            append("Content-Length: ${payload.size}\r\n")
                            append("Connection: close\r\n\r\n")
                        }
                        val out = client.getOutputStream()
                        out.write(head.toByteArray(Charsets.US_ASCII))
                        out.write(payload)
                        out.flush()
                    }
                } catch (_: Exception) {
                    // Socket closed or client went away; keep serving otherwise.
                }
            }
        }.apply {
            isDaemon = true
            name = "update-checker-stub"
            start()
        }

        override fun close() {
            socket.close()
            worker.interrupt()
        }
    }
}
