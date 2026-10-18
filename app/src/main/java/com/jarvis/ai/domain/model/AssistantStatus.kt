package com.jarvis.ai.domain.model

/** Visible assistant state. Drives the reactor animation and status text. */
enum class AssistantStatus {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    ERROR
}
