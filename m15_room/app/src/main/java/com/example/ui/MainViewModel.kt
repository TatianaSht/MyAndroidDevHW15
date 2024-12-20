package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.Word
import com.example.database.WordsDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val wordsDao: WordsDao) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.START)
    val state = _state.asStateFlow()

    private val regexFilterNumberOfCharacters = """^.{3,}${'$'}"""
    private val regexFilterSymbols = """^[А-Яа-я\-]+$|^[A-Za-z\-]+$"""
    private val errorNumberOfCharacters = "The number of characters must be > 3"
    private val errorFilterSymbols = "Only letters and hyphens are allowed"

    fun inputText(text: CharSequence?) {
        _state.value = when {
            text.isNullOrEmpty() -> State.START
            !text.matches(Regex(regexFilterNumberOfCharacters)) -> State.ERROR(
                errorNumberOfCharacters
            )

            text.matches(Regex(regexFilterSymbols)) -> State.SUCCESS
            else -> State.ERROR(errorFilterSymbols)
        }
    }

    val allWords = this.wordsDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun onSave(text: String) {
        viewModelScope.launch {
            val existingWord = wordsDao.getWord(text)
            if (wordsDao.getWord(text) != null) {
                wordsDao.updateCount(existingWord.word, existingWord.count + 1)
            } else {
                wordsDao.addWord(Word(text, 1))
            }
        }
    }

    fun onDelete() {
        viewModelScope.launch {
            wordsDao.delete()
            _state.emit(State.START)
        }
    }
}