package com.interviewmirror.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class QuestionRequest(
    val category: String,
    val difficulty: String,
    val mode: String,
    val userId: String = "demo-user",
    val recentQuestionIds: List<String> = emptyList()
)

@Serializable
data class QuestionResponse(val questions: List<InterviewQuestion>)

@Serializable
data class InterviewQuestion(
    val id: String,
    val category: String,
    val difficulty: String,
    val type: String,
    val text: String
)

@Serializable
data class EvaluationRequest(
    val question: String,
    val answer: String,
    val category: String,
    val difficulty: String
)

@Serializable
data class EvaluationResponse(
    val score: Double,
    val correctness: String,
    val technicalDepth: String,
    val communicationClarity: String,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val missingConcepts: List<String>,
    val suggestedAnswer: String,
    val difficulty: String? = null
)

@Serializable
data class AnalyticsResponse(
    val userId: String,
    val overallScore: Int,
    val practiceCount: Int,
    val streak: Int,
    val breakdown: Map<String, Int>,
    val weakAreas: List<String>,
    val summary: String
)
