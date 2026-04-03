package com.bitbenders.theentity.domain.models

/**
 * Represents one slot of the 4-part Root Eradication Cipher.
 */
data class CipherChunk(
    val id: Int,
    val textValue: String,
    val isLocked: Boolean,
)

