package ru.hse.client.chapters

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import ru.hse.client.R
import ru.hse.client.databinding.ActivityCreateSettingsForTestBinding
import ru.hse.client.databinding.ActivityCreateTestBinding
import ru.hse.client.utility.DrawerBaseActivity
import java.text.SimpleDateFormat

class CreateSettingsForTestActivity: DrawerBaseActivity() {

    private lateinit var binding: ActivityCreateSettingsForTestBinding
    private var currentTime = "00:00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSettingsForTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.progressDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
        binding.progressBar.max = 3
        binding.progressBar.progress = 3

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            if (source.matches("[0-9]*".toRegex()) || source.matches(":*".toRegex())) {
                source
            } else {
                ""
            }
        }
        binding.timeLimitPreview.filters = arrayOf(filter)

        binding.setTl.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                binding.timeLimitPreview.setHint(R.string._00_00)
                binding.timeLimitPreview.setHintTextColor(resources.getColor(R.color.light_gray))
                binding.timeLimitPreview.setTextColor(resources.getColor(R.color.light_gray))
            } else {
                binding.timeLimitPreview.setHint(R.string._00_00)
                binding.timeLimitPreview.setHintTextColor(resources.getColor(R.color.dark_gray_chapter))
                binding.timeLimitPreview.setTextColor(resources.getColor(R.color.dark_gray_chapter))
            }
        }

        binding.testCreated.setOnClickListener {
            if (!correctFormat()) {
                Toast.makeText(this, "Enter the time in the format hh:mm", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            var timeLimit: Long = -1L
            if (binding.setTl.isChecked) {
                val minutes = binding.timeLimitPreview.text.subSequence(0, 2).toString()
                val seconds = binding.timeLimitPreview.text.subSequence(3, binding.timeLimitPreview.text.length).toString()
                timeLimit = minutes.toLong() * 60 + seconds.toLong()
            }
            val data: Intent = Intent()
            setResult(RESULT_OK, data)
            data.putExtra("secondsForTest", timeLimit)
            finish()
        }
    }

    private fun correctFormat(): Boolean {
        val timePattern = Regex("^\\d{2}:\\d{2}\$")
        return timePattern.matches(binding.timeLimitPreview.text)
    }

}