package com.jarvis.ai.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jarvis.ai.domain.model.ActionOutcome
import com.jarvis.ai.domain.model.AssistantStatus
import com.jarvis.ai.ui.components.AiReactor
import com.jarvis.ai.ui.components.HudPanel
import com.jarvis.ai.ui.components.StatusChip
import com.jarvis.ai.ui.components.Waveform
import com.jarvis.ai.ui.components.assistantColor
import com.jarvis.ai.ui.navigation.JarvisTab
import com.jarvis.ai.ui.theme.JarvisAlert
import com.jarvis.ai.ui.theme.JarvisOk
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

private val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")

@Composable
fun HomeScreen(
    onOpenTab: (JarvisTab) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val keyboard = LocalSoftwareKeyboardController.current

    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(1000)
        }
    }

    var input by rememberSaveable { mutableStateOf("") }
    val submit = {
        viewModel.submitCommand(input)
        input = ""
        keyboard?.hide()
    }

    val batteryText = state.device.batteryPercent?.let { level ->
        if (state.device.charging) "$level% (charging)" else "$level%"
    } ?: "Unavailable"
    val networkText = if (state.device.online) "Online" else "Offline"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Logo, assistant status, clock.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "J.A.R.V.I.S.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "ASSISTANT: ${state.assistantStatus.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = now.format(TIME_FORMAT),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = now.format(DATE_FORMAT),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Status tiles: battery, network, AI connection, microphone.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(label = "BATTERY", value = batteryText, modifier = Modifier.weight(1f))
            StatusChip(label = "NETWORK", value = networkText, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(label = "GEMINI", value = "Not configured", modifier = Modifier.weight(1f))
            StatusChip(label = "MICROPHONE", value = "Stage 3", modifier = Modifier.weight(1f))
        }

        // 2. Central reactor.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentAlignment = Alignment.Center
        ) {
            AiReactor(status = state.assistantStatus, modifier = Modifier.size(240.dp))
        }

        Waveform(
            active = state.assistantStatus == AssistantStatus.LISTENING ||
                state.assistantStatus == AssistantStatus.SPEAKING,
            color = assistantColor(state.assistantStatus)
        )

        // 3. Microphone button. Enabled in Stage 3, when SpeechRecognizer is added.
        OutlinedButton(
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voice input: available in Stage 3")
        }

        // 4. Text command input.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                singleLine = true,
                placeholder = { Text("e.g. open youtube, battery") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { submit() })
            )
            Button(onClick = submit, enabled = input.isNotBlank()) {
                Text("Send")
            }
        }

        // 5. Quick actions. The Coding and Settings buttons switch tabs.
        Text(
            text = "QUICK ACTIONS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val actions: List<Pair<String, () -> Unit>> = listOf(
            "Open YouTube" to { viewModel.runQuickAction(QuickAction.OPEN_YOUTUBE) },
            "Play music" to { viewModel.runQuickAction(QuickAction.PLAY_MUSIC) },
            "Open WhatsApp" to { viewModel.runQuickAction(QuickAction.OPEN_WHATSAPP) },
            "Coding assistant" to { onOpenTab(JarvisTab.CODING) },
            "Settings" to { onOpenTab(JarvisTab.SETTINGS) }
        )
        actions.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pair.forEach { (label, onClick) ->
                    OutlinedButton(onClick = onClick, modifier = Modifier.weight(1f)) {
                        Text(text = label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        // 6. Recent operations and the last real result.
        Text(
            text = "RECENT OPERATIONS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (state.history.isEmpty()) {
            Text(
                text = "No operations yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            state.history.forEach { entry ->
                HudPanel {
                    Text(
                        text = "${entry.time}  ${entry.kind.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = kindColor(entry.kind)
                    )
                    Text(
                        text = entry.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

private fun kindColor(kind: ActionOutcome.Kind) = when (kind) {
    ActionOutcome.Kind.SUCCESS -> JarvisOk
    ActionOutcome.Kind.FAILURE, ActionOutcome.Kind.MISSING -> JarvisAlert
    ActionOutcome.Kind.INFO -> Color.Unspecified
}
