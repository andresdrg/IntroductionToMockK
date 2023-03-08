package com.example.mockkexample

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

const val DUMMY_MESSAGE = "DUMMY_MESSAGE"

class CalculatorConstructorTest {

    @Before
    fun setup() {
        mockkConstructor(Tracker::class)
        mockkConstructor(Logger::class)

        every { anyConstructed<Logger>().logGetMessage(any()) } returns DUMMY_MESSAGE
        every { anyConstructed<Logger>().logGetLongTag(any()) } returns LONG_DUMMY_MESSAGE

        every { anyConstructed<Tracker>().sendMessage(any()) } just runs
        coEvery { anyConstructed<Tracker>().sendCode(any()) } just runs
    }

    @Test
    fun `Sum calculates the proper result`() {
        runTest {
            val calculator = Calculator(
                tracker = Tracker(),
                logger = Logger()
            )

            val expectedResult = 8

            val result = calculator.sum(4, 4)

            Assert.assertTrue(expectedResult == result)
            verify {
                anyConstructed<Tracker>().sendMessage(
                    withArg { message ->
                        assert(message.text.contains(DUMMY_MESSAGE))
                    }
                )
            }
            coVerify(exactly = 1) { anyConstructed<Tracker>().sendCode(any()) }
        }
    }

    @Test
    fun `Divide calculates the proper result and returns Long Tag`() {
        val calculator = Calculator(
            tracker = Tracker(),
            logger = Logger()
        )

        val expectedResult = 1

        val result = calculator.customDivide(2, 2)

        Assert.assertTrue(expectedResult == result)

        verify(exactly = 1) { anyConstructed<Logger>().logGetLongTag(any()) }
        verify(exactly = 1) {
            anyConstructed<Tracker>().sendMessage(
                withArg { message ->
                    assert(message.text.contains(LONG_DUMMY_MESSAGE))
                }
            )
        }
    }
}