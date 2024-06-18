package ru.hse.client.chapters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import okhttp3.OkHttpClient
import ru.hse.client.R
import ru.hse.client.databinding.ActivityTestBinding
import ru.hse.client.groups.GroupActivity
import ru.hse.client.groups.ListChapterAdapter
import ru.hse.client.groups.ListChapterData
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.QuestionList
import ru.hse.server.proto.EntitiesProto.QuestionModel

class TestActivity : DrawerBaseActivity(){
    private lateinit var questionNumberTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var testNameTextView: TextView
    private lateinit var answersListView: ListView
    private lateinit var answerEditText: EditText
    private lateinit var answerButton: Button
    private lateinit var binding: ActivityTestBinding
    private lateinit var listViewAdapter: AnswerAdapter
    private var currentQuestion: Int = 1
    private var rightAnswersQuantity: Int = 0
    private val dataArrayList = ArrayList<ListTestData?>()
    private var test: EntitiesProto.TestModel? = null
    private var chapter: EntitiesProto.ChapterModel? = null
    private var okHttpClient = OkHttpClient()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        testNameTextView = findViewById(R.id.test_name)
        questionNumberTextView = findViewById(R.id.questionNumberTextView)
        questionTextView = findViewById(R.id.questionTextView)
        answersListView = findViewById(R.id.answersListView)
        listViewAdapter = ListAnswerAdapter(this, dataArrayList)
        answerEditText = findViewById(R.id.answerEditText)
        answerButton = findViewById(R.id.answerButton)
        val bundle = intent.extras


        if (bundle != null) {
            test = bundle.getSerializable("test") as EntitiesProto.TestModel
            chapter = bundle.getSerializable("chapter") as EntitiesProto.ChapterModel
        }

        if (test != null) {
            Log.i("TestActivity", test!!.name)
            testNameTextView.text = test!!.name
        } else {
            Log.e("TestActivity", "ERROR test is empty")
            finish()
            return
        }

        questionNumberTextView.text = "Question number: $currentQuestion"
        val questions: QuestionList? = test?.questions
        if (questions != null && !questions.questionsList.isEmpty()) {
            createAnswersList(questions.getQuestions(currentQuestion - 1))
        } else {
            val data: Intent = Intent()
            this.setResult(RESULT_OK, data)
            this.finish()
            return
        }

        questionTextView.text = questions.getQuestions(currentQuestion - 1).question

        answerButton.setOnClickListener {
            if (checkAnswer(questions.getQuestions(currentQuestion - 1), answerEditText.text.toString())) {
                rightAnswersQuantity++
            }
            currentQuestion++

            if (questions.questionsList.size >= currentQuestion) {
                questionTextView.text = questions.getQuestions(currentQuestion - 1).question
            } else {
                addTestToUser(chapter!!, test!!, rightAnswersQuantity, questions.questionsList.size, false, this@TestActivity, okHttpClient)
                val data: Intent = Intent()
                this.setResult(RESULT_OK, data)
                this.finish()
                return@setOnClickListener
            }
            createAnswersList(questions.getQuestions(currentQuestion - 1))
            questionNumberTextView.text = "Question number: $currentQuestion"
            answerEditText.text.clear()
        }
    }

    private fun createAnswersList(question: EntitiesProto.QuestionModel) {
        val data: MutableList<Map<String, String>> = mutableListOf()
        val answers = question.answersList
        dataArrayList.clear()
        for ((numAnswer, answer) in question.answersList.withIndex()) {
            dataArrayList.add(
                ListTestData(
                    numAnswer,
                   answer,
                )
            )
        }
        listViewAdapter.notifyDataSetChanged()

        listViewAdapter = ListAnswerAdapter(this, dataArrayList)
        binding.answersListView.adapter = listViewAdapter

        // binding.answersListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataArrayList.toArray())
    }

    private fun checkAnswer(question: QuestionModel, text:String): Boolean {
        var flag = true
        if (question.rightAnswersList.size != text.length) {
            return false
        }
        for (symbol in text) {
            val num = (symbol - '0').toLong() - 1
            flag = flag && (question.rightAnswersList.indexOf(num) != -1)
        }
        return flag
    }
}
