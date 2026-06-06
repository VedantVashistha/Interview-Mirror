package com.interviewmirror.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val signedIn: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun updateEmail(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }

    fun updatePassword(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun login() {
        val snapshot = _state.value
        if (!validate(snapshot)) return

        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching {
                auth.signInWithEmailAndPassword(snapshot.email.trim(), snapshot.password).await()
            }.onSuccess {
                _state.update { it.copy(loading = false, signedIn = true) }
            }.onFailure { error ->
                _state.update { it.copy(loading = false, error = error.message ?: "Login failed") }
            }
        }
    }

    fun createAccount() {
        val snapshot = _state.value
        if (!validate(snapshot)) return

        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching {
                auth.createUserWithEmailAndPassword(snapshot.email.trim(), snapshot.password).await()
            }.onSuccess {
                _state.update { it.copy(loading = false, signedIn = true) }
            }.onFailure { error ->
                _state.update { it.copy(loading = false, error = error.message ?: "Sign up failed") }
            }
        }
    }

    fun useDemoAccount() {
        _state.update { it.copy(signedIn = true) }
    }

    private fun validate(state: AuthUiState): Boolean {
        if (!state.email.contains("@")) {
            _state.update { it.copy(error = "Enter a valid email address.") }
            return false
        }
        if (state.password.length < 6) {
            _state.update { it.copy(error = "Password must be at least 6 characters.") }
            return false
        }
        return true
    }
}
