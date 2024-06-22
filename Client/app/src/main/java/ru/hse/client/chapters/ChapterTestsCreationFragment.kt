package ru.hse.client.chapters

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.BufferedSink
import okio.ByteString
import okio.source
import ru.hse.client.R
import ru.hse.client.databinding.FragmentChapterCreateTestsBinding
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.GroupModel
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ChapterTestsCreationFragment(activity: ChapterCreateActivity, testStartPositions: MutableList<Int>, testListBuilder: EntitiesProto.TestList.Builder) : Fragment(R.layout.fragment_chapter_create_tests) {
    private var mActivity: ChapterCreateActivity = activity
    private lateinit var binding: FragmentChapterCreateTestsBinding
    private var mTestStartPositions: MutableList<Int> = testStartPositions
    private var mTestListBuilder: EntitiesProto.TestList.Builder = testListBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.helpButton.setOnClickListener {
            Log.d("ChapterTestsCreationFragment", "helpButton pressed")
            showHelpInfo()
        }

        binding.addTest.setOnClickListener {
            Log.d("ChapterTestsCreationFragment", "addTestButton pressed")
            addTest()
        }
    }

    private fun addTest() {
        if (isEmpty(binding.preview.text)) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    mActivity,
                    "To begin, select a file",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        } else {
            val positionStart = binding.preview.selectionStart
            val positionEnd = binding.preview.selectionEnd

            if (positionStart == 0 || positionEnd != positionStart) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        mActivity,
                        "Please select the location after which the test should start",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            Log.i(mActivity.toString(), "cursor pos $positionStart")
            if (mTestStartPositions.isNotEmpty() && positionStart - mTestStartPositions.last() <= 20) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        mActivity,
                        "Distance between tests is too small (less than 20 characters)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            mTestStartPositions.add(positionStart)
            val intent = Intent(mActivity, TestManagerActivity::class.java)
            intent.putExtra(
                "text",
                binding.preview.text.toString().subSequence(
                    if (mTestStartPositions.size == 1) 0 else mTestStartPositions[mTestStartPositions.size - 2],
                    mTestStartPositions.last()
                ),
            )
            intent.putExtra("test number", mTestStartPositions.size)
            intent.putExtra("test start position", mTestStartPositions.last())
            startActivityForResult(intent, 121)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 121 && resultCode == Activity.RESULT_OK && data != null) {
            if (data.extras!!.get("go back button pressed") != null) {
                mTestStartPositions.removeLast()
            } else {
                val currentTestInBytes = data.extras!!.getByteArray("test model")
                val currentTestProto = EntitiesProto.TestModel.parseFrom(currentTestInBytes)
                mTestListBuilder.addTests(currentTestProto)
            }
        }
    }

    private fun showHelpInfo() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(mActivity)
        builder
            .setTitle("How to upload a file and create tests")
            .setMessage(
                "Select a file using the \"Choose\" button. Next, mark up the text. " +
                        "Place the cursor in the place in the text after which the next test should begin and " +
                        "click on the “+” button. When you finish uploading, click the \"Upload\" button.\n"
            )
            .setNegativeButton("I get it!", null)


        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun setText(text: String) {
        binding.preview.setText(text)
    }

    fun isTextEmpty(): Boolean {
        return isEmpty(binding.preview.text)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChapterCreateTestsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}