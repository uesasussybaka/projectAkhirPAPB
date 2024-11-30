package com.example.papb

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class DatabaseViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun addData(detail: String, description: String, status: String, onResult: (Boolean) -> Unit) {
        val data = hashMapOf(
            "detail" to detail,
            "description" to description,
            "status" to status
        )
        db.collection("tasks")
            .add(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun fetchData(onResult: (List<Map<String, String>>) -> Unit) {
        db.collection("tasks")
            .get()
            .addOnSuccessListener { result ->
                val dataList = result.documents.mapNotNull { document ->
                    val data = document.data as? Map<String, String>
                    data?.toMutableMap()?.apply {
                        this["id"] = document.id // Tambahkan documentId
                    }
                }
                onResult(dataList)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }


    fun updateData(documentId: String, updatedData: Map<String, String>, onResult: (Boolean) -> Unit) {
        db.collection("tasks").document(documentId)
            .set(updatedData)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteData(documentId: String, onResult: (Boolean) -> Unit) {
        db.collection("tasks").document(documentId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
