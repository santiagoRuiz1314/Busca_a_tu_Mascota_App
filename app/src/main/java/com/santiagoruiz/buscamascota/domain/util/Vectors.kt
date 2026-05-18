package com.santiagoruiz.buscamascota.domain.util

import kotlin.math.sqrt

/**
 * Operaciones vectoriales para el matching visual (Fase 6). Kotlin puro:
 * el dominio no depende de Android ni de TFLite.
 */
object Vectors {

    /**
     * Devuelve una copia del vector con norma L2 = 1. Si el vector es nulo
     * (todo ceros), se devuelve igual. Normalizar los embeddings hace que la
     * similitud coseno se reduzca a un producto punto.
     */
    fun l2Normalize(vector: FloatArray): FloatArray {
        var sum = 0.0
        for (v in vector) sum += v.toDouble() * v
        val norm = sqrt(sum)
        if (norm == 0.0) return vector.copyOf()
        return FloatArray(vector.size) { (vector[it] / norm).toFloat() }
    }

    /**
     * Similitud coseno en [-1, 1] entre dos embeddings del mismo tamaño.
     * Robusta aunque los vectores no estén normalizados; devuelve 0 si los
     * tamaños no coinciden o alguno es nulo.
     */
    fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        if (a.size != b.size || a.isEmpty()) return 0f
        var dot = 0.0
        var normA = 0.0
        var normB = 0.0
        for (i in a.indices) {
            val x = a[i].toDouble()
            val y = b[i].toDouble()
            dot += x * y
            normA += x * x
            normB += y * y
        }
        if (normA == 0.0 || normB == 0.0) return 0f
        return (dot / (sqrt(normA) * sqrt(normB))).toFloat()
    }
}
