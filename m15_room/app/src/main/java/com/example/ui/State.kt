package com.example.ui

sealed class State {
    data object START : State()
    data object SUCCESS : State()
    data class ERROR(val error: String) : State()
}