package com.bitbenders.theentity.domain.usecases

import com.bitbenders.theentity.domain.models.P2HardwareAction
import com.bitbenders.theentity.domain.repository.IMultiplayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ResolveAnomalyUseCase @Inject constructor(
    private val multiplayerRepository: IMultiplayerRepository,
) {
    /**
     * Maps incoming P2 dial turns to a normalized static intensity value [0f..1f].
     */
    fun observeStaticIntensity(): Flow<Float> {
        return multiplayerRepository.incomingHardwareActions
            .filterIsInstance<P2HardwareAction.DialTurn>()
            .map { action -> action.value.coerceIn(0f, 1f) }
    }

    fun observeKeypadSymbols(): Flow<String> {
        return multiplayerRepository.incomingHardwareActions
            .filterIsInstance<P2HardwareAction.KeypadPress>()
            .map { action -> action.symbol }
    }
}
