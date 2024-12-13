package com.corporation8793.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.corporation8793.calculator.databinding.ActivityMainBinding
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val firstNumText = StringBuilder("")
    private val secondNumText = StringBuilder("")
    private val operatorText = StringBuilder("")
    private val decimalFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    // 숫자 클릭
    fun numClicked(view: View) {
        val numString = (view as? Button)?.text?.toString() ?: ""
        val numText = if (operatorText.isEmpty()) firstNumText else secondNumText

        numText.append(numString)
        updateEquationTextView()
    }

    // C 클릭
    fun clearClicked(view: View) {
        firstNumText.clear()
        secondNumText.clear()
        operatorText.clear()

        updateEquationTextView()
        binding.tvResult.text = ""
    }

    // = 클릭
    fun equalClicked(view: View) {
        if (firstNumText.isEmpty() || secondNumText.isEmpty() || operatorText.isEmpty()) {
            Toast.makeText(this, "올바르지 않은 수식 입니다.", Toast.LENGTH_SHORT).show()
            return
        }
        // toInt()로 하면 숫자가 클 때 에러 뜸
        val firstNum = firstNumText.toString().toBigDecimal()
        val secondNum = secondNumText.toString().toBigDecimal()

        val result = when(operatorText.toString()){
            "+" -> decimalFormat.format(firstNum + secondNum)
            "-" -> decimalFormat.format(firstNum - secondNum)
            else -> "잘못된 수식 입니다."
        }.toString()
        binding.tvResult.text = result
    }

    // 연산자 클릭
    fun operatorClicked(view: View) {
        val operatorString = (view as? Button)?.text?.toString() ?: ""

        if (firstNumText.isEmpty()) {
            Toast.makeText(this, "먼저 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (secondNumText.isNotEmpty()) {
            Toast.makeText(this, "1개의 연산자에 대해서만 연산이 가능합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        operatorText.append(operatorString)
        updateEquationTextView()
    }

    private fun updateEquationTextView() {
        val firstFormattedNum = if
                (firstNumText.isNotEmpty())
            decimalFormat.format(firstNumText.toString().toBigDecimal()) else ""
        val secondFormattedNum = if
                (secondNumText.isNotEmpty())
            decimalFormat.format(secondNumText.toString().toBigDecimal()) else ""

        binding.tvEquation.text = "$firstFormattedNum $operatorText $secondFormattedNum"
    }
}