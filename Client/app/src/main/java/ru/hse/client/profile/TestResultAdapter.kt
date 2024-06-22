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
import ru.hse.client.profile.TestResultData

class TestResultAdapter(context: Context, dataArrayList: ArrayList<TestResultData?>?) :
    ArrayAdapter<TestResultData?>(context, R.layout.result_list_item, dataArrayList!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val listData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.result_list_item, parent, false)
        }
        val listGroupChapter = view!!.findViewById<TextView>(R.id.group_chapter)
        val listLogin = view.findViewById<TextView>(R.id.login_user)
        val listTest = view.findViewById<TextView>(R.id.test_name_res)
        val listResult = view.findViewById<TextView>(R.id.result)

        listGroupChapter.text = listData!!.groupAndChapter
        listLogin.text = listData.login
        listTest.text = listData.testName
        listResult.text = listData.testResult

        return view
    }

}