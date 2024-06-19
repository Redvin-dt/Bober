package ru.hse.client.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import android.widget.Toast
import okhttp3.OkHttpClient
import ru.hse.client.R
import ru.hse.client.databinding.ActivityGroupSelectMenuBinding
import ru.hse.client.databinding.ActivityPassedTestBinding
import ru.hse.client.groups.*
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto

class PassedTestActivity: DrawerBaseActivity() {
    private lateinit var binding: ActivityPassedTestBinding
    private lateinit var dataArrayList: ArrayList<TestResultData?>
    private lateinit var listViewAdapter: TestResultAdapter

    private var okHttpClient = OkHttpClient()

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