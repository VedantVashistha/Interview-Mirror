package com.interviewmirror.app.data.remote

import com.interviewmirror.app.data.model.AnalyticsResponse
import com.interviewmirror.app.data.model.EvaluationRequest
import com.interviewmirror.app.data.model.EvaluationResponse
import com.interviewmirror.app.data.model.QuestionRequest
import com.interviewmirror.app.data.model.QuestionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InterviewApi {
    @POST("api/interviews/questions")
    suspend fun generateQuestions(@Body request: QuestionRequest): QuestionResponse

    @POST("api/evaluations")
    suspend fun evaluateAnswer(@Body request: EvaluationRequest): EvaluationResponse

    @GET("api/analytics/{userId}")
    suspend fun analytics(@Path("userId") userId: String): AnalyticsResponse
}
