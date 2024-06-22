package ru.hse.client.chapters

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Window
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener,
    DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    interface DatePickerListener {
        fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int)
        fun onDatePickerCanceled()
        fun onDatePickerDismissed()
    }

    private lateinit var datePickerListener: DatePickerListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        datePickerListener = parentFragment as DatePickerListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireContext(),
            AlertDialog.THEME_HOLO_LIGHT,
            this,
            year,
            month,
            day
        ).apply {
            setOnCancelListener(this@DatePickerFragment)
            setOnDismissListener(this@DatePickerFragment)
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        datePickerListener.onDateSet(view, year, month, day)
    }

    override fun onCancel(dialog: DialogInterface) {
        datePickerListener.onDatePickerCanceled()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        datePickerListener.onDatePickerDismissed()
    }
}

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    interface TimePickerListener {
        fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int)
        fun onTimePickerCanceled()
        fun onTimePickerDismissed()
    }

    private lateinit var timePickerListener: TimePickerListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        timePickerListener = parentFragment as TimePickerListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return TimePickerDialog(
            requireContext(),
            AlertDialog.THEME_HOLO_LIGHT,
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(requireContext())
        ) .apply {
            setOnCancelListener(this@TimePickerFragment)
            setOnDismissListener(this@TimePickerFragment)
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        timePickerListener.onTimeSet(view, hourOfDay, minute)
    }

    override fun onCancel(dialog: DialogInterface) {
        timePickerListener.onTimePickerCanceled()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        timePickerListener.onTimePickerDismissed()
    }
}

