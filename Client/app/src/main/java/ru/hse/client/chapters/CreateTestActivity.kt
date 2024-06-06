package ru.hse.client.chapters

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsetsAnimation
import android.view.WindowInsetsAnimationController
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsAnimationControllerCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.google.android.material.textfield.TextInputEditText
import ru.hse.client.R
import ru.hse.client.databinding.ActivityCreateTestBinding
import ru.hse.client.databinding.QuestionActivityBinding
import ru.hse.client.entry.hideKeyboard
import ru.hse.client.utility.DrawerBaseActivity
import java.util.LinkedList


class CreateTestActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityCreateTestBinding
    private lateinit var startPositions: ArrayList<Int>
    private var currentPosition = 0
    private lateinit var questionManager: QuestionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //startPositions = intent.extras?.get("startPositions") as ArrayList<Int>
        startPositions = ArrayList()
        binding.logo.text = "Test #${currentPosition} of ${startPositions.size}"

        questionManager = QuestionManager(binding, this)
        questionManager.addNewQuestion()

        binding.addButton.setOnClickListener {
            questionManager.addNewQuestion()
        }

        binding.prevButton.setOnClickListener {
            questionManager.toPreviousQuestion()
        }

        binding.nextButton.setOnClickListener {
            questionManager.toNextQuestion()
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}

