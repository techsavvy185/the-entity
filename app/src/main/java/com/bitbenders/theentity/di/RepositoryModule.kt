package com.bitbenders.theentity.di

import com.bitbenders.theentity.data.repository.EntityBackendRepositoryImpl
import com.bitbenders.theentity.data.repository.GameEngineRepositoryImpl
import com.bitbenders.theentity.data.repository.MultiplayerRepositoryImpl
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IGameEngineRepository
import com.bitbenders.theentity.domain.repository.IMultiplayerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEntityBackendRepository(
        entityBackendRepositoryImpl: EntityBackendRepositoryImpl
    ): IEntityBackendRepository

    @Binds
    @Singleton
    abstract fun bindGameEngineRepository(
        gameEngineRepositoryImpl: GameEngineRepositoryImpl
    ): IGameEngineRepository

    @Binds
    @Singleton
    abstract fun bindMultiplayerRepository(
        multiplayerRepositoryImpl: MultiplayerRepositoryImpl
    ): IMultiplayerRepository
}
