package com.tycoding.kotlinflows

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var testDispatcher: TestDispatchers

    @Before
    fun setUp() {
        testDispatcher = TestDispatchers()
        viewModel = MainViewModel(testDispatcher)
    }

    @Test
    fun `countDownFlow, properly counts down from 5 to 0`() = runBlocking {
        viewModel.countDownflow.test {
            for (i in 5 downTo 0) {
                testDispatcher.testDispatcher.advanceTimeBy(1000L)
                val emission = awaitItem()
                assertThat(emission).isEqualTo(i)
            }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `squareNumber, number properly squared`() = runBlocking {
        val job = launch {
            viewModel.shareFlow.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(9)
                cancelAndConsumeRemainingEvents()
            }
        }
        viewModel.squareNumber(3)
        job.join()
        job.cancel()
    }
}