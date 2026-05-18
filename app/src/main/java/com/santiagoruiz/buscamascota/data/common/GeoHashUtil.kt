package com.santiagoruiz.buscamascota.data.common

/**
 * Codificación geohash (base32) para indexar reportes por zona en Firestore.
 * Precisión 7 ≈ ~150 m, suficiente para filtrar por barrio antes del
 * matching visual (Fase 6).
 */
object GeoHashUtil {

    private const val BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz"
    const val DEFAULT_PRECISION = 7

    fun encode(
        latitude: Double,
        longitude: Double,
        precision: Int = DEFAULT_PRECISION,
    ): String {
        var latMin = -90.0
        var latMax = 90.0
        var lonMin = -180.0
        var lonMax = 180.0

        val geohash = StringBuilder()
        var bit = 0
        var ch = 0
        var even = true

        while (geohash.length < precision) {
            if (even) {
                val mid = (lonMin + lonMax) / 2
                if (longitude >= mid) {
                    ch = ch or (1 shl (4 - bit))
                    lonMin = mid
                } else {
                    lonMax = mid
                }
            } else {
                val mid = (latMin + latMax) / 2
                if (latitude >= mid) {
                    ch = ch or (1 shl (4 - bit))
                    latMin = mid
                } else {
                    latMax = mid
                }
            }
            even = !even
            if (bit < 4) {
                bit++
            } else {
                geohash.append(BASE32[ch])
                bit = 0
                ch = 0
            }
        }
        return geohash.toString()
    }
}
