package ru.hse.client.groups

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import okhttp3.OkHttpClient
import ru.hse.client.R
import ru.hse.client.chapters.*
import ru.hse.client.databinding.FragmentGroupChaptersBinding
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.GroupModel
import ru.hse.server.proto.groupModel

class  GroupChaptersFragment(activity: GroupActivity, groupModel: GroupModel) : Fragment(R.layout.fragment_group_chapters) {
    private lateinit var binding: FragmentGroupChaptersBinding
    private var mGroupModel = groupModel
    private var mActivity = activity
    private lateinit var dataArrayList: ArrayList<ListChapterData?>
    private lateinit var listViewAdapter: ListChapterAdapter
    private var okHttpClient = OkHttpClient()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataArrayList = ArrayList()
        listViewAdapter = ListChapterAdapter(mActivity, dataArrayList)

        if (user.getId() != mGroupModel.admin.id) {
            binding.newChapterButton.visibility = View.INVISIBLE
        }
        binding.newChapterButton.setOnClickListener {
            newChapterButtonPressed()
        }

        drawGroupChapterList()
    }

    private fun newChapterButtonPressed() {
        val intent = Intent(activity, ChapterCreateActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("group info", mGroupModel.toByteArray())
        startActivityForResult(intent, 100)
    }

    private fun drawGroupChapterList() {
        val chapters: EntitiesProto.ChapterList = mGroupModel.chapters
        Log.e("GroupChaptersFragment", chapters.chaptersList?.size.toString())

        val stringBuilder: StringBuilder = StringBuilder()

        for (i in chapters.chaptersList) {
            stringBuilder.append(i.name)
            stringBuilder.append(" ")
        }

        Log.i("GroupChaptersFragment", "Chapters of group are $stringBuilder")
        drawChapterList(chapters.chaptersList)
    }

    private fun drawChapterList(chapters: List<EntitiesProto.ChapterModel>) {
        val data: MutableList<Map<String, String>> = mutableListOf()

        dataArrayList.clear()
        for ((numOfChapter, fChapter) in chapters.withIndex()) {
            val chapter = getChapter(fChapter, false, mActivity, okHttpClient)!!
            data.add(
                mapOf(
                    KEY_TITLE to chapter.name,
                    KEY_NUMBER to numOfChapter.toString(),
                )
            )
            var numberOfPassedTestForCurrentChapter = 0
            val passedTestList = user.getUserPassedTestList().passedTestsList
            val idsOfPassedTestList = ArrayList<Long>()
            for (test in passedTestList) {
                idsOfPassedTestList.add(test.id)
            }
            for (test in chapter.tests.testsList) {
                if (idsOfPassedTestList.contains(test.id)) {
                    numberOfPassedTestForCurrentChapter++
                }
            }
            dataArrayList.add(
                ListChapterData(
                    chapter.name.toString(),
                    chapter.tests.testsList.size,
                    numberOfPassedTestForCurrentChapter,
                    chapter.deadlineTs
                )
            )
        }

        listViewAdapter = ListChapterAdapter(mActivity, dataArrayList)
        binding.groupChaptersList.adapter = listViewAdapter
        binding.groupChaptersList.isClickable = true
        listViewAdapter.notifyDataSetChanged()

        binding.groupChaptersList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val chapter = chapters[position]
                val chapterName = data[position][KEY_TITLE]

                if (chapterName != chapter.name) {
                    Log.e(
                        "GroupActivity",
                        "can not open chapter, chapter name and position mismatch"
                    )
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                    return@OnItemClickListener
                }

                val chapterResponse = getChapter(chapter, false, mActivity, okHttpClient)

                if (chapterResponse == null) {
                    Log.e("GroupActivity", "can't get chapter from sever")
                } else {
                    val intent = Intent(mActivity, ReadingChapterActivity::class.java)
                    val bundle = Bundle()
                    Log.e("Tests", chapterResponse.tests.testsList.size.toString())
                    bundle.putSerializable("chapter", chapterResponse)
                    val text = getChapterText(chapterResponse, false, mActivity, okHttpClient)
                    bundle.putSerializable("text", text)
                    bundle.putSerializable("chapters", mGroupModel.chapters)
                    bundle.putSerializable("position", position)
                    intent.putExtras(bundle)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupChaptersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        val KEY_TITLE = "title"

        @JvmStatic
        val KEY_NUMBER = "admin"
    }

}