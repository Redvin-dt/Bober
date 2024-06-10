package ru.hse.client.chapters

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
<<<<<<< HEAD
import android.text.method.ScrollingMovementMethod
=======
>>>>>>> 37e1117 (update ui and small fixes)
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
    private var currentTestNumber: Int = -1
    private var testStartPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textForCurrentTest = intent.extras?.get("text") as String
        currentTestNumber = intent.extras?.get("test number") as Int
        testStartPosition = intent.extras?.get("test start position") as Int

        binding.testNumber.text = "Text for Test ${currentTestNumber}"
        binding.preview.text = textForCurrentTest
        binding.preview.movementMethod = ScrollingMovementMethod()

        binding.createTestButton.setOnClickListener {
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
                intent.putExtra("test number", currentTestNumber)
                intent.putExtra("test start position", testStartPosition)
                startActivityForResult(intent, 211)
            }
        }

        binding.goBackButton.setOnClickListener {
            val data: Intent = Intent()
            setResult(RESULT_OK, data)
            data.putExtra("go back button pressed", true)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 211 && resultCode == RESULT_OK && data != null) {
            val currentTestInBytes = data.extras!!.getByteArray("test model")
            val intent: Intent = Intent()
            setResult(RESULT_OK, intent)
            intent.putExtra("test model", currentTestInBytes)
            finish()
        }
    }

}