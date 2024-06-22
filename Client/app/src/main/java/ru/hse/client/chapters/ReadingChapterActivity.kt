package ru.hse.client.chapters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import okhttp3.OkHttpClient
import ru.hse.client.databinding.ActivityReadingChapterBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto.ChapterList
import java.util.*

class ReadingChapterActivity : DrawerBaseActivity() {
    private lateinit var binding: ActivityReadingChapterBinding
    private lateinit var chapterTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var startTestButton: Button
    private lateinit var nextTestButton: ImageButton
    private lateinit var prevTestButton: ImageButton
    private var currentTest: Int = 0
    private var chapter: EntitiesProto.ChapterModel? = null
    private var tests: EntitiesProto.TestList? = null
    private var text: String = ""
    private var pos: Int = 0
    private var chapters: ChapterList? = null
    private var okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        user.setUserByLogin(this, user.getUserLogin())
        super.onCreate(savedInstanceState)
        binding = ActivityReadingChapterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chapterTextView = binding.contentTextView
        titleTextView = binding.titleTextView
        startTestButton = binding.startTestButton
        nextTestButton = binding.nextButton
        prevTestButton = binding.prevButton

        startTestButton.visibility = View.INVISIBLE
        chapterTextView.movementMethod = ScrollingMovementMethod()
        val bundle = intent.extras

        if (bundle != null) {
            Log.i("ReadingChapterActivity", "Got chapter")
            chapter = bundle.getSerializable("chapter") as EntitiesProto.ChapterModel
            text = bundle.getSerializable("text") as String
            pos = bundle.getSerializable("position") as Int
            chapters = bundle.getSerializable("chapters") as EntitiesProto.ChapterList
        }

        if (chapter != null) {
            Log.i("ReadingChapterActivity", chapter!!.name)
        } else {
            Log.e("ReadingChapterActivity", "ERROR chapter is empty")
            finish()
            return
        }

        titleTextView.text = chapter!!.name
        tests = chapter!!.tests

        changeReadableText()
        Log.i("ReadingChapterActivity", "TEXT:$text")

        startTestButton.setOnClickListener {
            user.setUserByLogin(this, user.getUserLogin())
            if (startTestButton.text == "Back") {
                finish()
                return@setOnClickListener
            }
            if (startTestButton.visibility != View.INVISIBLE && tests != null && tests!!.testsList.size > currentTest) {
                val intent = Intent(this, TestActivity::class.java)

                val nextBundle = Bundle()
                nextBundle.putSerializable("test", tests?.testsList?.get(currentTest))
                nextBundle.putSerializable("chapter", chapter)
                intent.putExtras(nextBundle)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivityForResult(intent, 100)
                user.setUserByLogin(this, user.getUserLogin())
            }
        }

        prevTestButton.setOnClickListener {
            if (pos <= 0) {
                finish()
                return@setOnClickListener
            }
            pos--
            val chapterResponse = getChapter(chapters!!.chaptersList.get(pos), false, this, okHttpClient)

            if (chapterResponse == null) {
                Log.e("GroupActivity", "can't get chapter from sever")
            } else {
                Log.e("GroupActivity", chapterResponse.name)
                val intent = Intent(this, ReadingChapterActivity::class.java)
                val bundleNext = Bundle()
                Log.e("Tests", chapterResponse.tests.testsList.size.toString())
                bundleNext.putSerializable("chapter", chapterResponse)
                val text = getChapterText(chapterResponse, false, this, okHttpClient)
                bundleNext.putSerializable("text", text)
                bundleNext.putSerializable("chapters", chapters)
                bundleNext.putSerializable("position", pos)
                intent.putExtras(bundleNext)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            finish()
        }

        nextTestButton.setOnClickListener {
            pos++
            if (pos >= chapters!!.chaptersList.size) {
                finish()
                return@setOnClickListener
            }
            val chapterResponse = getChapter(chapters!!.chaptersList.get(pos), false, this, okHttpClient)

            if (chapterResponse == null) {
                Log.e("GroupActivity", "can't get chapter from sever")
            } else {
                val intent = Intent(this, ReadingChapterActivity::class.java)
                val bundleNext = Bundle()
                Log.e("Tests", chapterResponse.tests.testsList.size.toString())
                bundleNext.putSerializable("chapter", chapterResponse)
                val text = getChapterText(chapterResponse, false, this, okHttpClient)
                bundleNext.putSerializable("text", text)
                bundleNext.putSerializable("chapters", chapters)
                bundleNext.putSerializable("position", pos)
                intent.putExtras(bundleNext)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            finish()
        }

        chapterTextView.viewTreeObserver.addOnScrollChangedListener {
            if (!chapterTextView.canScrollVertically(1)) {
                if (tests != null && tests!!.testsList.size > currentTest) {
                    startTestButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun isPassedTest(test : EntitiesProto.TestModel) : Boolean {
        var isPassed = false
        val passedTests = user.getUserModel()!!.passedTests.passedTestsList
        for (passedTest in passedTests) {
            isPassed = isPassed || passedTest.testId == test.id
        }
        return isPassed
    }

    private fun isDeadlineOver(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val currentTimestamp = calendar.timeInMillis
        return timestamp < currentTimestamp
    }

    @SuppressLint("SetTextI18n")
    private fun changeReadableText() {
        while ((chapter!!.deadlineTs != -1L && isDeadlineOver(chapter!!.deadlineTs)) || (tests?.testsList?.size!! > currentTest && isPassedTest(tests!!.getTests(currentTest)))) {
            currentTest++;
        }
        if (tests?.testsList?.size!! > currentTest) {
            startTestButton.visibility = View.INVISIBLE
            chapterTextView.text = tests?.getTests(currentTest)?.position?.let { text.subSequence(0, it.toInt()) }
        } else {
            startTestButton.visibility = View.VISIBLE
            startTestButton.text = "Back"
            chapterTextView.text = text
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            currentTest++
            changeReadableText()
        }
    }
}