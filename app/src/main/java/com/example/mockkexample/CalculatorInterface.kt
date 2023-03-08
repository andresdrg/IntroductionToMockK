package com.example.mockkexample

interface CalculatorInterface {
    suspend fun sum(a: Int, b: Int): Int
}