package com.interviewmirror.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interviewmirror.app.data.model.EvaluationResponse
import com.interviewmirror.app.data.model.InterviewQuestion
import com.interviewmirror.app.domain.AnswerRecord
import com.interviewmirror.app.domain.HistoryItem
import com.interviewmirror.app.domain.HistoryRepository
import com.interviewmirror.app.domain.InterviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InterviewUiState(
    val category: String = "Android Development",
    val difficulty: String = "Beginner",
    val mode: String = "Quick",
    val loading: Boolean = false,
    val questions: List<InterviewQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val answer: String = "",
    val evaluations: List<EvaluationResponse> = emptyList(),
    val completedAnswers: List<AnswerRecord> = emptyList(),
    val history: List<HistoryItem> = emptyList(),
    val historyLoading: Boolean = false,
    val historySaved: Boolean = false,
    val error: String? = null
) {
    val currentQuestion: InterviewQuestion?
        get() = questions.getOrNull(currentIndex)

    val isComplete: Boolean
        get() = questions.isNotEmpty() && currentIndex >= questions.size

    val overallScore: Int
        get() = if (evaluations.isEmpty()) 0 else (evaluations.map { it.score }.average() * 10).toInt()
}

@HiltViewModel
class InterviewViewModel @Inject constructor(
    private val repository: InterviewRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(InterviewUiState())
    val state: StateFlow<InterviewUiState> = _state

    fun chooseCategory(category: String) {
        _state.update { it.copy(category = category) }
    }

    fun chooseDifficulty(difficulty: String) {
        _state.update { it.copy(difficulty = difficulty) }
    }

    fun chooseMode(mode: String) {
        _state.update { it.copy(mode = mode) }
    }

    fun updateAnswer(answer: String) {
        _state.update { it.copy(answer = answer) }
    }

    fun startInterview() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loading = true,
                    error = null,
                    currentIndex = 0,
                    answer = "",
                    evaluations = emptyList(),
                    completedAnswers = emptyList(),
                    historySaved = false
                )
            }
            runCatching {
                repository.questions(_state.value.category, _state.value.difficulty, _state.value.mode)
            }.onSuccess { questions ->
                _state.update { it.copy(loading = false, questions = questions, answer = "") }
            }.onFailure { error ->
                _state.update { it.copy(loading = false, error = error.message ?: "Could not load questions") }
            }
        }
    }

    fun submitAnswer() {
        val snapshot = _state.value
        val question = snapshot.currentQuestion ?: return
        if (snapshot.answer.isBlank()) {
            _state.update { it.copy(error = "Please type an answer first.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching {
                repository.evaluate(question.text, snapshot.answer, snapshot.category, snapshot.difficulty)
            }.onSuccess { evaluation ->
                val record = AnswerRecord(
                    question = question.text,
                    answer = snapshot.answer,
                    evaluation = evaluation
                )
                var shouldSave = false
                _state.update {
                    val nextAnswers = it.completedAnswers + record
                    val nextIndex = it.currentIndex + 1
                    shouldSave = nextIndex >= it.questions.size
                    it.copy(
                        loading = false,
                        evaluations = it.evaluations + evaluation,
                        completedAnswers = nextAnswers,
                        currentIndex = nextIndex,
                        answer = ""
                    )
                }
                if (shouldSave) saveCurrentSession()
            }.onFailure { error ->
                _state.update { it.copy(loading = false, error = error.message ?: "Could not evaluate answer") }
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _state.update { it.copy(historyLoading = true, error = null) }
            runCatching {
                historyRepository.loadRecentSessions()
            }.onSuccess { items ->
                _state.update { it.copy(historyLoading = false, history = items) }
            }.onFailure { error ->
                _state.update { it.copy(historyLoading = false, error = error.message ?: "Could not load history") }
            }
        }
    }

    private suspend fun saveCurrentSession() {
        val snapshot = _state.value
        if (snapshot.historySaved || snapshot.completedAnswers.isEmpty()) return

        runCatching {
            historyRepository.saveSession(
                category = snapshot.category,
                difficulty = snapshot.difficulty,
                mode = snapshot.mode,
                score = snapshot.overallScore,
                answers = snapshot.completedAnswers
            )
        }.onSuccess {
            _state.update { it.copy(historySaved = true) }
            loadHistory()
        }.onFailure { error ->
            _state.update { it.copy(error = error.message ?: "Interview completed, but history was not saved.") }
        }
    }
}
