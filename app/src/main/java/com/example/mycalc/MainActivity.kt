package com.example.mycalc

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView
    private lateinit var previousCalculationTextView: TextView

    private var firstNumber = 0.0
    private var operation = ""
    private var isNewOperation=true

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        when (currentMode) {
            AppCompatDelegate.MODE_NIGHT_NO ->
                menu?.findItem(R.id.theme_light)?.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES ->
                menu?.findItem(R.id.theme_dark)?.isChecked = true
            else ->
                menu?.findItem(R.id.theme_system)?.isChecked = true
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.theme_light -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(this,"Theme Changed", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.theme_dark -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(this,"Theme Changed", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.theme_system -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                Toast.makeText(this,"Theme Changed", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.menu_version -> {
                Toast.makeText(this, "Version 2.0.0", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.menu_history -> {
                showHistory()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        resultTextView=findViewById(R.id.resultTextView)
        previousCalculationTextView=findViewById(R.id.previousCalculationTextView)

        val button0: Button=findViewById(R.id.btn0)
        val button1: Button=findViewById(R.id.btn1)
        val button2: Button=findViewById(R.id.btn2)
        val button3: Button=findViewById(R.id.btn3)
        val button4: Button=findViewById(R.id.btn4)
        val button5: Button=findViewById(R.id.btn5)
        val button6: Button=findViewById(R.id.btn6)
        val button7: Button=findViewById(R.id.btn7)
        val button8: Button=findViewById(R.id.btn8)
        val button9: Button=findViewById(R.id.btn9)
        val dot: Button=findViewById(R.id.btnDot)

        val add: Button=findViewById(R.id.btnPlus)
        val sub: Button=findViewById(R.id.btnMinus)
        val mul: Button=findViewById(R.id.btnMultiply)
        val div: Button=findViewById(R.id.btnDivide)
        val percent: Button=findViewById(R.id.btnPercent)

        val equals: Button=findViewById(R.id.btnEquals)
        val clear: Button=findViewById(R.id.btnCLear)
        val backspace: ImageButton=findViewById(R.id.btnBackspace)

        val bracOpen: Button=findViewById(R.id.btnOpenBrac)
        val bracClose: Button=findViewById(R.id.btnCloseBrac)
        val root: Button=findViewById(R.id.btnRoot)
        val power: Button=findViewById(R.id.btnPower)
        val fact: Button=findViewById(R.id.btnFact)

        button0.setOnClickListener { appendNumber("0") }
        button1.setOnClickListener { appendNumber("1") }
        button2.setOnClickListener { appendNumber("2") }
        button3.setOnClickListener { appendNumber("3") }
        button4.setOnClickListener { appendNumber("4") }
        button5.setOnClickListener { appendNumber("5") }
        button6.setOnClickListener { appendNumber("6") }
        button7.setOnClickListener { appendNumber("7") }
        button8.setOnClickListener { appendNumber("8") }
        button9.setOnClickListener { appendNumber("9") }
        dot.setOnClickListener { appendNumber(".") }

        add.setOnClickListener { appendOperator("+") }
        sub.setOnClickListener { appendOperator("-") }
        mul.setOnClickListener { appendOperator("*") }
        div.setOnClickListener { appendOperator("/") }
        percent.setOnClickListener { appendOperator("%") }
        power.setOnClickListener { appendOperator("^") }

        equals.setOnClickListener { calculateResult() }
        clear.setOnClickListener { clearCalculator() }
        backspace.setOnClickListener { deleteNum() }

        root.setOnClickListener {

            if (expression.isNotEmpty() &&
                (expression.last().isDigit() || expression.last() == ')')
            ) {
                expression += "*"
            }
            expression += "√("
            resultTextView.text = expression
            previousCalculationTextView.text = expression
        }

        fact.setOnClickListener {
            try{
                val number: Double = resultTextView.text.toString().toDouble()
                if(number<0){
                    resultTextView.text="Error"
                    return@setOnClickListener
                }
                val result = factorial(number.toInt())
                previousCalculationTextView.text="$number! ="
                resultTextView.text = formatResult(result.toDouble())
                isNewOperation=true
            }
            catch (e:Exception){
                resultTextView.text="Error"
            }
        }

        bracOpen.setOnClickListener {
            expression += "("
            resultTextView.text = expression
            previousCalculationTextView.text = expression
        }

        bracClose.setOnClickListener {
            expression += ")"
            resultTextView.text = expression
            previousCalculationTextView.text = expression
        }


        val toolbar: MaterialToolbar=findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        checkAndShowStartupWarning()
    }

    private fun checkAndShowStartupWarning() {
        val prefs = getSharedPreferences("MyCalcPrefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)
        if (isFirstLaunch) {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Notice")
                .setMessage("Currently negative number operations are not supported but it will be added soon.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
            prefs.edit().putBoolean("isFirstLaunch", false).apply()
        }
    }

    private fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            value.toString()
                .trimEnd('0')
                .trimEnd('.')
        }
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

    private var justEvaluated = false

    private fun evaluateExpression(expr: String): Double {
        val tokens = tokenize(expr)
        val postfix = infixToPostfix(tokens)
        return evaluatePostfix(postfix)
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var number = ""
        for (char in expr) {
            if (char.isDigit() || char == '.') {
                number += char
            } else {
                if (number.isNotEmpty()) {
                    tokens.add(number)
                    number = ""
                }
                tokens.add(char.toString())
            }
        }
        if (number.isNotEmpty()) tokens.add(number)
        return tokens
    }

    private fun infixToPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = java.util.Stack<String>()
        val precedence = mapOf(
            "+" to 1,
            "-" to 1,
            "*" to 2,
            "/" to 2,
            "%" to 2,
            "^" to 3,
            "√" to 4
        )
        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> output.add(token)
                token == "(" -> stack.push(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.peek() != "(") {
                        output.add(stack.pop())
                    }
                    if (stack.isEmpty()) throw Exception("Mismatched brackets")
                    stack.pop()
                }
                token in precedence -> {
                    while (stack.isNotEmpty() &&
                        stack.peek() in precedence &&
                        precedence[token]!! <= precedence[stack.peek()]!!) {
                        output.add(stack.pop())
                    }
                    stack.push(token)
                }
            }
        }
        while (stack.isNotEmpty()) {
            if (stack.peek() == "(") throw Exception("Mismatched brackets")
            output.add(stack.pop())
        }
        return output
    }

    private fun evaluatePostfix(tokens: List<String>): Double {
        val stack = java.util.Stack<Double>()

        for (token in tokens) {

            if (token.toDoubleOrNull() != null) {
                stack.push(token.toDouble())
            } else {

                val result = when (token) {

                    "√" -> {
                        if (stack.isEmpty()) throw Exception("Invalid expression")
                        val a = stack.pop()
                        if (a < 0) throw Exception("Negative root")
                        Math.sqrt(a)
                    }
                    "+", "-", "*", "/", "%", "^" -> {
                        if (stack.size < 2) throw Exception("Invalid expression")
                        val b = stack.pop()
                        val a = stack.pop()
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
                stack.push(result)
            }
        }
        if (stack.size != 1) throw Exception("Invalid expression")
        return stack.pop()
    }

    private var expression = ""

    private val historyList = mutableListOf<String>()

    private fun showHistory() {
        if (historyList.isEmpty()) {
            Toast.makeText(this, "No history yet", Toast.LENGTH_SHORT).show()
            return
        }
        val historyText = historyList.joinToString("\n")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Calculation History")
            .setMessage(historyText)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun appendOperator(op: String) {
        if (expression.isEmpty()) return

        if (justEvaluated) {
            justEvaluated = false
        }
        if (!expression.last().isOperator()) {
            expression += op
            resultTextView.text = expression
            previousCalculationTextView.text = expression
        }
    }

    private fun Char.isOperator(): Boolean {
        return this == '+' ||
                this == '-' ||
                this == '*' ||
                this == '/' ||
                this == '%' ||
                this == '^'
    }

    private fun factorial(n: Int): Long {
        var result = 1L
        for (i in 1..n) {
            result *= i
        }
        return result
    }

    private fun deleteNum() {
        if(resultTextView.text.isNotEmpty() && resultTextView.text!="0.0" && resultTextView.text!="error"){
            resultTextView.text=resultTextView.text.dropLast(1)
        }
        else{
            Toast.makeText(this,"Invalid Operation",Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearCalculator() {
        expression = ""
        resultTextView.text = "0"
        previousCalculationTextView.text = ""
        justEvaluated = false
    }

    private fun calculateResult() {
        try {
            val balancedExpression = autoBalanceBrackets(expression)
            val result = evaluateExpression(balancedExpression)
            val formatted = formatResult(result)
            previousCalculationTextView.text = "$balancedExpression ="
            historyList.add("$balancedExpression = $formatted")
            resultTextView.text = formatted
            expression = formatted
            justEvaluated = true
        } catch (e: Exception) {
            resultTextView.text = "Error"
            expression = ""
        }
    }

    private fun setOperation(operator: String) {
        val currentNumber = resultTextView.text.toString().toDouble()
        if (!isNewOperation) {
            if (operation.isNotEmpty()) {
                firstNumber = when (operation) {
                    "+" -> firstNumber + currentNumber
                    "-" -> firstNumber - currentNumber
                    "x" -> firstNumber * currentNumber
                    "/" -> firstNumber / currentNumber
                    "%" -> firstNumber % currentNumber
                    "^" -> Math.pow(firstNumber, currentNumber)
                    else -> currentNumber
                }
                resultTextView.text = firstNumber.toString()
            }
            else {
                firstNumber = currentNumber
            }
        }
        operation = operator
        isNewOperation = true
        previousCalculationTextView.text = "$firstNumber $operation"
    }

    private fun appendNumber(number: String) {
        if (justEvaluated) {
            expression = ""
            previousCalculationTextView.text = ""
            justEvaluated = false
        }
        expression += number
        resultTextView.text = expression
        previousCalculationTextView.text = expression
    }
}