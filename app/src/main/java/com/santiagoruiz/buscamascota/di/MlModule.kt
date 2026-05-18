package com.santiagoruiz.buscamascota.di

import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provee los clientes de IA on-device. El [ImageLabeler] de ML Kit se crea
 * por fábrica (no tiene constructor inyectable). El extractor TFLite es una
 * clase con `@Inject` y no necesita binding aquí.
 */
@Module
@InstallIn(SingletonComponent::class)
object MlModule {

    @Provides
    @Singleton
    fun provideImageLabeler(): ImageLabeler =
        ImageLabeling.getClient(
            ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.5f)
                .build(),
        )
}
