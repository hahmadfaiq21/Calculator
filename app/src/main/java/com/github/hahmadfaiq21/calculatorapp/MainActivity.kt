package com.github.hahmadfaiq21.calculatorapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.hahmadfaiq21.calculatorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isResultDisplayed = false
    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            val currentText = binding.tvWorkings.text.toString()
            if (view.text == ".") {
                if (canAddDecimal) {
                    if (currentText.isEmpty() || currentText.last() in listOf('+', '–', '×', '÷')) {
                        binding.tvWorkings.append("0")
                    }
                    binding.tvWorkings.append(".")
                    canAddDecimal = false
                }
            } else {
                binding.tvWorkings.append(view.text)
                canAddOperation = true
            }
        }
    }

    fun operatorAction(view: View) {
        if (view is Button) {
            if (isResultDisplayed) {
                binding.tvWorkings.text = binding.tvResults.text
                binding.tvResults.text = getString(R.string.empty_string)
                isResultDisplayed = false
            }

            val currentText = binding.tvWorkings.text.toString()
            if (currentText.isNotEmpty() && canAddOperation) {
                binding.tvWorkings.append(view.text)
                canAddOperation = false
                canAddDecimal = true
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun allClearAction(view: View) {
        binding.tvWorkings.text = getString(R.string.empty_string)
        binding.tvResults.text = getString(R.string.empty_string)
        isResultDisplayed = false
        canAddOperation = false
        canAddDecimal = true
    }

    @Suppress("UNUSED_PARAMETER")
    fun backspaceAction(view: View) {
        val length = binding.tvWorkings.length()
        if (length > 0) {
            binding.tvWorkings.text = binding.tvWorkings.text.subSequence(0, length - 1)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun equalsAction(view: View) {
        var currentText = binding.tvWorkings.text.toString().trim()
        if (currentText.isNotEmpty() && currentText.last().isOperator()) {
            currentText = currentText.substring(0, currentText.length - 1)
        }
        if (currentText.isNotEmpty()) {
            binding.tvResults.text = formatResult(calculateResults(currentText))
            isResultDisplayed = true
        } else {
            binding.tvResults.text = getString(R.string.error_message)
        }
    }

    private fun calculateResults(workings: String): Float {
        val digitsOperators = digitsOperators(workings)
        if (digitsOperators.isEmpty()) return 0f

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return 0f

        return addSubtractCalculate(timesDivision)
    }

    private fun formatResult(result: Float): String {
        return if (result % 1 == 0f) {
            result.toInt().toString()
        } else {
            result.toString()
        }
    }

    private fun digitsOperators(workings: String): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in workings) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                if (currentDigit.isNotEmpty()) {
                    list.add(currentDigit.toFloat())
                    currentDigit = ""
                }
                list.add(character)
            }
        }
        if (currentDigit.isNotEmpty()) {
            list.add(currentDigit.toFloat())
        }
        return list
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var i = 0
        while (i < passedList.size) {
            if (passedList[i] is Char && (passedList[i] == '×' || passedList[i] == '÷')) {
                val prev = newList.removeLast() as Float
                val next = passedList[i + 1] as Float
                val result = if (passedList[i] == '×') prev * next else prev / next
                newList.add(result)
                i += 2
            } else {
                newList.add(passedList[i])
                i++
            }
        }
        return newList
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float
        var i = 1

        while (i < passedList.size) {
            val operator = passedList[i] as Char
            val nextDigit = passedList[i + 1] as Float

            when (operator) {
                '+' -> result += nextDigit
                '–' -> result -= nextDigit
            }
            i += 2
        }

        return result
    }

    private fun Char.isOperator(): Boolean {
        return this in listOf('+', '–', '×', '÷')
    }

}
