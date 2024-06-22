package ru.hse.client.profile

import android.os.Bundle
import android.util.Log
import ru.hse.client.databinding.ActivityPassedTestBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.client.utility.user

class PassedTestActivity: DrawerBaseActivity() {
    private lateinit var binding: ActivityPassedTestBinding
    private lateinit var dataArrayList: ArrayList<TestResultData?>
    private lateinit var listViewAdapter: TestResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassedTestBinding.inflate(layoutInflater)

        dataArrayList = ArrayList()
        listViewAdapter = TestResultAdapter(this, dataArrayList)

        setContentView(binding.root)
        allocateActivityTitle("Tests:")

        binding.testList.adapter = listViewAdapter

        binding.backButton.setOnClickListener {
            finish()
            return@setOnClickListener
        }

        drawTestList()
    }

    private fun drawTestList() {
        val passedTests = user.getUserModel()!!.passedTests.passedTestsList

        val stringBuilder : StringBuilder = StringBuilder()

        for (i in passedTests) {
            stringBuilder.append(i.testName)
            stringBuilder.append(" ")
        }

        Log.i("Info", "userGroupList is $stringBuilder")
        dataArrayList.clear()

        for (test in passedTests) {
            dataArrayList.add(
                TestResultData(
                    user.getUserLogin(),
                    test.chapterName,
                    test.rightAnswers.toString() + "/" + test.questionsNumber.toString(),
                    test.testName
                )
            )
        }

        listViewAdapter.notifyDataSetChanged()
    }


}