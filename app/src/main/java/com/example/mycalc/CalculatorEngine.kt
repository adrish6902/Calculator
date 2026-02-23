package com.example.mycalc

class CalculatorEngine {
    fun evaluate(expr: String): String {
        val balanced = autoBalanceBrackets(expr)
        val result = evaluateExpression(balanced)
        return formatResult(result)
    }

    private fun evaluateExpression(expr: String): Double {
        val tokens = tokenize(expr)
        val postfix = infixToPostfix(tokens)
        return evaluatePostfix(postfix)
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var number = ""
        var word = ""
        for (i in expr.indices) {
            val char = expr[i]
            when {
                char.isDigit() || char == '.' -> {
                    number += char
                }
                char.isLetter() -> {
                    if (number.isNotEmpty()) {
                        tokens.add(number)
                        number = ""
                    }
                    word += char
                }
                else -> {
                    if (number.isNotEmpty()) {
                        tokens.add(number)
                        number = ""
                    }
                    if (word.isNotEmpty()) {
                        tokens.add(word)
                        word = ""
                    }
                    if (char == '-') {
                        val prev = tokens.lastOrNull()
                        if (prev == null || prev == "(" || prev in listOf("+", "-", "*", "/", "%", "^")) {
                            number = "-"
                        } else {
                            tokens.add("-")
                        }
                    } else {
                        tokens.add(char.toString())
                    }
                }
            }
        }
        if (number.isNotEmpty()) tokens.add(number)
        if (word.isNotEmpty()) tokens.add(word)
        return tokens
    }

    private fun infixToPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = ArrayDeque<String>()
        val precedence = mapOf(
            "+" to 1,
            "-" to 1,
            "*" to 2,
            "/" to 2,
            "%" to 2,
            "^" to 3,
            "√" to 4,
            "sin" to 4,
            "cos" to 4,
            "tan" to 4,
            "log" to 4,
            "!" to 5
        )
        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> output.add(token)
                token == "(" -> stack.addLast(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.last() != "(") {
                        output.add(stack.removeLast())
                    }
                    if (stack.isEmpty()) throw Exception("Mismatched brackets")
                    stack.removeLast()
                }
                token in precedence -> {
                    while (stack.isNotEmpty() &&
                        stack.last() in precedence &&
                        precedence[token]!! <= precedence[stack.last()]!!) {
                        output.add(stack.removeLast())
                    }
                    stack.addLast(token)
                }
            }
        }
        while (stack.isNotEmpty()) {
            if (stack.last() == "(") throw Exception("Mismatched brackets")
            output.add(stack.removeLast())
        }
        return output
    }

    private fun evaluatePostfix(tokens: List<String>): Double {
        val stack = ArrayDeque<Double>()

        for (token in tokens) {

            if (token.toDoubleOrNull() != null) {
                stack.addLast(token.toDouble())
            } else {

                val result = when (token) {

                    "sin" -> {
                        val a = stack.removeLast()
                        Math.sin(Math.toRadians(a))
                    }
                    "cos" -> {
                        val a = stack.removeLast()
                        Math.cos(Math.toRadians(a))
                    }
                    "tan" -> {
                        val a = stack.removeLast()
                        Math.tan(Math.toRadians(a))
                    }
                    "log" -> {
                        val a = stack.removeLast()
                        if (a <= 0) throw Exception("Invalid log")
                        Math.log10(a)
                    }
                    "√" -> {
                        if (stack.isEmpty()) throw Exception("Invalid expression")
                        val a = stack.removeLast()
                        if (a < 0) throw Exception("Negative root")
                        Math.sqrt(a)
                    }
                    "!" -> {
                        if (stack.isEmpty()) throw Exception("Invalid expression")
                        val a = stack.removeLast()
                        if (a < 0 || a % 1 != 0.0) throw Exception("Invalid factorial")
                        factorial(a.toInt()).toDouble()
                    }
                    "+", "-", "*", "/", "%", "^" -> {
                        if (stack.size < 2) throw Exception("Invalid expression")
                        val b = stack.removeLast()
                        val a = stack.removeLast()
                        when (token) {
                            "+" -> a + b
                            "-" -> a - b
                            "*" -> a * b
                            "/" -> a / b
                            "%" -> a % b
                            "^" -> Math.pow(a, b)
                            else -> 0.0
                        }
                    }
                    else -> throw Exception("Unknown operator")
                }
                stack.addLast(result)
            }
        }
        if (stack.size != 1) throw Exception("Invalid expression")
        return stack.removeLast()
    }

    private fun factorial(n: Int): Long {
        var result = 1L
        for (i in 1..n) {
            result *= i
        }
        return result
    }

    private fun autoBalanceBrackets(expr: String): String {
        var openCount = 0
        var closeCount = 0
        for (c in expr) {
            if (c == '(') openCount++
            if (c == ')') closeCount++
        }
        if (closeCount > openCount) {
            throw Exception("Extra closing bracket")
        }
        val missing = openCount - closeCount
        return if (missing > 0) {
            expr + ")".repeat(missing)
        } else {
            expr
        }
    }

    private fun formatResult(value: Double): String {
        val rounded = kotlin.math.round(value * 1_000_000_000.0) / 1_000_000_000.0
        return if (rounded % 1.0 == 0.0) {
            rounded.toLong().toString()
        } else {
            rounded.toString()
                .trimEnd('0')
                .trimEnd('.')
        }
    }
}