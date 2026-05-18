package com.santiagoruiz.buscamascota.di

import com.santiagoruiz.buscamascota.data.auth.AuthRepositoryImpl
import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Vincula las implementaciones de la capa data con las interfaces de
 * dominio. Se irán añadiendo bindings (reportes, usuarios) en cada fase.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
