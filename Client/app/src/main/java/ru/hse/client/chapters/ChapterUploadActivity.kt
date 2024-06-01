package ru.hse.client.chapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils.isEmpty
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import ru.hse.client.databinding.ActivityUploadChapterBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.ChapterModel
import ru.hse.server.proto.EntitiesProto.UserModel
import java.io.*
import kotlin.math.abs

class ChapterUploadActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityUploadChapterBinding
    private lateinit var filepath: Uri
    private var testStartPositions: ArrayList<Int> = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadChapterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chooseFileButton.setOnClickListener {
            Log.d("chooser", "0")
            testStartPositions.clear()
            startFileChooser()
        }

        binding.helpButton.setOnClickListener {
            Log.d("help", "0")
            showHelpInfo()
        }

        binding.addTest.setOnClickListener {
            Log.d("add test", "0")
            addTest()
        }

        binding.uploadFileButton.setOnClickListener {
            Log.d("upload", "0")
            val intent = Intent(this, CreateTestActivity::class.java)
            intent.putExtra("startPositions", testStartPositions)
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
            Log.i("info", "cursor pos $positionStart")
            if (testStartPositions.stream().anyMatch { abs(it - positionStart) < 20 }) {
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
            val text = istream!!.bufferedReader().use { it.readText() }
            istream.close()
            binding.preview.setText(text)
        }
    }

    private fun readText(uri: Uri): String {
        val f: File = File(uri.toString())
        var inputStream: FileInputStream? = null
        try {
            inputStream = FileInputStream(f)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        var i: Int
        try {
            i = inputStream!!.read()
            while (i != -1) {
                byteArrayOutputStream.write(i)
                i = inputStream!!.read()
            }
            inputStream!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return byteArrayOutputStream.toString()
    }

}