package com.jarvis.ai.domain.model

/**
 * The real result of a device action. Only report SUCCESS when the Android
 * call actually returned without an error. Never invent a success message.
 */
data class ActionOutcome(
    val kind: Kind,
    val message: String
) {
    enum class Kind { SUCCESS, MISSING, FAILURE, INFO }
}
