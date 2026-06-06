package com.interviewmirror.app.domain

import com.interviewmirror.app.data.model.EvaluationRequest
import com.interviewmirror.app.data.model.EvaluationResponse
import com.interviewmirror.app.data.model.InterviewQuestion
import com.interviewmirror.app.data.model.QuestionRequest
import com.interviewmirror.app.data.remote.InterviewApi
import javax.inject.Inject

class InterviewRepository @Inject constructor(
    private val api: InterviewApi
) {
    suspend fun questions(category: String, difficulty: String, mode: String): List<InterviewQuestion> {
        return api.generateQuestions(
            QuestionRequest(category = category, difficulty = difficulty, mode = mode)
        ).questions
    }

    suspend fun evaluate(
        question: String,
        answer: String,
        category: String,
        difficulty: String
    ): EvaluationResponse {
        return api.evaluateAnswer(
            EvaluationRequest(
                question = question,
                answer = answer,
                category = category,
                difficulty = difficulty
            )
        )
    }
}
