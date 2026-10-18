package com.jarvis.ai.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.ai.device.AppLauncher
import com.jarvis.ai.device.DeviceStatus
import com.jarvis.ai.domain.model.ActionOutcome
import com.jarvis.ai.domain.model.AssistantStatus
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class QuickAction { OPEN_YOUTUBE, PLAY_MUSIC, OPEN_WHATSAPP }

data class DeviceSnapshot(
    val batteryPercent: Int?,
    val charging: Boolean,
    val online: Boolean
)

data class HistoryEntry(
    val time: String,
    val text: String,
    val kind: ActionOutcome.Kind
)

data class HomeUiState(
    val assistantStatus: AssistantStatus = AssistantStatus.IDLE,
    val device: DeviceSnapshot = DeviceSnapshot(null, charging = false, online = false),
    val history: List<HistoryEntry> = emptyList(),
    val lastMessage: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")

    init {
        viewModelScope.launch {
            while (isActive) {
                refreshDevice()
                delay(30_000)
            }
        }
    }

    fun refreshDevice() {
        val ctx = getApplication<Application>()
        val snapshot = DeviceSnapshot(
            batteryPercent = DeviceStatus.batteryPercent(ctx),
            charging = DeviceStatus.isCharging(ctx),
            online = DeviceStatus.isOnline(ctx)
        )
        _state.update { it.copy(device = snapshot) }
    }

    fun runQuickAction(action: QuickAction) {
        val ctx = getApplication<Application>()
        val outcome = when (action) {
            QuickAction.OPEN_YOUTUBE -> AppLauncher.openYouTube(ctx)
            QuickAction.PLAY_MUSIC -> AppLauncher.openYouTubeSearch(ctx, "music")
            QuickAction.OPEN_WHATSAPP ->
                AppLauncher.launchPackage(ctx, AppLauncher.WHATSAPP_PACKAGE, "WhatsApp")
        }
        record(outcome)
    }

    /**
     * Stage 2 typed-command handling. Only a small fixed set of local commands
     * is recognized. Free-form text is NOT sent to any AI here, and the app says so.
     * Gemini arrives in Stage 3.
     */
    fun submitCommand(raw: String) {
        val text = raw.trim()
        if (text.isEmpty()) return
        val ctx = getApplication<Application>()
        val lower = text.lowercase()

        val outcome = when {
            lower == "open youtube" -> AppLauncher.openYouTube(ctx)
            lower == "open whatsapp" -> AppLauncher.launchPackage(ctx, AppLauncher.WHATSAPP_PACKAGE, "WhatsApp")
            lower == "open settings" -> AppLauncher.openSystemSettings(ctx)
            lower.startsWith("search youtube for ") -> {
                val query = text.substring("search youtube for ".length).trim()
                if (query.isEmpty()) {
                    ActionOutcome(ActionOutcome.Kind.INFO, "Tell me what to search for on YouTube.")
                } else {
                    AppLauncher.openYouTubeSearch(ctx, query)
                }
            }
            lower == "battery" || lower == "battery level" -> batteryOutcome()
            else -> ActionOutcome(
                ActionOutcome.Kind.INFO,
                "No local command matched. Free-form questions need Gemini, which is not connected yet (Stage 3). " +
                    "Supported now: open youtube, open whatsapp, open settings, search youtube for <text>, battery."
            )
        }
        record(outcome)
    }

    private fun batteryOutcome(): ActionOutcome {
        val level = _state.value.device.batteryPercent
        return if (level == null) {
            ActionOutcome(ActionOutcome.Kind.FAILURE, "Battery level is not available on this device.")
        } else {
            val charging = if (_state.value.device.charging) ", charging" else ""
            ActionOutcome(ActionOutcome.Kind.SUCCESS, "Battery is at $level%$charging.")
        }
    }

    private fun record(outcome: ActionOutcome) {
        val entry = HistoryEntry(
            time = LocalTime.now().format(timeFormat),
            text = outcome.message,
            kind = outcome.kind
        )
        _state.update { current ->
            current.copy(
                assistantStatus = if (outcome.kind == ActionOutcome.Kind.FAILURE) {
                    AssistantStatus.ERROR
                } else {
                    AssistantStatus.IDLE
                },
                history = (listOf(entry) + current.history).take(20),
                lastMessage = outcome.message
            )
        }
    }
}
