package com.bitbenders.theentity.di

import com.bitbenders.theentity.data.repository.EntityBackendRepositoryImpl
import com.bitbenders.theentity.data.repository.GameEngineRepositoryImpl
import com.bitbenders.theentity.data.repository.MultiplayerRepositoryImpl
import com.bitbenders.theentity.data.repository.mock.MockEntityBackendRepository
import com.bitbenders.theentity.data.repository.mock.MockMultiplayerRepository
import com.bitbenders.theentity.data.remote.api.EntityBackendApi
import com.bitbenders.theentity.data.remote.spacetime.SpaceTimeDbClient
import com.bitbenders.theentity.data.remote.spacetime.mock.MockSpaceTimeDbClient
import com.bitbenders.theentity.domain.repository.IEntityBackendRepository
import com.bitbenders.theentity.domain.repository.IGameEngineRepository
import com.bitbenders.theentity.domain.repository.IMultiplayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideEntityBackendRepository(
        api: EntityBackendApi,
    ): IEntityBackendRepository {
        return if (MockConfig.USE_MOCK) {
            MockEntityBackendRepository()
        } else {
            EntityBackendRepositoryImpl(api)
        }
    }

    @Provides
    @Singleton
    fun provideGameEngineRepository(): IGameEngineRepository {
        return GameEngineRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideSpaceTimeDbClient(): SpaceTimeDbClient {
        return SpaceTimeDbClient()
    }

    @Provides
    @Singleton
    fun provideMultiplayerRepository(spaceTimeDbClient: SpaceTimeDbClient): IMultiplayerRepository {
        return if (MockConfig.USE_MOCK) {
            MockMultiplayerRepository(MockSpaceTimeDbClient())
        } else {
            MultiplayerRepositoryImpl(spaceTimeDbClient)
        }
    }
}
