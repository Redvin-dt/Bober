package ru.hse.client.groups

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.GroupModel


class GroupPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    activity: GroupActivity,
    groupModel: GroupModel
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private var mGroupModel = groupModel
    private var mActivity = activity

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                GroupChaptersFragment(mActivity, mGroupModel)
            }

            1 -> {
                GroupMembersFragment(mActivity, mGroupModel)
            }

            else -> {
                GroupScoreboardFragment(mActivity, mGroupModel)
            }
        }
    }
}