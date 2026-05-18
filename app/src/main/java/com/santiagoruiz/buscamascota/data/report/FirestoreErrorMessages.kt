package com.santiagoruiz.buscamascota.data.report

import com.santiagoruiz.buscamascota.data.common.toFirestoreErrorMessage

/**
 * Alias histórico para la traducción de errores de Firestore en la feature
 * de reportes. La lógica se centralizó en
 * [com.santiagoruiz.buscamascota.data.common.toFirestoreErrorMessage] al
 * añadir el perfil (Fase 7), que la comparte; este punto de entrada se
 * mantiene para no tocar las llamadas existentes de reportes.
 */
internal fun Throwable.toReportErrorMessage(): String = toFirestoreErrorMessage()
