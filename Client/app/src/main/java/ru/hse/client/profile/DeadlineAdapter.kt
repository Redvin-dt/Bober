package ru.hse.client.profile

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import ru.hse.client.R
import ru.hse.client.profile.DeadlineData
import ru.hse.client.profile.TestResultData

class DeadlineAdapter(context: Context, dataArrayList: ArrayList<DeadlineData?>?) :
    ArrayAdapter<DeadlineData?>(context, R.layout.deadline_list_item, dataArrayList!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val listData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.deadline_list_item, parent, false)
        }
        val listGroup = view!!.findViewById<TextView>(R.id.chapter)
        val listChapter = view.findViewById<TextView>(R.id.group)
        val listTestsRemaining = view.findViewById<TextView>(R.id.tests_passed)
        val listDeadline = view.findViewById<TextView>(R.id.deadline)

        listGroup.text = listData!!.group
        listChapter.text = listData.chapter
        listTestsRemaining.text = listData.testsRemaining
        listDeadline.text = listData.deadline

        return view
    }

}