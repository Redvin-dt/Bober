package ru.hse.client.chapters

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import com.google.android.material.appbar.AppBarLayout.LiftOnScrollListener
import com.google.android.material.textfield.TextInputEditText
import ru.hse.client.R

class AnswerAdapter(context: Context, private val dataArrayList: MutableList<AnswerData>) :
    ArrayAdapter<AnswerData>(context, R.layout.answer_list_item, dataArrayList) {

    private val itemsPadding = 10F
    private val enteredTextMap = mutableMapOf<Int, String>()
    private val conditionMap = mutableMapOf<Int, Boolean>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.answer_list_item, parent, false)

        val textWidget = view.findViewById<TextInputEditText>(R.id.answer)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)

        val listAnswerData = dataArrayList[position]
        textWidget.hint = listAnswerData.hint

        if (conditionMap.containsKey(position)) {
            conditionMap[position]?.let { checkBox.setChecked(it) }
        } else {
            checkBox.isChecked = listAnswerData.isCorrect
            conditionMap[position] = listAnswerData.isCorrect
        }

        if (enteredTextMap.containsKey(position)) {
            textWidget.setText(enteredTextMap[position])
        } else {
            textWidget.text = listAnswerData.answer
            enteredTextMap[position] = listAnswerData.answer.toString()
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            conditionMap[position] = isChecked
            onCheckBoxCheckedListener?.onCheckBoxChecked(position, isChecked)
        }

        textWidget.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                enteredTextMap[position] = s.toString()
            }
        })

        view.translationY = itemsPadding

        //view.translationY = -itemsPadding

        return view
    }

    override fun getItem(position: Int): AnswerData {
        return dataArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    fun getAnswerDataByPosition(position: Int): Bundle {
        val state = Bundle()
        state.putString("answer text", enteredTextMap[position])
        state.putBoolean("checkBox state", conditionMap[position]!!)
        return state
    }

    fun removeItem(position: Int) {
        dataArrayList.removeAt(position)
        enteredTextMap.remove(position)
        conditionMap.remove(position)
        notifyDataSetChanged()
    }

    fun updateCheckboxState(position: Int, isChecked: Boolean) {
        dataArrayList[position].isCorrect = isChecked
        conditionMap[position] = isChecked
        notifyDataSetChanged()
    }

    interface OnCheckBoxListener {
        fun onCheckBoxChecked(position: Int, isChecked: Boolean)
    }

    private var onCheckBoxCheckedListener: OnCheckBoxListener? = null
    fun setOnCheckboxCheckedListener(listener: OnCheckBoxListener) {
        onCheckBoxCheckedListener = listener
    }
}
