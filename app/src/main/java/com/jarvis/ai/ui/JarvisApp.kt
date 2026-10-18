package com.jarvis.ai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jarvis.ai.ui.navigation.JarvisTab
import com.jarvis.ai.ui.screens.home.HomeScreen
import com.jarvis.ai.ui.screens.placeholder.PlaceholderScreen

/** Root of the app: bottom navigation plus the current tab's screen. */
@Composable
fun JarvisApp() {
    var tab by rememberSaveable { mutableStateOf(JarvisTab.HOME) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                JarvisTab.entries.forEach { item ->
                    val selected = tab == item
                    NavigationBarItem(
                        selected = selected,
                        onClick = { tab = item },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline
                                    )
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (tab) {
                JarvisTab.HOME -> HomeScreen(onOpenTab = { tab = it })
                JarvisTab.CHAT -> PlaceholderScreen(
                    "AI Chat",
                    "Gemini chat is built in Stage 3. No AI responses are generated yet."
                )
                JarvisTab.AUTOMATION -> PlaceholderScreen(
                    "Automation",
                    "Device actions and WhatsApp automation are built in Stages 4 and 5."
                )
                JarvisTab.CODING -> PlaceholderScreen(
                    "Coding",
                    "The coding workspace is built in Stage 5."
                )
                JarvisTab.SETTINGS -> PlaceholderScreen(
                    "Settings",
                    "Settings, privacy controls, and data management are built in Stage 6."
                )
            }
        }
    }
}
