package com.bitbenders.theentity.domain.models

/**
 * Round 1 constraints used to manipulate the AI persona safely.
 */
data class PersonaConfig(
    val targetWord: String,
    val forbiddenWords: List<String>,
)

