package com.example.mycalc

import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {

    private val engine = CalculatorEngine()

    var rawExpression: String = ""
    var justEvaluated: Boolean = false

    val historyList = mutableListOf<String>()

    fun evaluate(): String {
        val formatted = engine.evaluate(rawExpression)
        historyList.add("$rawExpression = $formatted")
        rawExpression = formatted
        justEvaluated = true
        return formatted
    }

    fun evaluatePreview(): String {
        return engine.evaluate(rawExpression)
    }
}