package com.tycoding.kotlinflows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val countDownflow = flow<Int> {
        val startingValue = 10
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    /**
     * 위에 같은 flow는 cold flow라고 한다.
     * Cold Flow❄️는
     * Flow 를 수집하는 각각의 Collector 들이
     * 데이터를 수집 할 때마다 새로운 데이터 스트림을 생성하므로
     * Collector 들은 각각의 개별적인 데이터 스트림에서 데이터를 수집한다.
     * */

    init {
        collectFlow()
    }

//    private fun collectFlow() {
//        viewModelScope.launch {
//            countDownflow.collect { time ->
//                println("The current time is $time")
//            }
//        }
//    }
    /**
     * The current time is 10
     * The current time is 9
     * The current time is 8
     * The current time is 7
     * The current time is 6
     * The current time is 5
     * The current time is 4
     * The current time is 3
     * The current time is 2
     * The current time is 1
     * The current time is 0
     * */

    private fun collectFlow() {
        viewModelScope.launch {
            countDownflow.collectLatest { time ->
                delay(1500L)
                println("The current time is $time")
            }
        }
    }
    /**
     * The current time is 0
     *
     * collectLatest의 경우 가장 최근에 수집한 값을 수집한다.
     *
     * 즉, 처리 도중 새로운 데이터가 들어오면
     * 이전 데이터의 처리는 강제 종료 시키고
     * 새로운 데이터를 처리한다.
     * */
}