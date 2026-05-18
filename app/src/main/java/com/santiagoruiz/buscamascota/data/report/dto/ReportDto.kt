package com.santiagoruiz.buscamascota.data.report.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Representación del reporte en Firestore (colección `reports`). POJO con
 * constructor sin argumentos (valores por defecto) y tipos serializables.
 * Nunca se expone fuera de la capa data.
 */
data class ReportDto(
    @DocumentId val id: String = "",
    val type: String = "",
    val status: String = "",
    val ownerId: String = "",
    val species: String = "",
    val breed: String? = null,
    val color: String? = null,
    val animalName: String? = null,
    val description: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val geohash: String = "",
    val photoBase64: String? = null,
    // Solo se llena en LOST/SIGHTING a partir de la Fase 6 (IA).
    val embedding: List<Double>? = null,
    @ServerTimestamp val createdAt: Date? = null,
)
