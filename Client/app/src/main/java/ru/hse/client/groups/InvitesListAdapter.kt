package ru.hse.client.groups

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import okhttp3.OkHttpClient
import ru.hse.client.R

class InvitesData (val groupId : Long, val groupName : String, val groupAdmin : String)

class InvitesListAdapter(private val activity: InvitesActivity, context : Context, dataArrayList: ArrayList<InvitesData?>)  : ArrayAdapter<InvitesData?>(context, R.layout.invite_list_item, dataArrayList) {

    private val okHttpClient = OkHttpClient()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val listData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.invite_list_item, parent, false)
        }

        val listName = view!!.findViewById<TextView>(R.id.list_name)
        val listAdmin = view.findViewById<TextView>(R.id.list_admin)
        val acceptButton = view.findViewById<ImageButton>(R.id.accept_button)
        val cancelButton = view.findViewById<ImageButton>(R.id.cancel_button)

        listName.text = listData!!.groupName
        listAdmin.text = listData.groupAdmin

        acceptButton.setOnClickListener {
            if (acceptInvite(listData)) {
                remove(listData)
                notifyDataSetChanged()
            }
        }

        cancelButton.setOnClickListener{
            if (declineInvite(listData)) {
                remove(listData)
                notifyDataSetChanged()
            }
        }

        return view
    }

    private fun acceptInvite(data : InvitesData?) : Boolean {
        if (data == null) {
            return false
        }

        return acceptInvite(data.groupId, activity, okHttpClient)
    }

    private fun declineInvite(data : InvitesData?) : Boolean {
        if (data == null) {
            return false
        }

        return declineInvite(data.groupId, activity, okHttpClient)
    }
}

