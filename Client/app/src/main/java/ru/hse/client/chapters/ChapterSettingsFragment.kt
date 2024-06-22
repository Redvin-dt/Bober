package ru.hse.client.chapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import ru.hse.client.R
import ru.hse.client.databinding.FragmentChapterSettingsBinding
import androidx.fragment.app.FragmentActivity
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

class ChapterSettingsFragment() :
    Fragment(R.layout.fragment_chapter_settings), DatePickerFragment.DatePickerListener,
    TimePickerFragment.TimePickerListener {
    private lateinit var binding: FragmentChapterSettingsBinding
    private lateinit var selectedDate: String
    private lateinit var selectedTime: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setDeadline.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                binding.deadlinePreview.setText(R.string.sample_date)
                binding.deadlinePreview.setTextColor(resources.getColor(R.color.light_gray))
            } else {
                DatePickerFragment().show(childFragmentManager, "datePicker")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChapterSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun cancelChooser() {
        binding.setDeadline.isChecked = false
        binding.deadlinePreview.setText(R.string.sample_date)
        binding.deadlinePreview.setTextColor(resources.getColor(R.color.light_gray))
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {

        Log.i("ChapterSettingsFragment", "onDateSet")
        selectedDate = if (day < 10) {
            "0$day-"
        } else {
            "$day-"
        }
        selectedDate += if (month + 1 < 10) {
            "0${month + 1}-$year"
        } else {
            "${month + 1}-$year"
        }
        TimePickerFragment().show(childFragmentManager, "timePicker")
    }

    override fun onDatePickerCanceled() {
        cancelChooser()
    }

    override fun onDatePickerDismissed() {

    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        selectedTime = if (hourOfDay < 10) {
            "0$hourOfDay:"
        } else {
            "$hourOfDay:"
        }
        selectedTime += if (minute < 10) {
            "0$minute"
        } else {
            "$minute"
        }
        binding.deadlinePreview.setTextColor(resources.getColor(R.color.dark_gray))
        val date = "$selectedDate $selectedTime"
        binding.deadlinePreview.text = date
    }

    override fun onTimePickerCanceled() {
        cancelChooser()
    }

    override fun onTimePickerDismissed() {

    }

    fun getDeadline(): Long {
        if (!binding.setDeadline.isChecked) {
            return -1
        }
        val date = binding.deadlinePreview.text
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, date.subSequence(6, 10).toString().toInt())
        calendar.set(Calendar.MONTH, date.subSequence(3, 5).toString().toInt())
        calendar.set(Calendar.DAY_OF_MONTH, date.subSequence(0, 2).toString().toInt())
        calendar.set(Calendar.HOUR_OF_DAY, date.subSequence(11, 13).toString().toInt())
        calendar.set(Calendar.MINUTE, date.subSequence(15, date.length).toString().toInt())
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val timestamp = calendar.timeInMillis
        return timestamp
    }
}
