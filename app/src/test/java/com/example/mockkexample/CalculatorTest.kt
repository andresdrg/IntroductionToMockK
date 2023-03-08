package com.example.mockkexample

import io.mockk.*
import org.junit.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

const val LONG_DUMMY_MESSAGE = "LONG_DUMMY_MESSAGE"
const val DUMMY_MESSAGE_PREFIX = "DUMMY_PREF:"

@ExperimentalCoroutinesApi
class CalculatorTest {

    private val mockUUID: UUID = mockk()

    private val tracker: Tracker = spyk()
    private val logger: Logger = mockk(relaxed = true)

    private val calculator = Calculator(
        tracker,
        logger
    )
    private val calculatorInterface: CalculatorInterface = mockk {
        coEvery { sum(any(), any()) } returns 0
    }

    @Before
    fun setup() {
        // Mocked Object
        mockkObject(UtilObject)
        every { UtilObject.getCustomMessageUUID() } returns mockUUID

        // Mocked Companion object
        mockkObject(Calculator.Companion)
        every { Calculator.Companion.getMessagePrefix() } returns DUMMY_MESSAGE_PREFIX

        // Logger mocks
        every { logger.log(any()) } just runs
        every { logger.logGetMessage(any()) } returns DUMMY_MESSAGE
        every { logger.logGetLongTag(any()) } returns LONG_DUMMY_MESSAGE

        every { tracker.sendMessage(any()) } just runs
        coEvery { tracker.sendCode(any()) } just runs
    }

    @Test
    fun `Square calculates the proper result`() {
        val result = calculator.square(2)

        assertTrue(4 == result)
    }

    @Test
    fun `Sum calculates the proper result`() {
        runTest {
            val result = calculator.sum(4, 4)

            assertTrue(8 == result)
            coVerify(exactly = 1) { tracker.sendCode(any()) }
            verify(exactly = 1) {
                tracker.sendMessage(
                    withArg { message ->
                        assertEquals(message.uuid, mockUUID)
                        assert(message.text.startsWith(DUMMY_MESSAGE_PREFIX))
                    }
                )
            }
        }
    }

    @Test
    fun `Sum from interfaces returns always 0`() {
        runTest {
            val result = calculatorInterface.sum(4, 4)

            assertTrue(0 == result)
        }
    }

    @Test
    fun `Divide calculates the proper result and returns Long Tag`() {
        val expectedResult = 1

        val result = calculator.customDivide(2, 2)

        assertTrue(expectedResult == result)

        verify(exactly = 1) { logger.logGetLongTag(any()) }
        verify(exactly = 1) {
            tracker.sendMessage(
                withArg { message ->
                    assert(message.text.startsWith(DUMMY_MESSAGE_PREFIX))
                    assert(message.text.endsWith(LONG_DUMMY_MESSAGE))
                }
            )
        }
    }

    @Test
    fun `WHEN square is called THEN logger is called`() {
        calculator.square(1234)

        verify(exactly = 1) { logger.log(any()) }
        verify(exactly = 0) { logger.logGetMessage(any()) }
    }

    @Test
    fun `WHEN square is called THEN tracker sends a message`() {
        calculator.square(1234)

        verify(exactly = 1) { tracker.sendMessage(any()) }

        confirmVerified(tracker)
    }

    @Test
    fun `WHEN square is called THEN tracker sends the right message`() {
        val number = 1234
        calculator.square(number)

        val slot = slot<Message>()

        verify(
            atLeast = 1,
            atMost = 1
        ) { tracker.sendMessage(capture(slot)) }

        val captured = slot.captured
        assertTrue(captured.text == "${DUMMY_MESSAGE_PREFIX}Calculating square of $number...")
        assertTrue(captured.code == Message.Code.CODE_1)
    }

    @Test
    fun `WHEN square is called THEN fail checking tracker sends a code`() {
        calculator.square(1234)

        coVerify(exactly = 1) { tracker.sendCode(any()) }
    }

    @Test
    fun `Sum fails after checking wrong return stub`() {
        runTest {
            val calculator = mockk<Calculator>()
            coEvery { calculator.sum(2, 3) } returns 0
            val result = calculator.sum(2, 3)
            assertEquals(5, result)
        }
    }
}