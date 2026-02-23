package com.example.mycalc

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.appbar.MaterialToolbar
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private val viewModel: CalculatorViewModel by viewModels()

    private lateinit var resultTextView: TextView
    private lateinit var previousCalculationTextView: TextView

    private fun View.addHapticClick(action: () -> Unit) {
        setOnClickListener {
            this.animate()
                .scaleX(0.90f)
                .scaleY(0.90f)
                .setDuration(80)
                .withEndAction {
                    this.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(80)
                        .start()
                }
                .start()
            it.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            action()
        }
    }

    private fun previewResult() {
        if (viewModel.rawExpression.isEmpty()) {
            previousCalculationTextView.text = ""
            resultTextView.text = "0"
            return
        }
        val last = viewModel.rawExpression.last()
        if (last.isOperator() && last != '-') {
            previousCalculationTextView.text = ""
            resultTextView.text = viewModel.rawExpression
            return
        }
        if (viewModel.rawExpression.toDoubleOrNull() != null) {
            previousCalculationTextView.text = ""
            resultTextView.text = viewModel.rawExpression
            return
        }
        try {
            val formatted = viewModel.evaluatePreview()
            previousCalculationTextView.alpha = 0f
            previousCalculationTextView.translationY = 40f
            updatePreviousDisplay()
            previousCalculationTextView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300) // slower
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
            resultTextView.animate()
                .alpha(0f)
                .setDuration(180) // slower fade out
                .withEndAction {
                    fitAndCompress(resultTextView, formatted)
                    resultTextView.animate()
                        .alpha(1f)
                        .setDuration(260) // slower fade in
                        .setInterpolator(android.view.animation.DecelerateInterpolator())
                        .start()
                }
                .start()
        } catch (e: Exception) {
            previousCalculationTextView.text = ""
            resultTextView.text = viewModel.rawExpression
        }
    }

    private fun showInvalidInput() {
        Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show()
    }

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
                Toast.makeText(this, "Version 3.0.0", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.menu_history -> {
                showHistory()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkForChangelog() {

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val lastVersionSeen = prefs.getString("last_version_seen", "")

        val currentVersion = packageManager
            .getPackageInfo(packageName, 0)
            .versionName

        if (lastVersionSeen != currentVersion) {
            showChangelogDialog()
            prefs.edit().putString("last_version_seen", currentVersion).apply()
        }
    }

    private fun showChangelogDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("What's New in 3.0.0")
            .setMessage(
                "• Smarter calculations\n" +
                        "• Scientific function support\n" +
                        "• Improved expression handling\n" +
                        "• Adaptive long-expression display\n" +
                        "• Smoother animations & better performance"
            )
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        checkForChangelog()

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

        val btnsin: Button=findViewById(R.id.btnSin)
        val btncos: Button=findViewById(R.id.btnCos)
        val btntan: Button=findViewById(R.id.btnTan)
        val btnlog: Button=findViewById(R.id.btnLog)
        val btnpi: Button=findViewById(R.id.btnPi)

        button0.addHapticClick { appendNumber("0") }
        button1.addHapticClick { appendNumber("1") }
        button2.addHapticClick { appendNumber("2") }
        button3.addHapticClick { appendNumber("3") }
        button4.addHapticClick { appendNumber("4") }
        button5.addHapticClick { appendNumber("5") }
        button6.addHapticClick { appendNumber("6") }
        button7.addHapticClick { appendNumber("7") }
        button8.addHapticClick { appendNumber("8") }
        button9.addHapticClick { appendNumber("9") }

        dot.addHapticClick {
            if (viewModel.justEvaluated) {
                viewModel.rawExpression = ""
                previousCalculationTextView.text = ""
                viewModel.justEvaluated = false
            }
            val lastNumber = viewModel.rawExpression.takeLastWhile {
                it.isDigit() || it == '.'
            }
            if (!lastNumber.contains(".")) {
                if (viewModel.rawExpression.isEmpty() || viewModel.rawExpression.last().isOperator() || viewModel.rawExpression.last() == '(') {
                    viewModel.rawExpression += "0."
                } else {
                    viewModel.rawExpression += "."
                }
                updatePreviousDisplay()
                previewResult()
            } else {
                showInvalidInput()
            }
        }

        add.addHapticClick { appendOperator("+") }
        sub.addHapticClick { appendOperator("-") }
        mul.addHapticClick { appendOperator("*") }
        div.addHapticClick { appendOperator("/") }
        percent.addHapticClick { appendOperator("%") }
        power.addHapticClick { appendOperator("^") }

        equals.addHapticClick { calculateResult() }
        clear.addHapticClick { clearCalculator() }
        backspace.addHapticClick { deleteNum() }
        backspace.setOnLongClickListener {
            if (viewModel.rawExpression.isNotEmpty()) {
                clearCalculator()
                Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show()
            } else {
                showInvalidInput()
            }
            true
        }

        btnsin.addHapticClick {
            viewModel.rawExpression += "sin("
            resultTextView.text = viewModel.rawExpression
        }

        btncos.addHapticClick {
            viewModel.rawExpression += "cos("
            resultTextView.text = viewModel.rawExpression
        }

        btntan.addHapticClick {
            viewModel.rawExpression += "tan("
            resultTextView.text = viewModel.rawExpression
        }

        btnlog.addHapticClick {
            viewModel.rawExpression += "log("
            resultTextView.text = viewModel.rawExpression
        }

        btnpi.addHapticClick {
            viewModel.rawExpression += Math.PI.toString()
            resultTextView.text = viewModel.rawExpression
        }

        root.addHapticClick {

            if (viewModel.rawExpression.isNotEmpty() &&
                (viewModel.rawExpression.last().isDigit() || viewModel.rawExpression.last() == ')')
            ) {
                viewModel.rawExpression += "*"
            }
            viewModel.rawExpression += "√("
            resultTextView.text = viewModel.rawExpression
            updatePreviousDisplay()
        }

        fact.addHapticClick {
            if (viewModel.rawExpression.isNotEmpty() &&
                (viewModel.rawExpression.last().isDigit() || viewModel.rawExpression.last() == ')')
            ) {
                viewModel.rawExpression += "!"
                resultTextView.text = viewModel.rawExpression
                updatePreviousDisplay()
            }
        }

        bracOpen.addHapticClick {
            viewModel.rawExpression += "("
            updatePreviousDisplay()
            previewResult()
        }

        bracClose.addHapticClick {
            val openCount = viewModel.rawExpression.count { it == '(' }
            val closeCount = viewModel.rawExpression.count { it == ')' }
            if (closeCount < openCount &&
                viewModel.rawExpression.isNotEmpty() &&
                !viewModel.rawExpression.last().isOperator()
            ) {
                viewModel.rawExpression += ")"
                updatePreviousDisplay()
                previewResult()
            } else {
                showInvalidInput()
            }
        }


        val toolbar: MaterialToolbar=findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
    }

    private fun showHistory() {
        if (viewModel.historyList.isEmpty()) {
            Toast.makeText(this, "No history yet", Toast.LENGTH_SHORT).show()
            return
        }
        val historyText = viewModel.historyList.joinToString("\n")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Calculation History")
            .setMessage(historyText)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun appendOperator(op: String) {
        if (viewModel.rawExpression.isEmpty()) {
            showInvalidInput()
            return
        }
        val last = viewModel.rawExpression.last()
        if (last.isOperator() && last != '-') {
            showInvalidInput()
            return
        }
        if (viewModel.justEvaluated) {
            viewModel.rawExpression += op
            updateResultDisplay()
            previousCalculationTextView.text = ""
            viewModel.justEvaluated = false
            return
        }
        viewModel.rawExpression += op
        previousCalculationTextView.text = ""
        updateResultDisplay()
    }

    private fun updatePreviousDisplay() {
        previousCalculationTextView.post {
            fitAndCompress(previousCalculationTextView, viewModel.rawExpression)
        }
    }

    private fun updateResultDisplay() {
        resultTextView.post {
            fitAndCompress(resultTextView, viewModel.rawExpression)
        }
    }

    private fun fitAndCompress(textView: TextView, fullText: String): String {

        val paint = textView.paint
        val availableWidth = textView.measuredWidth -
                textView.paddingLeft -
                textView.paddingRight
        if (paint.measureText(fullText) <= availableWidth - 10) {
            textView.text = fullText
            return fullText
        }
        var displayText = fullText
        while (paint.measureText(displayText) > availableWidth - 10) {
            val compressed = compressFromStart(displayText)
            if (compressed == displayText) break
            displayText = compressed
        }
        textView.text = displayText
        return displayText
    }

    private fun compressFromStart(expr: String): String {
        if (expr.length <= 1) return expr
        return expr.drop(1)
    }

    private fun deleteNum() {
        if (viewModel.rawExpression.isNotEmpty()){
            viewModel.rawExpression = viewModel.rawExpression.dropLast(1)
            updatePreviousDisplay()
            if (viewModel.rawExpression.isEmpty()) {
                resultTextView.text = "0"
            } else {
                previewResult()
            }
        } else {
            showInvalidInput()
        }
    }

    private fun clearCalculator() {
        viewModel.rawExpression = ""
        resultTextView.text = "0"
        previousCalculationTextView.text = ""
        viewModel.justEvaluated = false
    }

    private fun calculateResult() {
        if (viewModel.rawExpression.isEmpty()) {
            showInvalidInput()
            return
        }
        if (viewModel.rawExpression.toDoubleOrNull() != null) {
            showInvalidInput()
            return
        }
        val last = viewModel.rawExpression.last()
        if (last.isOperator() && last != '-') {
            showInvalidInput()
            return
        }
        try {
            val formatted = viewModel.evaluate()
            previousCalculationTextView.animate()
                .alpha(0f)
                .setDuration(120)
                .withEndAction {
                    previousCalculationTextView.text = ""
                    previousCalculationTextView.alpha = 1f
                }
                .start()
            resultTextView.text = formatted
            resultTextView.animate()
                .scaleX(1.06f)
                .scaleY(1.06f)
                .setDuration(120)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .withEndAction {
                    resultTextView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .setInterpolator(android.view.animation.OvershootInterpolator(1.1f))
                        .start()
                }
                .start()
            viewModel.rawExpression = formatted
            viewModel.justEvaluated = true
        } catch (e: Exception) {
            resultTextView.text = "Error"
            viewModel.rawExpression = ""
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

    private fun appendNumber(number: String) {
        if (viewModel.justEvaluated) {
            if (viewModel.rawExpression.isNotEmpty() && viewModel.rawExpression.last().isOperator()) {
                viewModel.justEvaluated = false
            } else {
                viewModel.rawExpression = ""
                previousCalculationTextView.text = ""
                viewModel.justEvaluated = false
            }
        }
        viewModel.rawExpression += number
        updatePreviousDisplay()
        previewResult()
    }
}