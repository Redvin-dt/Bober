package ru.hse.client.chapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import ru.hse.client.R

class ListAnswerAdapter(context: Context, dataArrayList: ArrayList<ListTestData?>?) :
    ArrayAdapter<ListTestData?>(context, R.layout.answer_test_list_item, dataArrayList!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val listData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.answer_test_list_item, parent, false)
        }

        val listNum = view!!.findViewById<TextView>(R.id.number)
        val listAnswer = view.findViewById<TextView>(R.id.answer)

        // listImage.setImageResource(listData!!.image)
        listNum!!.text = listData!!.number.toString()
        listAnswer!!.text = listData.answer

        return view
    }
}