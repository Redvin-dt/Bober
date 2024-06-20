package ru.hse.client.groups

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.hse.client.databinding.FragmentGroupScoreboardBinding
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.UserModel

class GroupScoreboardFragment(activity: GroupActivity, groupModel: EntitiesProto.GroupModel) : Fragment() {
    private lateinit var binding: FragmentGroupScoreboardBinding
    private var mGroupModel = groupModel
    private var mActivity = activity
    private lateinit var dataArrayList: ArrayList<ListScoreboardData?>
    private lateinit var listViewAdapter: ListScoreboardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataArrayList = ArrayList()
        listViewAdapter = ListScoreboardAdapter(mActivity, dataArrayList)

        drawGroupScoreboardList()
    }

    private fun drawGroupScoreboardList() {
        val users: EntitiesProto.UserList = mGroupModel.users
        val stringBuilder: StringBuilder = StringBuilder()

        for (i in users.usersList) {
            stringBuilder.append(i.login)
            stringBuilder.append(" ")
        }

        Log.i("GroupScoreboardFragment", "Users in the group$stringBuilder")
        drawScoreboardList(users.usersList, mGroupModel.admin.login)
    }

    private fun checkTest(test: EntitiesProto.PassedTestModel, chapterIds: List<Int>) : Boolean {
        return (chapterIds.indexOf(test.chapterId.toInt()) != -1)
    }
    @SuppressLint("NewApi")
    private fun getAvgPercents(chapterIds: List<Int>, user: UserModel) : Long {
        val groupTests = user.passedTests
            .passedTestsList
            .stream()
            .filter({ test1 -> checkTest(test1, chapterIds) })
            .toList()
        var sumTests = 0L
        var counter = 0L
        for (testPassed in groupTests) {
            if (testPassed.questionsNumber.toInt() != 0) {
                counter++
                sumTests += testPassed.rightAnswers * 100 / testPassed.questionsNumber
            }
        }
        if (counter.toInt() == 0) {
            return 0
        }
        return sumTests / counter;
    }

    @SuppressLint("NewApi")
    private fun drawScoreboardList(users: List<EntitiesProto.UserModel>, adminName: String) {
        val chapterIds = mutableListOf<Int>()
        for (chapter in mGroupModel.chapters.chaptersList) {
            chapterIds.add(chapter.id.toInt())
        }
        Log.e("Chapters", chapterIds.toString())
        dataArrayList.clear()
        users.sortedBy { user -> -getAvgPercents(chapterIds, user) }
        var pos = 0
        for (user in users) {
            Log.e("User + tests", user.login + " " + user.passedTests.passedTestsList.size.toString())
            if (user.login != adminName) {
                dataArrayList.add(ListScoreboardData(user.login, user.passedTests.passedTestsList.stream().filter { test -> checkTest(test, chapterIds) }.toList().size, getAvgPercents(chapterIds, user), pos))
                pos++
            }
        }

        listViewAdapter = ListScoreboardAdapter(mActivity, dataArrayList)
        binding.groupScoreboardList.adapter = listViewAdapter
        listViewAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupScoreboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}