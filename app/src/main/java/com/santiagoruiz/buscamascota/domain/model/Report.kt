package com.santiagoruiz.buscamascota.domain.model

/**
 * Reporte ciudadano sobre un animal. El geohash es un detalle de
 * almacenamiento/índice y se calcula en la capa data a partir de
 * [location]. `embedding` queda nulo hasta la Fase 6 (IA on-device).
 *
 * La foto se guarda como JPEG comprimido en base64 dentro del documento
 * (plan Spark, sin Firebase Storage).
 */
data class Report(
    val id: String = "",
    val type: ReportType,
    val status: ReportStatus = ReportStatus.OPEN,
    val ownerId: String = "",
    val animal: AnimalInfo,
    val description: String,
    val location: GeoPoint,
    val photoBase64: String? = null,
    val embedding: FloatArray? = null,
    val createdAt: Long = 0L,
) {
    // data class con FloatArray: equals/hashCode generados comparan la
    // referencia del array; se sobreescriben para comparar por contenido.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Report) return false
        return id == other.id &&
            type == other.type &&
            status == other.status &&
            ownerId == other.ownerId &&
            animal == other.animal &&
            description == other.description &&
            location == other.location &&
            photoBase64 == other.photoBase64 &&
            (embedding?.contentEquals(other.embedding) ?: (other.embedding == null)) &&
            createdAt == other.createdAt
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + ownerId.hashCode()
        result = 31 * result + animal.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + (photoBase64?.hashCode() ?: 0)
        result = 31 * result + (embedding?.contentHashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        return result
    }
}
