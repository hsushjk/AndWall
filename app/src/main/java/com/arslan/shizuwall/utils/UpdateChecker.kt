package com.arslan.shizuwall.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.arslan.shizuwall.R
import com.arslan.shizuwall.ui.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker {
    private const val REPO_URL = "https://github.com/hsushjk/AndWall"
    private const val API_BASE = "https://api.github.com/repos/hsushjk/AndWall"
    private const val TIMEOUT_MS = 8000

    data class Release(val tag: String, val url: String)

    enum class Status { FOUND, NO_RELEASE, FAILED }

    data class CheckResult(val status: Status, val release: Release? = null)

    fun isNewer(remote: String, local: String): Boolean {
        val r = parts(remote)
        val l = parts(local)
        val diff = (0 until maxOf(r.size, l.size)).map { r.getOrElse(it) { 0 } - l.getOrElse(it) { 0 } }
        return diff.firstOrNull { it != 0 }?.let { it > 0 } ?: false
    }

    private fun parts(v: String) = v.trim().removePrefix("v").split('.').map { it.takeWhile(Char::isDigit).toIntOrNull() ?: 0 }

    /**
     * Looks up the newest release, falling back to the newest tag while the
     * repository has no published release yet.
     *
     * A repository that answers but has neither a release nor a tag yields
     * [Status.NO_RELEASE]; only a network/HTTP failure yields [Status.FAILED].
     */
    fun check(): CheckResult = check(API_BASE)

    /** Same as [check], but talks to [apiBase]; exposed so tests can use a local server. */
    internal fun check(apiBase: String): CheckResult {
        val reachability = Reachability()
        val release = latestRelease(apiBase, reachability) ?: firstRelease(apiBase, reachability) ?: firstTag(apiBase, reachability)
        return when {
            release != null -> CheckResult(Status.FOUND, release)
            reachability.responded -> CheckResult(Status.NO_RELEASE)
            else -> CheckResult(Status.FAILED)
        }
    }

    /** Convenience wrapper for callers that only care about the release. */
    fun fetchLatest(): Release? = check().release

    private class Reachability {
        var responded = false
    }

    private fun latestRelease(apiBase: String, reachability: Reachability): Release? = request("$apiBase/releases/latest", reachability)?.let { body ->
        runCatching { JSONObject(body).toRelease() }.getOrNull()
    }

    private fun firstRelease(apiBase: String, reachability: Reachability): Release? = request("$apiBase/releases?per_page=1", reachability)?.let { body ->
        runCatching { JSONArray(body).optJSONObject(0)?.toRelease() }.getOrNull()
    }

    private fun firstTag(apiBase: String, reachability: Reachability): Release? = request("$apiBase/tags?per_page=1", reachability)?.let { body ->
        val tag = runCatching { JSONArray(body).optJSONObject(0)?.optString("name").orEmpty() }.getOrNull().orEmpty()
        if (tag.isBlank()) null else Release(tag, "$REPO_URL/tree/$tag")
    }

    private fun JSONObject.toRelease(): Release? {
        val tag = optString("tag_name").ifBlank { optString("name") }
        if (tag.isBlank()) return null
        val url = optString("html_url").ifBlank { "$REPO_URL/releases/tag/$tag" }
        return Release(tag, url)
    }

    private fun request(url: String, reachability: Reachability): String? {
        return try {
            val conn = URL(url).openConnection() as HttpURLConnection
            try {
                conn.connectTimeout = TIMEOUT_MS
                conn.readTimeout = TIMEOUT_MS
                conn.setRequestProperty("Accept", "application/vnd.github+json")
                conn.setRequestProperty("User-Agent", "AndWall")
                val code = conn.responseCode
                // Any HTTP answer proves the repository exists and is reachable.
                reachability.responded = true
                if (code == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    null
                }
            } finally {
                conn.disconnect()
            }
        } catch (_: Throwable) {
            null
        }
    }

    fun showDialog(context: Context, release: Release) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.update_available_title)
            .setMessage(context.getString(R.string.update_available_message, release.tag.removePrefix("v")))
            .setPositiveButton(R.string.update_download) { _, _ ->
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(release.url)))
            }
            .setNeutralButton(R.string.update_skip_version) { _, _ ->
                context.getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE).edit()
                    .putString(MainActivity.KEY_SKIPPED_UPDATE, release.tag).apply()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}