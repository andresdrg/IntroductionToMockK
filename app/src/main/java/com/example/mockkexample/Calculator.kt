package com.example.mockkexample

class Calculator(
    private val tracker: Tracker,
    private val logger: Logger
) : CalculatorInterface {

    companion object {
        fun getMessagePrefix(): String = "LogMsg:"
    }

    fun square(number: Int): Int {
        val message = Message(
            text = "${getMessagePrefix()}Calculating square of $number...",
            code = Message.Code.CODE_1
        )
        tracker.sendMessage(message)

        val result = number * number
        logger.log("The square of $number is $result")
        return result
    }

    override suspend fun sum(a: Int, b: Int): Int {
        val result = a + b
        val messageText = "${getMessagePrefix()}The sum of $a and $b is $result "
        val message = Message(
            uuid = UtilObject.getCustomMessageUUID(),
            text = messageText + logger.logGetMessage(messageText),
            code = Message.Code.CODE_1
        )
        tracker.sendMessage(message)
        tracker.sendCode(result)
        return result
    }

    fun customDivide(a: Int, b: Int): Int {
        val result = a / b
        val messageText = "${getMessagePrefix()}The division of $a and $b is $result "
        val message = Message(
            text = messageText + logger.logGetLongTag(messageText),
            code = Message.Code.CODE_2
        )
        tracker.sendMessage(message)
        return result
    }
}