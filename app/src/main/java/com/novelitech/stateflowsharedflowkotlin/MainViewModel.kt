package com.novelitech.stateflowsharedflowkotlin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    fun incrementCounter() {
        _stateFlow.value += 1
    }

    // I use SharedFlow when working with One Time Events as Navigate to another screen, to show a Snackbar
    private val _sharedFlow = MutableSharedFlow<Int>(
        replay = 5, // It will cache 5 emittions in the flow when a new collectors receive these emittions.
        // Doing it I can put the execution of the function before the declaration of the collectors
    )
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun squareNumber(number: Int) {
        viewModelScope.launch {
            _sharedFlow.emit(number * number)
        }
    }

    init {

        viewModelScope.launch {
            sharedFlow.collect { // This collect block suspends the corountine
                delay(2000L)
                println("FIRST FLOW: The received number is $it")
            }

            // So it will never reach this point of the code
        }

        viewModelScope.launch {
            sharedFlow.collect {
                delay(3000L)
                println("SECOND FLOW: The received number is $it")
            }
        }

        // To work I need to put the execution of the function AFTER the declaration of the collectors
        // because it's a Hot Flow.
        squareNumber(3)
    }
}