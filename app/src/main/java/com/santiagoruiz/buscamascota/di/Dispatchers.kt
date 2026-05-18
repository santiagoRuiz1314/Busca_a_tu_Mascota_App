package com.santiagoruiz.buscamascota.di

import javax.inject.Qualifier

/** Dispatcher para operaciones de E/S (red, disco, Firebase). */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

/** Dispatcher para trabajo intensivo de CPU (procesamiento de imágenes, IA). */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher
