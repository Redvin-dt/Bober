package ru.hse.client.chapters

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.widget.*
import okhttp3.OkHttpClient
import ru.hse.client.R
import ru.hse.client.databinding.ActivityTestBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.QuestionList
import ru.hse.server.proto.EntitiesProto.QuestionModel
import kotlin.math.roundToLong

class TestActivity : DrawerBaseActivity(), ListQuestionAdapter.OnCheckBoxListener{
    private lateinit var questionNumberTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var testNameTextView: TextView
    private lateinit var answersListView: ListView
    private lateinit var answerEditText: EditText
    private lateinit var answerButton: Button
    private lateinit var binding: ActivityTestBinding
    private lateinit var listViewAdapter: ListQuestionAdapter
    private var currentQuestion: Int = 1
    private var rightAnswersQuantity: Int = 0
    private val dataArrayList = ArrayList<ListQuestionData?>()
    private var test: EntitiesProto.TestModel? = null
    private var chapter: EntitiesProto.ChapterModel? = null
    private lateinit var selectedAnswers: ArrayList<Int>
    private var okHttpClient = OkHttpClient()
    private lateinit var progressBar: ProgressBar
    private lateinit var customCountDownTimer: CustomCountDownTimer
    private var countDownTime: Long = 0
    private var clockTime: Long = (countDownTime * 1000)
    private var progressTime: Float = (clockTime / 1000).toFloat()
    private lateinit var timer: TextView
    private var questions: QuestionList? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        testNameTextView = findViewById(R.id.test_name)
        questionNumberTextView = findViewById(R.id.questionNumberTextView)
        questionTextView = findViewById(R.id.question_text)
        answersListView = findViewById(R.id.answersListView)
        listViewAdapter = ListQuestionAdapter(this, dataArrayList)
        answerButton = findViewById(R.id.answerButton)
        selectedAnswers = ArrayList()
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
        questions = test?.questions

        if (questions != null && questions!!.questionsList.isNotEmpty()) {
            createAnswersList(questions!!.getQuestions(currentQuestion - 1))
            countDownTime = test!!.secondsForTest
            if (countDownTime != -1L) {
                clockTime = (countDownTime * 1000)
                progressTime = (clockTime / 1000).toFloat()
                initTimer()
            }
        } else {
            val data: Intent = Intent()
            this.setResult(RESULT_OK, data)
            this.finish()
            return
        }

        questionTextView.text = questions!!.getQuestions(currentQuestion - 1).question

        answerButton.setOnClickListener {
            if (checkAnswer(questions!!.getQuestions(currentQuestion - 1))) {
                rightAnswersQuantity++
            }
            currentQuestion++

            if (questions!!.questionsList.size >= currentQuestion) {
                questionTextView.text = questions!!.getQuestions(currentQuestion - 1).question
            } else {
                addTestToUser(chapter!!, test!!, rightAnswersQuantity, questions!!.questionsList.size, false, this@TestActivity, okHttpClient)
                val data: Intent = Intent()
                this.setResult(RESULT_OK, data)
                this.finish()
                return@setOnClickListener
            }
            createAnswersList(questions!!.getQuestions(currentQuestion - 1))
            questionNumberTextView.text = "Question number: $currentQuestion"
        }
    }

    private fun initTimer() {
        var secondsLeft: Long = 0
        timer = binding.time
        customCountDownTimer = CustomCountDownTimer(clockTime, 1000)
        customCountDownTimer.onTick = {milliSecondsUntilFinish: Long ->
            val second = (milliSecondsUntilFinish / 1000.0).roundToLong()
            secondsLeft = second
            timerFormat(
                secondsLeft,
                timer
            )
        }
        customCountDownTimer.onDone = {
            addTestToUser(chapter!!, test!!, rightAnswersQuantity, questions!!.questionsList.size, false, this@TestActivity, okHttpClient)
            val data: Intent = Intent()
            this.setResult(RESULT_OK, data)
            this.finish()
        }

        progressBar = binding.timer
        progressBar.max = progressTime.toInt()
        progressBar.progress = progressTime.toInt()

        customCountDownTimer.startTimer()

    }

    private fun timerFormat(secondsLeft: Long, timer: TextView) {
        progressBar.progress = secondsLeft.toInt()
        val timeFormat = DecimalFormat("00")
        val minutes = secondsLeft / 60
        val seconds = secondsLeft % 60
        val time = timeFormat.format(minutes) + ":" + timeFormat.format(seconds)
        timer.text = time
    }

    private fun createAnswersList(question: EntitiesProto.QuestionModel) {
        val data: MutableList<Map<String, String>> = mutableListOf()
        val answers = question.answersList
        dataArrayList.clear()
        for ((numAnswer, answer) in question.answersList.withIndex()) {
            dataArrayList.add(
                ListQuestionData(
                    false,
                    answer,
                )
            )
        }
        listViewAdapter.notifyDataSetChanged()

        listViewAdapter = ListQuestionAdapter(this, dataArrayList)
        binding.answersListView.adapter = listViewAdapter

        listViewAdapter.setOnCheckboxCheckedListener(this)

        // binding.answersListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataArrayList.toArray())
    }

    private fun checkAnswer(question: QuestionModel): Boolean {
        if (question.rightAnswersList.size != selectedAnswers.size) {
            return false
        }
        for (number in selectedAnswers) {
            if (!question.rightAnswersList.contains(number.toLong())) {
                return false
            }
        }
        return true
    }

    override fun onCheckBoxChecked(position: Int, isChecked: Boolean) {
        if (!isChecked) {
            selectedAnswers.remove(position)
        } else {
            selectedAnswers.add(position)
        }
    }
}
