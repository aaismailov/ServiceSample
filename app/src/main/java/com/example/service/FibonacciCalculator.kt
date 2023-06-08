package com.example.service

class FibonacciCalculator {

    fun getNextNumber(): Long {
        val current = pair.first
        pair = pair.second to pair.first + pair.second
        return current
    }

    companion object {
        var pair: Pair<Long, Long> = 0L to 1L
    }
}