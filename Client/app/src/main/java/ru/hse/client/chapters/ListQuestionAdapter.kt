package ru.hse.client.chapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import ru.hse.client.R

class ListQuestionAdapter(context: Context, dataArrayList: ArrayList<ListQuestionData?>?) :
    ArrayAdapter<ListQuestionData?>(context, R.layout.answer_test_list_item, dataArrayList!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val listData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.answer_test_list_item, parent, false)
        }

        val listCheckBox = view!!.findViewById<CheckBox>(R.id.checkBox)
        val listAnswer = view.findViewById<TextView>(R.id.answer)

        // listImage.setImageResource(listData!!.image)
        listCheckBox!!.isSelected = listData!!.isSelected
        listAnswer!!.text = listData.answer
        listCheckBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckBoxCheckedListener?.onCheckBoxChecked(position, isChecked)
        }

        return view
    }

    interface OnCheckBoxListener {
        fun onCheckBoxChecked(position: Int, isChecked: Boolean)
    }

    private var onCheckBoxCheckedListener: OnCheckBoxListener? = null
    fun setOnCheckboxCheckedListener(listener: OnCheckBoxListener) {
        onCheckBoxCheckedListener = listener
    }
}