class QuestionBody(questionNumber: String, binding: ActivityCreateTestBinding, context: Context) :
    AnswerAdapter.OnCheckBoxListener {
    private var mQuestionNumber: String = questionNumber
    private var mBinding: ActivityCreateTestBinding = binding
    private var mContext: Context = context
    private var numberOfAnswers: Int = 0
    private var correctAnswerNumbers: LinkedList<Int> = LinkedList()

    private enum class QuestionType {
        SINGLE, MULTIPLE
    }

    private var questionType: QuestionType = QuestionType.SINGLE
    private lateinit var answerAdapter: AnswerAdapter
    private var dataArrayList: MutableList<AnswerData> = ArrayList()


    private val listOfAnswersHints =
        listOf("Answer 1", "Answer 2", "Answer 3", "Answer 4", "Answer 5")
    private val listOfAnswersNumbers = listOf("1", "2", "3", "4", "5")
    private val listOfTypes = listOf("multiple", "single")

    init {
        initAnswersList()
        initSpinners()
        mBinding.questionNumber.text = mQuestionNumber
        mBinding.questionText.text = Editable.Factory.getInstance().newEditable("")
    }

    private fun initAnswersList() {
        numberOfAnswers = 1
        val answerData =
            AnswerData(listOfAnswersHints[0], false, Editable.Factory.getInstance().newEditable(""))
        dataArrayList.add(answerData)

        answerAdapter = AnswerAdapter(mContext, dataArrayList)
        mBinding.listView.adapter = answerAdapter
        mBinding.listView.isClickable = true
        answerAdapter.setOnCheckboxCheckedListener(this@QuestionBody)
    }

    private fun initSpinners() {
        val spinnerNumberOfTests: Spinner = mBinding.spinnerNumber
        ArrayAdapter(
            mContext,
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
                    mBinding.listView.adapter = answerAdapter
                    numberOfAnswers = currentNumberOfAnswers.toInt()
                } else if (currentNumberOfAnswers > numberOfAnswers) {
                    while (currentNumberOfAnswers > numberOfAnswers) {
                        val answerData = AnswerData(
                            listOfAnswersHints[numberOfAnswers],
                            false,
                            Editable.Factory.getInstance().newEditable("")
                        )
                        dataArrayList.add(answerData)
                        numberOfAnswers++
                    }
                    mBinding.listView.adapter = answerAdapter
                    numberOfAnswers = currentNumberOfAnswers.toInt()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        val spinnerTestType: Spinner = mBinding.spinnerType
        ArrayAdapter(
            mContext,
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

    fun saveState(): Bundle {
        val state = Bundle()
        state.putString("question text", mBinding.questionText.text.toString())
        state.putString("question number", mQuestionNumber)
        state.putInt("number of answers", numberOfAnswers)
        for (position in 0..<numberOfAnswers) {
            state.putBundle("answer $position", answerAdapter.getAnswerDataByPosition(position))
        }
        state.putString("question type", questionType.toString())
        state.putInt("correct answer numbers", correctAnswerNumbers.size)
        for (number in correctAnswerNumbers) {
            state.putInt("correct answer $number", number)
        }
        return state
    }

    fun createQuestion(state: Bundle) {
        mBinding.questionText.text =
            Editable.Factory.getInstance().newEditable(state.getString("question text")!!)
        mQuestionNumber = state.getString("question number")!!
        mBinding.questionNumber.text = mQuestionNumber

        dataArrayList.clear()
        numberOfAnswers = state.getInt("number of answers")
        for (position in 0..<numberOfAnswers) {
            val currentAnswerData: Bundle? = state.getBundle("answer $position")
            val answerText: String = currentAnswerData!!.getString("answer text")!!
            val checkBoxState: Boolean = currentAnswerData.getBoolean("checkBox state")
            val answerData =
                AnswerData(
                    listOfAnswersHints[position],
                    checkBoxState,
                    Editable.Factory.getInstance().newEditable(answerText)
                )
            dataArrayList.add(answerData)
        }

        answerAdapter = AnswerAdapter(mContext, dataArrayList)
        mBinding.listView.adapter = answerAdapter
        mBinding.listView.isClickable = true

        if (state.getString("question type") == "MULTIPLE") {
            questionType = QuestionType.MULTIPLE
            mBinding.spinnerType.setSelection(0)
        } else {
            questionType = QuestionType.SINGLE
            mBinding.spinnerType.setSelection(1)
        }
        correctAnswerNumbers.clear()
        val numberOfCorrectAnswers = state.getInt("correct answer numbers")
        for (position in 0..<numberOfCorrectAnswers) {
            val number = state.getInt("correct answer $position")
            correctAnswerNumbers.add(number)
        }

        // mBinding.spinnerNumber.setSelection(numberOfAnswers - 1)
        answerAdapter.setOnCheckboxCheckedListener(this@QuestionBody)

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

}

class QuestionManager(binding: ActivityCreateTestBinding, context: Context) {
    private var mBinding: ActivityCreateTestBinding = binding
    private var mContext: Context = context
    private var questionBodies: MutableList<QuestionBody> = mutableListOf()
    private var questionBundles: MutableList<Bundle> = mutableListOf()
    private var currentQuestionIndex = -1

    fun addNewQuestion() {
        if (currentQuestionIndex == -1) {
            val newQuestionBody =
                QuestionBody("Question #1", mBinding, mContext)
            questionBodies.add(newQuestionBody)
            questionBundles.add(Bundle())
            currentQuestionIndex = 0
            return
        }

        questionBundles[currentQuestionIndex] = questionBodies[currentQuestionIndex].saveState()
        val newQuestionBody =
            QuestionBody("Question #${questionBodies.size + 1}", mBinding, mContext)
        questionBodies.add(newQuestionBody)
        questionBundles.add(Bundle())
        currentQuestionIndex = questionBodies.size - 1
    }

    fun toPreviousQuestion() {
        if (currentQuestionIndex != 0) {
            questionBundles[currentQuestionIndex] = questionBodies[currentQuestionIndex].saveState()
            currentQuestionIndex--
            questionBodies[currentQuestionIndex].createQuestion(questionBundles[currentQuestionIndex])
        } else {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    mContext,
                    "You are on the first question",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun toNextQuestion() {
        if (currentQuestionIndex != questionBodies.size - 1) {
            questionBundles[currentQuestionIndex] = questionBodies[currentQuestionIndex].saveState()
            currentQuestionIndex++
            questionBodies[currentQuestionIndex].createQuestion(questionBundles[currentQuestionIndex])
        } else {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    mContext,
                    "You are on the last question",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}