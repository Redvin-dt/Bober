package ru.hse.client.groups

import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.InspectableProperty
import ru.hse.client.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ListChapterAdapter(context: Context, dataArrayList: ArrayList<ListChapterData?>?) :
    ArrayAdapter<ListChapterData?>(context, R.layout.chapter_list_item, dataArrayList!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val listData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.chapter_list_item, parent, false)
        }
        // TODO Book image somewhere
        // val listImage = view!!.findViewById<ImageView>(R.id.list_img)

        val listName = view!!.findViewById<TextView>(R.id.list_name)
        val listProgressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val listDeadline = view.findViewById<TextView>(R.id.nearest_deadline)
        val listIcon = view.findViewById<ImageView>(R.id.is_lock_img)

        // listImage.setImageResource(listData!!.image)
        listName!!.text = listData!!.name
        listName.ellipsize = TextUtils.TruncateAt.END
        listName.maxLines = 1
        listProgressBar.progressDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        listProgressBar!!.max = 100
        listProgressBar.progress = (listData.passedTests.toDouble() / listData.tests.toDouble() * 100).toInt()
        if (listData.timestamp != -1L) {
            listDeadline.text = getTimeFromTimestamp(listData.timestamp)
        } else {
            listDeadline.text = ""
        }

        if (listData.passedTests == listData.tests) {
            listIcon.setImageResource(R.drawable.chapter_done)
            listIcon.setBackgroundResource(R.drawable.border_green)
        } else if (listData.timestamp != -1L && !isDeadlineOver(listData.timestamp)) {
            listIcon.setImageResource(R.drawable.chapter_done)
            listIcon.setBackgroundResource(R.drawable.dotted_border_green)
        } else {
            listIcon.setImageResource(R.drawable.chapter_close)
            listIcon.setBackgroundResource(R.drawable.border_red)
        }

        return view
    }

    private fun isDeadlineOver(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val currentTimestamp = calendar.timeInMillis
        return timestamp < currentTimestamp
    }

    private fun getTimeFromTimestamp(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)

        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
        val date = Date(timestamp)
        val formattedDate = dateFormat.format(date)
        return "Deadline: $formattedDate"
    }
}