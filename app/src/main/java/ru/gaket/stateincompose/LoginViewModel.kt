package ru.gaket.stateincompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiModel(
    val input: String,
    val isError: Boolean
)

class LoginViewModel : ViewModel() {

    private val validator = BasicEmailValidator()
    private val _uiState = MutableStateFlow(LoginUiModel("", false))
    val uiState: StateFlow<LoginUiModel> = _uiState.asStateFlow()

    fun onEmailInputChanged(input: String) {
        viewModelScope.launch {
            _uiState.emit(LoginUiModel(input, !validator.validate(input)))
        }
    }
}

class BasicEmailValidator {
    fun validate(email: String): Boolean {
        return email.contains("@")
    }
}
