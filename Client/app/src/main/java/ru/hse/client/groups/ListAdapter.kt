package ru.hse.client.groups

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import ru.hse.client.R

class ListAdapter(context: Context, dataArrayList: ArrayList<ListData?>?) :
ArrayAdapter<ListData?>(context, R.layout.group_list_item, dataArrayList!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val listData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.group_list_item, parent, false)
        }
        val listImage = view!!.findViewById<ImageView>(R.id.list_img)
        val listName = view.findViewById<TextView>(R.id.list_name)
        val listAdmin = view.findViewById<TextView>(R.id.list_admin)

        listImage.setImageResource(listData!!.image)
        listName.text = listData.name
        listAdmin.text = listData.admin

        return view
    }

}