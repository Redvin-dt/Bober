package ru.hse.client.chapters

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import ru.hse.client.databinding.ActivityCreateTestBinding
import ru.hse.client.databinding.ActivityTestManagerBinding
import ru.hse.client.entry.SignInActivity
import ru.hse.client.groups.GroupSelectMenuActivity
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto

class TestManagerActivity: DrawerBaseActivity() {

    private lateinit var binding: ActivityTestManagerBinding
    private lateinit var textForCurrentTest: String
    private var currentTestNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textForCurrentTest = intent.extras?.get("text") as String
        currentTestNumber = intent.extras?.get("test number") as Int

        binding.testNumber.text = "Text for Test ${currentTestNumber}"
        binding.preview.text = textForCurrentTest

        binding.createTest.setOnClickListener {
            if (binding.testName.text.toString().isEmpty()) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this,
                        "Enter the name of the test",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val intent = Intent(this, CreateTestActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("test name", binding.testName.text.toString())
                startActivityForResult(intent, 211)
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 211 && resultCode == RESULT_OK) {
            val currentTestInBytes = data!!.extras!!.getByteArray("test data in protobuf")
            val currentTestProto = EntitiesProto.TestModel.parseFrom(currentTestInBytes)


            /*if (currentTestNumber == testStartPositions.size - 1) {
                // Upload to server
            } else {
                binding.testNumber.text = "Text for Test ${currentTestNumber + 1}"
                binding.preview.text = partsOfTextForTests[currentTestNumber]

                binding.createTest.setOnClickListener {
                    val intent = Intent(this, CreateTestActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.extras!!.putInt("new test number", currentTestNumber + 1)
                    intent.extras!!.putInt("number of tests", testStartPositions.size)
                    startActivityForResult(intent, 211)
                }
            }*/
        }
    }

}