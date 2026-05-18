package com.santiagoruiz.buscamascota.data.report

import com.google.firebase.firestore.FirebaseFirestore
import com.santiagoruiz.buscamascota.data.report.dto.ReportDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/** Acceso a la colección `reports` de Firestore. */
@Singleton
class FirestoreReportDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val reports get() = firestore.collection(COLLECTION)

    /** Crea el documento y devuelve el id generado por Firestore. */
    suspend fun create(dto: ReportDto): String {
        val ref = reports.add(dto).await()
        return ref.id
    }

    private companion object {
        const val COLLECTION = "reports"
    }
}
