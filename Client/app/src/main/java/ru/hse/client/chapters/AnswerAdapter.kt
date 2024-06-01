package ru.hse.client.chapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.CheckBox
import ru.hse.client.R


class AnswerAdapter(context: Context, dataArrayList: ArrayList<AnswerData>) :
    ArrayAdapter<AnswerData>(context, R.layout.answer_list_item, dataArrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val listAnswerData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.answer_list_item, parent, false)
        }
        val answer = view!!.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.answer)
        val isCorrect = view.findViewById<CheckBox>(R.id.checkBox)
        if (listAnswerData != null) {
            answer.hint = listAnswerData.answer
        }
        if (listAnswerData != null) {
            isCorrect.isChecked = listAnswerData.isCorrect
        }

        view.translationY = 40F

        return view
    }

}