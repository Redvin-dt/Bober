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

        val listNum = view!!.findViewById<TextView>(R.id.list_number)
        val listName = view.findViewById<TextView>(R.id.list_name)
        val listProgressBar = view.findViewById<ProgressBar>(R.id.progress_bar)

        // listImage.setImageResource(listData!!.image)
        listNum!!.text = (1 + listData!!.number).toString()
        listName!!.text = listData.name
        listName.ellipsize = TextUtils.TruncateAt.END
        listName.maxLines = 1
        listProgressBar.progressDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
        listProgressBar!!.max = 100
        listProgressBar.progress = (listData.passedTests.toDouble() / listData.tests.toDouble() * 100).toInt()
        return view
    }
}