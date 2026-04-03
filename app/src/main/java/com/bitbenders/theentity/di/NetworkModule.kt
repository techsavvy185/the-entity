package com.bitbenders.theentity.di

import com.bitbenders.theentity.data.remote.api.EntityBackendApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Base URL for the local HTTP relay (server.js).
     *
     * During development the relay runs on the host machine.
     * 10.0.2.2 is the Android emulator alias for the host's localhost.
     * For a physical device on the same network, swap this for the host's LAN IP.
     */
    private const val BASE_URL = "http://10.0.2.2:3000/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideEntityBackendApi(retrofit: Retrofit): EntityBackendApi =
        retrofit.create(EntityBackendApi::class.java)
}
