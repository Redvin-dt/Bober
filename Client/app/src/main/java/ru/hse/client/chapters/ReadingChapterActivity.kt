package ru.hse.client.chapters

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import ru.hse.client.databinding.ActivityReadingChapterBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto

class ReadingChapterActivity : DrawerBaseActivity() {
    private lateinit var binding: ActivityReadingChapterBinding
    private lateinit var chapterTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var startTestButton: Button
    private var currentTest: Int = 0
    private var chapter: EntitiesProto.ChapterModel? = null
    private var tests: EntitiesProto.TestList? = null
    private var text: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityReadingChapterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chapterTextView = binding.contentTextView
        titleTextView = binding.titleTextView
        startTestButton = binding.startTestButton
        startTestButton.visibility = View.INVISIBLE
        chapterTextView.movementMethod = ScrollingMovementMethod()
        val bundle = intent.extras

        if (bundle != null) {
            Log.e("ReadingChapterActivity", "Got chapter")
            chapter = bundle.getSerializable("chapter") as EntitiesProto.ChapterModel
            text = bundle.getSerializable("text") as String
        }

        if (chapter != null) {
            Log.i("ReadingChapterActivity", chapter!!.name)
        } else {
            Log.e("ReadingChapterActivity", "ERROR chapter is empty")
            finish()
            return
        }


        tests = chapter!!.tests

        changeReadableText()
        Log.i("ReadingChapterActivity", "TEXT:$text")

        startTestButton.setOnClickListener {
            if (startTestButton.visibility != View.INVISIBLE && tests != null && tests!!.testsList.size > currentTest) {
                val intent = Intent(this, TestActivity::class.java)

                val nextBundle = Bundle()
                nextBundle.putSerializable("test", tests?.testsList?.get(currentTest))
                intent.putExtras(nextBundle)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivityForResult(intent, 100)
            }
        }

        chapterTextView.viewTreeObserver.addOnScrollChangedListener {
            if (!chapterTextView.canScrollVertically(1)) {
                if (tests != null && tests!!.testsList.size > currentTest) {
                    startTestButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun changeReadableText() {
        if (tests?.testsList?.size!! > currentTest) {
            chapterTextView.text = tests?.getTests(currentTest)?.position?.let { text.subSequence(0, it.toInt()) }
        } else {
            startTestButton.visibility = View.INVISIBLE
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