package com.interviewmirror.app.domain

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.interviewmirror.app.data.model.EvaluationResponse
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

data class AnswerRecord(
    val question: String,
    val answer: String,
    val evaluation: EvaluationResponse
)

data class HistoryItem(
    val id: String,
    val category: String,
    val difficulty: String,
    val mode: String,
    val score: Int,
    val dateLabel: String,
    val weakAreas: List<String>,
    val completedAtMillis: Long = 0L
)

class HistoryRepository @Inject constructor() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    suspend fun saveSession(
        category: String,
        difficulty: String,
        mode: String,
        score: Int,
        answers: List<AnswerRecord>
    ) {
        val userId = auth.currentUser?.uid ?: "demo-user"
        val weakAreas = answers
            .flatMap { it.evaluation.missingConcepts }
            .filter { it.isNotBlank() }
            .distinct()
            .take(8)

        val answerMaps = answers.map { record ->
            mapOf(
                "question" to record.question,
                "answer" to record.answer,
                "score" to record.evaluation.score,
                "correctness" to record.evaluation.correctness,
                "technicalDepth" to record.evaluation.technicalDepth,
                "communicationClarity" to record.evaluation.communicationClarity,
                "strengths" to record.evaluation.strengths,
                "weaknesses" to record.evaluation.weaknesses,
                "missingConcepts" to record.evaluation.missingConcepts,
                "suggestedAnswer" to record.evaluation.suggestedAnswer
            )
        }

        firestore.collection("interviews").add(
            mapOf(
                "userId" to userId,
                "category" to category,
                "difficulty" to difficulty,
                "mode" to mode,
                "overallScore" to score,
                "weakAreas" to weakAreas,
                "answers" to answerMaps,
                "completedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun loadRecentSessions(): List<HistoryItem> {
        val userId = auth.currentUser?.uid ?: "demo-user"
        val snapshot = firestore.collection("interviews")
            .whereEqualTo("userId", userId)
            .limit(20)
            .get()
            .await()

        return snapshot.documents.map { document ->
            val timestamp = document.getTimestamp("completedAt")
            HistoryItem(
                id = document.id,
                category = document.getString("category") ?: "Interview",
                difficulty = document.getString("difficulty") ?: "Beginner",
                mode = document.getString("mode") ?: "Quick",
                score = document.getLong("overallScore")?.toInt() ?: 0,
                dateLabel = timestamp.toDateLabel(),
                weakAreas = document.get("weakAreas").toStringList(),
                completedAtMillis = timestamp?.toDate()?.time ?: 0L
            )
        }.sortedByDescending { it.completedAtMillis }
    }

    private fun Timestamp?.toDateLabel(): String {
        return if (this == null) "Just now" else dateFormat.format(toDate())
    }

    private fun Any?.toStringList(): List<String> {
        return (this as? List<*>)?.filterIsInstance<String>() ?: emptyList()
    }
}
