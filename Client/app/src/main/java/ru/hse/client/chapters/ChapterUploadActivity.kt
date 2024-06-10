package ru.hse.client.chapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import ru.hse.client.R
import ru.hse.client.databinding.ActivityUploadChapterBinding
import ru.hse.client.entry.hideKeyboard
import ru.hse.client.utility.DrawerBaseActivity

class ChapterUploadActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityUploadChapterBinding
    private lateinit var filepath: Uri
    private lateinit var text: String
    private var testStartPositions: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadChapterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            Log.d(this.localClassName, "uploadFileButton pressed")
            val intent = Intent(this, TestManagerActivity::class.java)
            intent.putExtra("text", binding.preview.text.toString())
            intent.putExtra("test number", testStartPositions)
            startActivity(intent)
        }
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

            if (positionEnd != positionStart) {
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
        }
    }

}