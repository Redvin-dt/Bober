package ru.hse.client.chapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import ru.hse.client.databinding.ActivityUploadChapterBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto
import java.io.*


class ChapterUploadActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityUploadChapterBinding
    private lateinit var filepath: Uri
    private lateinit var chapter: EntitiesProto.ChapterModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadChapterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chooseFileButton.setOnClickListener {
            Log.d("chooser", "0")
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

    }

    private fun addTest() {
        TODO("Not yet implemented")
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