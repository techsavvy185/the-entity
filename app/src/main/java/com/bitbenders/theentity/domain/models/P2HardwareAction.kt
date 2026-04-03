package com.bitbenders.theentity.domain.models

/**
 * Hardware actions emitted by the Operator (P2).
 */
sealed class P2HardwareAction {
    data class DialTurn(val value: Float) : P2HardwareAction()
    data class KeypadPress(val symbol: String) : P2HardwareAction()
}

