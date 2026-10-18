package com.jarvis.ai.device

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.jarvis.ai.domain.model.ActionOutcome

/**
 * Launches installed apps and URLs through supported Android APIs.
 * Every method returns the real outcome. Missing apps are reported, not faked.
 */
object AppLauncher {

    const val YOUTUBE_PACKAGE = "com.google.android.youtube"
    const val WHATSAPP_PACKAGE = "com.whatsapp"

    fun launchPackage(context: Context, packageName: String, label: String): ActionOutcome {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return ActionOutcome(ActionOutcome.Kind.MISSING, "$label is not installed on this device.")
        return start(context, intent, "Opened $label.")
    }

    /** Opens the YouTube app if installed, otherwise youtube.com in the browser. */
    fun openYouTube(context: Context): ActionOutcome {
        val hasApp = context.packageManager.getLaunchIntentForPackage(YOUTUBE_PACKAGE) != null
        return if (hasApp) {
            launchPackage(context, YOUTUBE_PACKAGE, "YouTube")
        } else {
            openUrl(
                context,
                "https://www.youtube.com",
                "YouTube app not found, so I opened youtube.com in the browser."
            )
        }
    }

    /** Opens a YouTube search. Playback itself is controlled by YouTube, not by JARVIS. */
    fun openYouTubeSearch(context: Context, query: String): ActionOutcome {
        val url = "https://www.youtube.com/results?search_query=" + Uri.encode(query)
        return openUrl(
            context,
            url,
            "Opened YouTube search for \"$query\". Playback is controlled by YouTube."
        )
    }

    fun openSystemSettings(context: Context): ActionOutcome =
        start(context, Intent(Settings.ACTION_SETTINGS), "Opened phone Settings.")

    fun openUrl(context: Context, url: String, successMessage: String): ActionOutcome =
        start(context, Intent(Intent.ACTION_VIEW, Uri.parse(url)), successMessage)

    private fun start(context: Context, intent: Intent, successMessage: String): ActionOutcome =
        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ActionOutcome(ActionOutcome.Kind.SUCCESS, successMessage)
        } catch (e: ActivityNotFoundException) {
            ActionOutcome(ActionOutcome.Kind.FAILURE, "No app on this device can handle that action.")
        } catch (e: SecurityException) {
            ActionOutcome(ActionOutcome.Kind.FAILURE, "Android blocked that action.")
        }
}
