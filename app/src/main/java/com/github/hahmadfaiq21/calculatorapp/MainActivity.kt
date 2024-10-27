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
            if (view.text == ".") {
                if (canAddDecimal)
                    binding.tvWorkings.append(view.text)
                canAddDecimal = false
            } else {
                binding.tvWorkings.append(view.text)
            }
            canAddOperation = true
        }
    }

    fun operatorAction(view: View) {
        if (view is Button) {
            if (isResultDisplayed) {
                binding.tvWorkings.text = binding.tvResults.text
                binding.tvResults.text = getString(R.string.empty_string)
                isResultDisplayed = false
            }

            if (canAddOperation) {
                binding.tvWorkings.append(view.text)
                canAddOperation = false
                canAddDecimal = true
            }
        }
    }

    fun allClearAction(view: View) {
        binding.tvWorkings.text = getString(R.string.empty_string)
        binding.tvResults.text = getString(R.string.empty_string)
    }

    fun backspaceAction(view: View) {
        val length = binding.tvWorkings.length()
        if (length > 0) {
            binding.tvWorkings.text = binding.tvWorkings.text.subSequence(0, length - 1)
        }
    }

    fun equalsAction(view: View) {
        binding.tvResults.text = formatResult(calculateResults())
        isResultDisplayed = true
    }

    private fun calculateResults(): Float {
        val digitsOperators = digitsOperators()
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

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in binding.tvWorkings.text) {
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
                i += 2  // Skip the next number
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

}
