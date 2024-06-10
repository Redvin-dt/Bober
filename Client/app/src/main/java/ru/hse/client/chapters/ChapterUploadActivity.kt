package ru.hse.client.chapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.ByteString
import ru.hse.client.R
import ru.hse.client.databinding.ActivityUploadChapterBinding
import ru.hse.client.entry.hideKeyboard
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.client.utility.printErrorAboutBadUser
import ru.hse.client.utility.printMessageFromBadResponse
import ru.hse.client.utility.printOkAboutGoodUser
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.ChapterModel
import ru.hse.server.proto.EntitiesProto.GroupModel
import ru.hse.server.proto.EntitiesProto.TestList
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CountDownLatch

class ChapterUploadActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityUploadChapterBinding
    private lateinit var filepath: Uri
    private lateinit var text: String
    private lateinit var group: GroupModel
    private var testStartPositions: MutableList<Int> = ArrayList()
    private lateinit var testListBuilder: TestList.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadChapterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val groupInBytes = intent!!.extras!!.get("group info") as ByteArray
        group = GroupModel.parseFrom(groupInBytes)

        testListBuilder = TestList.newBuilder()

        binding.chooseFileButton.setOnClickListener {
            Log.d(this.localClassName, "chooseFileButton pressed")
            testStartPositions.clear()
            startFileChooser()
        }

        binding.helpButton.setOnClickListener {
            Log.d(this.localClassName, "helpButton pressed")
            showHelpInfo()
        }

        binding.addTest.setOnClickListener {
            Log.d(this.localClassName, "addTestButton pressed")
            addTest()
            val intent = Intent(this, TestManagerActivity::class.java)
            intent.putExtra(
                "text",
                binding.preview.text.toString().subSequence(
                    if (testStartPositions.size == 1) 0 else testStartPositions[testStartPositions.size - 2],
                    testStartPositions.last()
                ),
            )
            intent.putExtra("test number", testStartPositions.size)
            startActivity(intent)
        }

        binding.uploadFileButton.setOnClickListener {
            if (isEmpty(binding.preview.text)) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this,
                        "To begin, select a file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (isEmpty(binding.chapterName.text)) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this,
                        "Name the chapter",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Log.d(this.localClassName, "uploadFileButton pressed")
                val chapterModel = ChapterModel.newBuilder()
                    .setName(binding.chapterName.text.toString())
                    .setTextFile(text)
                    .setTests(testListBuilder)
                    .setGroup(group)
                    .build()

                sendChapterToServer(chapterModel)
                finish()
            }
        }
    }

    private fun sendChapterToServer(chapterModel: ChapterModel) {
        val URlGetUser: String =
            ("http://" + ContextCompat.getString(this, R.string.IP) + "/chapters").toHttpUrlOrNull()
                ?.newBuilder()
                ?.build()
                .toString()

        val requestBody: RequestBody =
            RequestBody.create("application/x-protobuf".toMediaTypeOrNull(), chapterModel.toByteArray())

        val requestForAddChapter: Request = Request.Builder()
            .url(URlGetUser)
            .header("Authorization", "Bearer ${user.getUserToken()}")
            .post(requestBody)
            .build()

        val okHttpClient = OkHttpClient()
        val countDownLatch = CountDownLatch(1)
        okHttpClient.newCall(requestForAddChapter).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(this@ChapterUploadActivity.toString(), e.toString() + " " + e.message)
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this@ChapterUploadActivity,
                        "Something went wrong, try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i(this@ChapterUploadActivity.toString(), response.toString())
                if (response.isSuccessful) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this@ChapterUploadActivity,
                            "The chapter has been added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this@ChapterUploadActivity,
                            "Something went wrong, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                countDownLatch.countDown()
            }
        })
        countDownLatch.await()
    }

    private fun addTest() {
        if (isEmpty(binding.preview.text)) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    this,
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
                        this,
                        "Please select the location after which the test should start",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            Log.i(this.localClassName, "cursor pos $positionStart")
            if (testStartPositions.isNotEmpty() && positionStart - testStartPositions.last() <= 20) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this,
                        "Distance between tests is too small (less than 20 characters)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            testStartPositions.add(positionStart)
            val intent = Intent(this, TestManagerActivity::class.java)
            intent.putExtra(
                "text",
                binding.preview.text.toString().subSequence(
                    if (testStartPositions.size == 1) 0 else testStartPositions[testStartPositions.size - 2],
                    testStartPositions.last()
                ),
            )
            intent.putExtra("test number", testStartPositions.size)
            intent.putExtra("test start position", testStartPositions.last())
            startActivityForResult(intent, 121)
        }
    }

    private fun showHelpInfo() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
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

    private fun startFileChooser() {
        val intent = Intent()
        intent.setType("text/plain").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Choose file"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            filepath = data.data!!
            val istream = contentResolver.openInputStream(filepath)
            text = istream!!.bufferedReader().use { it.readText() }
            istream.close()
            binding.preview.setText(text)
        } else if (requestCode == 121 && resultCode == Activity.RESULT_OK && data != null) {
            if (data.extras!!.get("go back button pressed") != null) {
                testStartPositions.removeLast()
            } else {
                val currentTestInBytes = data.extras!!.getByteArray("test model")
                val currentTestProto = EntitiesProto.TestModel.parseFrom(currentTestInBytes)
                testListBuilder.addTests(currentTestProto)
            }
        }
    }
}