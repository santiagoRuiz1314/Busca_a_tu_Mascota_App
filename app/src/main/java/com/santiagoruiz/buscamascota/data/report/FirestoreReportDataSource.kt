package com.santiagoruiz.buscamascota.data.report

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.santiagoruiz.buscamascota.data.report.dto.ReportDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    /**
     * Escucha en tiempo real los reportes con el [status] dado, ordenados
     * del más reciente al más antiguo. Requiere un índice compuesto
     * (status + createdAt) que Firestore sugiere por enlace en logcat.
     */
    fun observeByStatus(status: String): Flow<List<ReportDto>> = callbackFlow {
        val registration = reports
            .whereEqualTo("status", status)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(ReportDto::class.java))
                }
            }
        awaitClose { registration.remove() }
    }

    /** Lee un reporte por id, o `null` si no existe. */
    suspend fun getById(id: String): ReportDto? =
        reports.document(id).get().await().toObject(ReportDto::class.java)

    private companion object {
        const val COLLECTION = "reports"
    }
}
