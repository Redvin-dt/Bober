package ru.hse.client.groups

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import ru.hse.client.R

class ListMemberAdapter(context: Context, dataArrayList: ArrayList<ListMemberData?>?) :
    ArrayAdapter<ListMemberData?>(context, R.layout.members_list_item, dataArrayList!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val listData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.members_list_item, parent, false)
        }
        val memberName = view!!.findViewById<TextView>(R.id.user_name)

        memberName!!.text = listData!!.name
        memberName.ellipsize = TextUtils.TruncateAt.END
        memberName.maxLines = 1

        return view
    }
}