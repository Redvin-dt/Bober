package ru.hse.client.chapters

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import ru.hse.client.R
import ru.hse.client.databinding.QuestionActivityBinding
import ru.hse.client.utility.DrawerBaseActivity

class QuestionActivity: DrawerBaseActivity() {
    private lateinit var binding: QuestionActivityBinding
    private var position: Int = 0
    private lateinit var listOfAnswers: ListView
    private lateinit var answerData: AnswerData
    private lateinit var answerAdapter: AnswerAdapter
    private  var dataArrayList: ArrayList<AnswerData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuestionActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAnswersList()

        initSpinners()

    }

    private fun initAnswersList() {
        position = 0
        listOfAnswers = binding.listView
        val list = listOf("Answer 1", "Answer 2", "Answer 3", "Answer 4", "Answer 5")
        for (number in list) {
            answerData = AnswerData(number, false)
            dataArrayList.add(answerData)
        }

        answerAdapter = AnswerAdapter(this, dataArrayList)
        binding.listView.adapter = answerAdapter
        binding.listView.isClickable = true

    }

    private fun initSpinners() {
        val spinnerNumberOfTests: Spinner = binding.spinnerNumber
        val listNumbers = listOf("1", "2", "3", "4", "5")
        ArrayAdapter(
            this,
            R.layout.selected_item,
            listNumbers
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.dropdown_item)
            spinnerNumberOfTests.adapter = adapter
        }

        spinnerNumberOfTests.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val spinnerTestType: Spinner = binding.spinnerType
        val listTypesItems = listOf("multiple", "one")
        ArrayAdapter(
            this,
            R.layout.selected_item,
            listTypesItems
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.dropdown_item)
            spinnerTestType.adapter = adapter
        }

        spinnerTestType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }
}