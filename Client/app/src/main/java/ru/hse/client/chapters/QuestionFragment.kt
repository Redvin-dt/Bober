package ru.hse.client.chapters

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import ru.hse.client.R
import ru.hse.client.databinding.QuestionActivityBinding
import java.util.LinkedList

class QuestionFragment : Fragment(R.layout.question_activity), AnswerAdapter.OnCheckBoxListener {
    private lateinit var binding: QuestionActivityBinding
    private var numberOfAnswers: Int = 0
    private var correctAnswerNumbers: LinkedList<Int> = LinkedList()

    private enum class QuestionType {
        SINGLE, MULTIPLE
    }

    private var questionType: QuestionType = QuestionType.SINGLE
    private lateinit var listOfAnswers: ListView
    private lateinit var answerAdapter: AnswerAdapter
    private var dataArrayList: MutableList<AnswerData> = ArrayList()
    private val listOfAnswersHints =
        listOf("Answer 1", "Answer 2", "Answer 3", "Answer 4", "Answer 5")
    private val listOfAnswersNumbers = listOf("1", "2", "3", "4", "5")
    private val listOfTypes = listOf("multiple", "single")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("HUI", "HUI")
        super.onViewCreated(view, savedInstanceState)
        binding = QuestionActivityBinding.inflate(layoutInflater)

        initAnswersList()

        initSpinners()

    }

    private fun initAnswersList() {
        numberOfAnswers = 1
        listOfAnswers = binding.listView
        val answerData =
            AnswerData(listOfAnswersHints[0], false, Editable.Factory.getInstance().newEditable(""))
        dataArrayList.add(answerData)

        answerAdapter = AnswerAdapter(activity!!, dataArrayList)
        binding.listView.adapter = answerAdapter
        binding.listView.isClickable = true
        answerAdapter.setOnCheckboxCheckedListener(this)
    }

    override fun onCheckBoxChecked(position: Int, isChecked: Boolean) {
        if (!isChecked) {
            correctAnswerNumbers.remove(position)
            return
        }
        if (questionType == QuestionType.SINGLE) {
            while (correctAnswerNumbers.size > 0) {
                val positionOfRemovedCheckBox = correctAnswerNumbers.first
                correctAnswerNumbers.removeFirst()
                answerAdapter.updateCheckboxState(positionOfRemovedCheckBox, false)
            }
            answerAdapter.updateCheckboxState(position, true)
            correctAnswerNumbers.addLast(position)
        } else {
            correctAnswerNumbers.addLast(position)
        }
    }


    private fun initSpinners() {
        val spinnerNumberOfTests: Spinner = binding.spinnerNumber
        ArrayAdapter(
            activity!!,
            R.layout.selected_item,
            listOfAnswersNumbers
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.dropdown_item)
            spinnerNumberOfTests.adapter = adapter
        }

        spinnerNumberOfTests.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                Log.d("spinnerNumberOfTests", "selected item: $selectedItem")
                val currentNumberOfAnswers = id + 1
                if (currentNumberOfAnswers < numberOfAnswers) {
                    while (currentNumberOfAnswers.toInt() != dataArrayList.size) {
                        answerAdapter.removeItem(dataArrayList.size - 1)
                    }
                    binding.listView.adapter = answerAdapter
                    numberOfAnswers = currentNumberOfAnswers.toInt()
                } else {
                    while (currentNumberOfAnswers > numberOfAnswers) {
                        val answerData = AnswerData(
                            listOfAnswersHints[numberOfAnswers],
                            false,
                            Editable.Factory.getInstance().newEditable("")
                        )
                        dataArrayList.add(answerData)
                        numberOfAnswers++
                    }
                    binding.listView.adapter = answerAdapter
                    numberOfAnswers = currentNumberOfAnswers.toInt()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        val spinnerTestType: Spinner = binding.spinnerType
        ArrayAdapter(
            activity!!,
            R.layout.selected_item,
            listOfTypes
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.dropdown_item)
            spinnerTestType.adapter = adapter
        }

        spinnerTestType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                Log.d("spinnerTestType", "selected item: $selectedItem")
                if (selectedItem == "multiple") {
                    questionType = QuestionType.MULTIPLE
                } else {
                    questionType = QuestionType.SINGLE
                    while (correctAnswerNumbers.size > 1) {
                        val positionOfRemovedCheckBox = correctAnswerNumbers.first
                        correctAnswerNumbers.removeFirst()
                        answerAdapter.updateCheckboxState(positionOfRemovedCheckBox, false)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
    }
}