package ru.hse.client.groups

import android.os.Bundle
import android.util.Log
import ru.hse.client.databinding.ActivityInvitesBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.client.utility.user

class InvitesActivity : DrawerBaseActivity() {
    private lateinit var binding: ActivityInvitesBinding
    private lateinit var dataArrayList: ArrayList<InvitesData?>
    private lateinit var inviteViewAdapter: InvitesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvitesBinding.inflate(layoutInflater)

        dataArrayList = ArrayList()
        inviteViewAdapter = InvitesListAdapter(this@InvitesActivity, this, dataArrayList)

        setContentView(binding.root)
        allocateActivityTitle("Invites")

        binding.invitesList.adapter = inviteViewAdapter

        drawUserInvites()
    }

    private fun drawUserInvites() {
        val invites = user.getUserInvites()

        Log.i("Invite activity", "invites are:\n$invites")

        dataArrayList.clear()

        for (group in invites) {
            dataArrayList.add(InvitesData(group.id, group.name, group.admin.login))
        }

        inviteViewAdapter.notifyDataSetChanged()
    }
}