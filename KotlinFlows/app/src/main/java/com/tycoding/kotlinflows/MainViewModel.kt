package com.tycoding.kotlinflows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    val countDownflow = flow<Int> {
        val startingValue = 5
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }.flowOn(dispatchers.main)

    /**
     * 위에 같은 flow는 cold flow라고 한다.
     * Cold Flow❄️는
     * Flow 를 수집하는 각각의 Collector 들이
     * 데이터를 수집 할 때마다 새로운 데이터 스트림을 생성하므로
     * Collector 들은 각각의 개별적인 데이터 스트림에서 데이터를 수집한다.
     * Collector가 없다면 작동하지 않는다.
     * */

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    private val _shareFlow = MutableSharedFlow<Int>(5)
    val shareFlow = _shareFlow.asSharedFlow()

    /**
     * Hot Flow🔥 는
     * Flow 를 수집하는 각각의 Collector 들이
     * 데이터 스트림을 공유하여 동일한 데이터를 수집하며 구현에 따라
     * Collector 유/무에 따라 시작되기도 하지만
     * 기본적으로 Collector 가 없어도
     * 데이터 제공자(Provider)는 스트림 데이터를 제공합니다.
     * */

    init {
//        collectFlow()
        squareNumber(3)
        viewModelScope.launch(dispatchers.main) {
            shareFlow.collect {
                delay(2000L)
                println("FIRST FLOW: The received number id $it")
            }
        }

        viewModelScope.launch(dispatchers.main) {
            shareFlow.collect {
                delay(3000L)
                println("SECOND FLOW: The received number id $it")
            }
        }
    }

    fun incrementCounter() {
        _stateFlow.value += 1
    }

    fun squareNumber(number: Int) {
        viewModelScope.launch(dispatchers.main) {
            _shareFlow.emit(number * number)
        }
        /**
         * 그냥 emit 할 경우 오류 발생
         */
//        _shareFlow.emit(number * number)
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

//    private fun collectFlow() {
//        viewModelScope.launch {
//            countDownflow.collectLatest { time ->
//                delay(1500L)
//                println("The current time is $time")
//            }
//        }
//    }
    /**
     * The current time is 0
     *
     * collectLatest의 경우 가장 최근에 수집한 값을 수집한다.
     *
     * 즉, 처리 도중 새로운 데이터가 들어오면
     * 이전 데이터의 처리는 강제 종료 시키고
     * 새로운 데이터를 처리한다.
     * */

//    private fun collectFlow() {
//        viewModelScope.launch {
//            val count = countDownflow
//                .filter { time ->
//                    time % 2 == 0
//                }
//                .map { time ->
//                    time * time
//                }
//                .onEach { time ->
//                    println(time)
//                }
//                .collect { time ->
//                    println("The current time is $time")
//                }
//        }
//    }

    /**
     * 중간 연산자!!
     *
     * filter는 filter에서 조건이 만족하는 값을 반환한다.
     * map은 중간 연산자로 원하는 값으로 매핑하여 반환한다.
     * onEach는 모든 데이터마다 연산 수행
     */

//    private fun collectFlow() {
//        viewModelScope.launch {
//            val reduceResult = countDownflow
////                .reduce { accumulator, value ->
////                    accumulator + value
////                }
//                .fold(100) { accumulator, value ->
//                    accumulator + value
//                }
//            println("The current time is $reduceResult")
//        }
//    }

    /**
     * 종단 연산자!!
     *
     * reduce는 첫번째 원소부터 주어진 operation을 이용하여 누적시키면서 최종값을 반환한다.
     * fold는 초기값 (위에서는 100)을 입력받아 reduce와 동인하게 작동한다.
     *
     */

//    private fun collectFlow() {
//        val flow1 = flow {
//            emit(1)
//            delay(500L)
//            emit(2)
//        }
//        viewModelScope.launch {
//            flow1.flatMapConcat { value ->
//                flow {
//                    emit(value  + 1)
//                    delay(500L)
//                    emit(value + 2)
//                }
//            }.collect { value ->
//                println("The value is $value")
//            }
//        }
//    }

    /**
     * The value is 2 (flow1에서 emit(1)이 반환되고 flow1 안에 flow에서 emit(value  + 1) 로 1+1이 되서 2가 출력
     * The value is 3 (flow1안에 flow에 아직 있으므로 0.5초 후 flow1 안에 flow에서 emit(value  + 2) 로 1+2이 되서 3가 출력
     * The value is 3 (flow1에서 emit(1)이 끝나고 0.5초 후 emit(2)가 반횐되고 flow1 안에 flow에서 다시 emit(value  + 1) 로 2+1이 되서 3가 출력
     * The value is 4 (flow1안에 flow에 아직 있으므로 0.5초 후 flow1 안에 flow에서 emit(value  + 2) 로 2+2이 되서 4가 출력
     */

//    private fun collectFlow() {
//        val flow1 = flow {
//            delay(250L)
//            emit("Appetizer")
//            delay(1000L)
//            emit("Main dish")
//            delay(100L)
//            emit("Desert")
//        }
//        viewModelScope.launch {
//            flow1.onEach {
//                println("FLOW: $it is delivered")
//            }
//                .collect {
//                    println("FLOW: Now eating $it")
//                    delay(1500L)
//                    println("FLOW: Finished eating $it")
//                }
//        }
//    }

    /**
     * FLOW: Appetizer is delivered
     * FLOW: Now eating Appetizer
     * FLOW: Finished eating Appetizer
     * FLOW: Main dish is delivered
     * FLOW: Now eating Main dish
     * FLOW: Finished eating Main dish
     * FLOW: Desert is delivered
     * FLOW: Now eating Desert
     * FLOW: Finished eating Desert
     */

//    private fun collectFlow() {
//        val flow1 = flow {
//            delay(250L)
//            emit("Appetizer")
//            delay(1000L)
//            emit("Main dish")
//            delay(100L)
//            emit("Desert")
//        }
//        viewModelScope.launch {
//            flow1.onEach {
//                println("FLOW: $it is delivered")
//            }
//                .buffer()
//                .collect {
//                    println("FLOW: Now eating $it")
//                    delay(1500L)
//                    println("FLOW: Finished eating $it")
//                }
//        }
//    }

    /**
     * buffer는 발행과 소비를 분리시키므로 순차적으로 기다리는게 아닌 바로바로 발행한다.
     * FLOW: Appetizer is delivered
     * FLOW: Now eating Appetizer
     * FLOW: Main dish is delivered
     * FLOW: Desert is delivered
     * FLOW: Finished eating Appetizer
     * FLOW: Now eating Main dish
     * FLOW: Finished eating Main dish
     * FLOW: Now eating Desert
     * FLOW: Finished eating Desert
     */

    private fun collectFlow() {
        val flow1 = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Desert")
        }
        viewModelScope.launch(dispatchers.main) {
            flow1.onEach {
                println("FLOW: $it is delivered")
            }
                .conflate()
                .collectLatest {
                    println("FLOW: Now eating $it")
                    delay(1500L)
                    println("FLOW: Finished eating $it")
                }
        }
    }

    /**
     * conflate는 buffer와 같이 소비와 발행이 개별이지만 소비가 되기 전에 발행이 되면 해당 소비는 건너뛰고 발행을 한다.
     * FLOW: Appetizer is delivered
     * FLOW: Now eating Appetizer
     * FLOW: Main dish is delivered
     * FLOW: Desert is delivered
     * FLOW: Finished eating Appetizer
     * FLOW: Now eating Desert
     * FLOW: Finished eating Desert
     *
     * 여기에 collectLatest를 사용하면
     * FLOW: Appetizer is delivered
     * FLOW: Now eating Appetizer
     * FLOW: Main dish is delivered
     * FLOW: Now eating Main dish
     * FLOW: Desert is delivered
     * FLOW: Now eating Desert
     * FLOW: Finished eating Desert
     */
}