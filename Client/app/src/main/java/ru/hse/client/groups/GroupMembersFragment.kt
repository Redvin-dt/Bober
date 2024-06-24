package ru.hse.client.groups

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import ru.hse.client.R
import ru.hse.client.chapters.ReadingChapterActivity
import ru.hse.client.chapters.getChapter
import ru.hse.client.databinding.FragmentGroupChaptersBinding
import ru.hse.client.databinding.FragmentGroupMembersBinding
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto

class GroupMembersFragment(activity: GroupActivity, groupModel: EntitiesProto.GroupModel) : Fragment() {
    private lateinit var binding: FragmentGroupMembersBinding
    private var mGroupModel = groupModel
    private var mActivity = activity
    private lateinit var dataArrayList: ArrayList<ListMemberData?>
    private lateinit var listViewAdapter: ListMemberAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataArrayList = ArrayList()
        listViewAdapter = ListMemberAdapter(mActivity, dataArrayList)

        if (user.getId() != mGroupModel.admin.id) {
            binding.inviteMemberButton.visibility = View.INVISIBLE
        }
        binding.inviteMemberButton.setOnClickListener {
            inviteButtonPressed()
        }

        drawGroupMembersList()
    }

    private fun inviteButtonPressed() {
        val intent = Intent(activity, InviteActivity::class.java)
        val bundle = Bundle()
        bundle.putLong("groupId", mGroupModel.id)
        bundle.putString("groupName", mGroupModel.name)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun drawGroupMembersList() {
        binding.adminName.text = mGroupModel.admin.login
        Log.i("GroupMembersFragment", "Admin login $mGroupModel.admin.login")

        val users: EntitiesProto.UserList = mGroupModel.users
        val stringBuilder: StringBuilder = StringBuilder()

        for (i in users.usersList) {
            stringBuilder.append(i.login)
            stringBuilder.append(" ")
        }

        Log.i("GroupMembersFragment", "Users in the group$stringBuilder")
        drawMembersList(users.usersList, mGroupModel.admin.login)
    }

    private fun drawMembersList(users: List<EntitiesProto.UserModel>, adminName: String) {

        dataArrayList.clear()
        for (user in users) {
            if (user.login != adminName) {
                dataArrayList.add(ListMemberData(user.login))
            }
        }

        listViewAdapter = ListMemberAdapter(mActivity, dataArrayList)
        binding.groupMembersList.adapter = listViewAdapter
        binding.groupMembersList.isClickable = true
        listViewAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupMembersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}