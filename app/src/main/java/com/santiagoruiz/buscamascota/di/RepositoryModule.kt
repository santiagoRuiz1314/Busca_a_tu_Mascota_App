package com.santiagoruiz.buscamascota.di

import com.santiagoruiz.buscamascota.data.auth.AuthRepositoryImpl
import com.santiagoruiz.buscamascota.data.image.PhotoEncoderImpl
import com.santiagoruiz.buscamascota.data.location.LocationRepositoryImpl
import com.santiagoruiz.buscamascota.data.ml.PhotoAnalyzerImpl
import com.santiagoruiz.buscamascota.data.report.ReportRepositoryImpl
import com.santiagoruiz.buscamascota.data.user.UserRepositoryImpl
import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import com.santiagoruiz.buscamascota.domain.repository.LocationRepository
import com.santiagoruiz.buscamascota.domain.repository.PhotoAnalyzer
import com.santiagoruiz.buscamascota.domain.repository.PhotoEncoder
import com.santiagoruiz.buscamascota.domain.repository.ReportRepository
import com.santiagoruiz.buscamascota.domain.repository.UserRepository
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

    @Binds
    @Singleton
    abstract fun bindReportRepository(impl: ReportRepositoryImpl): ReportRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindPhotoEncoder(impl: PhotoEncoderImpl): PhotoEncoder

    @Binds
    @Singleton
    abstract fun bindPhotoAnalyzer(impl: PhotoAnalyzerImpl): PhotoAnalyzer

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
