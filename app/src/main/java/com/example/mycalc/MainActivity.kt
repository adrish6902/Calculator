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
                Toast.makeText(this, "Version 1.2.0", Toast.LENGTH_SHORT).show()
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

        add.setOnClickListener { setOperation("+") }
        sub.setOnClickListener { setOperation("-") }
        mul.setOnClickListener { setOperation("x") }
        div.setOnClickListener { setOperation("/") }
        percent.setOnClickListener { setOperation("%") }

        equals.setOnClickListener { calculateResult() }
        clear.setOnClickListener { clearCalculator() }
        backspace.setOnClickListener { deleteNum() }

        val toolbar: MaterialToolbar=findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

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
        resultTextView.text="0"
        previousCalculationTextView.text=""
        firstNumber=0.0
        operation=""
        isNewOperation=true
    }

    private fun calculateResult() {
        try{
            val secondNumber: Double = resultTextView.text.toString().toDouble()
            val result: Double= when(operation){
                "+" -> firstNumber+secondNumber
                "-" -> firstNumber-secondNumber
                "x" -> firstNumber*secondNumber
                "/" -> firstNumber/secondNumber
                "%" -> firstNumber%secondNumber
                else -> secondNumber
            }
            previousCalculationTextView.text="$firstNumber $operation $secondNumber ="
            resultTextView.text=result.toString()
            isNewOperation=true
        }
        catch(e:Exception){
            resultTextView.text="Error"
        }
    }

    private fun setOperation(operator: String) {
        firstNumber=resultTextView.text.toString().toDouble()
        operation=operator
        isNewOperation=true
        previousCalculationTextView.text="$firstNumber $operation"
        resultTextView.text="0"
    }

    private fun appendNumber(number: String){
        if(isNewOperation){
            resultTextView.text=number
            isNewOperation=false
        }
        else{
            resultTextView.text="${resultTextView.text}$number"        }
    }
}