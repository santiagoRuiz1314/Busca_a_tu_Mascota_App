package com.santiagoruiz.buscamascota.data.report

import com.google.firebase.firestore.FirebaseFirestoreException

/**
 * Traduce excepciones de Firestore a mensajes en español listos para mostrar.
 * Centralizado para que creación, lectura y feed reporten la causa real en
 * vez de un genérico "revisa tu conexión" que ocultaba PERMISSION_DENIED
 * (reglas) o FAILED_PRECONDITION (índice compuesto faltante).
 */
internal fun Throwable.toReportErrorMessage(): String =
    if (this is FirebaseFirestoreException) when (code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED ->
            "No tienes permiso para esta acción. Falta publicar las reglas " +
                "de seguridad de Firestore (ver firestore.rules)."
        FirebaseFirestoreException.Code.FAILED_PRECONDITION ->
            "Falta crear un índice en Firestore para esta consulta. El " +
                "enlace para crearlo aparece en logcat (tag «Firestore»)."
        FirebaseFirestoreException.Code.UNAVAILABLE ->
            "Sin conexión con el servidor. Revisa tu internet e intenta de nuevo."
        FirebaseFirestoreException.Code.UNAUTHENTICATED ->
            "Tu sesión expiró. Vuelve a iniciar sesión."
        FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED ->
            "Se alcanzó un límite del plan gratuito. Intenta más tarde."
        FirebaseFirestoreException.Code.INVALID_ARGUMENT ->
            "Los datos no son válidos (¿la foto es muy pesada?)."
        FirebaseFirestoreException.Code.NOT_FOUND ->
            "El elemento ya no está disponible."
        else -> "No se pudo completar la operación. Intenta de nuevo."
    } else {
        "No se pudo completar la operación. Revisa tu conexión."
    }
