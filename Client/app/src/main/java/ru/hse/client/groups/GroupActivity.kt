package ru.hse.client.groups

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import okhttp3.OkHttpClient
import ru.hse.client.databinding.ActivityGroupBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto
import ru.hse.client.chapters.ChapterUploadActivity
import ru.hse.client.chapters.ReadingChapterActivity
import ru.hse.client.chapters.getChapter

class GroupActivity: DrawerBaseActivity() {

    private lateinit var binding: ActivityGroupBinding
    private var group: EntitiesProto.GroupModel? = null
    private lateinit var dataArrayList: ArrayList<ListChapterData?>
    private lateinit var listViewAdapter: ListChapterAdapter
    private var okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.extras

        dataArrayList = ArrayList()

        if (bundle != null) {
            group = bundle.getSerializable("group") as EntitiesProto.GroupModel
        }

        listViewAdapter = ListChapterAdapter(this, dataArrayList)

        if (group != null) {
            Log.i("GroupActivity", group!!.name)
        } else {
            Log.e("GroupActivity", "Null group in GroupActivity")
            finish()
            return
        }

        allocateActivityTitle(group!!.name.toString())

        binding.groupTitle.text = group!!.name.toString()
        binding.groupAdmin.text = group!!.admin.login.toString()


        binding.newChapterButton.setOnClickListener {
            newChapterButtonPressed()
        }

        drawGroupChapterList()
    }

    private fun drawGroupChapterList() {
        val chapters: EntitiesProto.ChapterList? = group?.chapters
        Log.e("GroupActivity", chapters?.chaptersList?.size.toString())
        if (chapters == null) {
            return
        }

        val stringBuilder : StringBuilder = StringBuilder()

        for (i in chapters.chaptersList) {
            stringBuilder.append(i.name)
            stringBuilder.append(" ")
        }

        Log.i("GroupActivity", "Chapters of group are $stringBuilder")
        drawChapterList(chapters.chaptersList)
    }

    private fun drawChapterList(chapters: List<EntitiesProto.ChapterModel>) {
        val data: MutableList<Map<String, String>> = mutableListOf()

        dataArrayList.clear()
        for ((numOfChapter, chapter) in chapters.withIndex()) {
            data.add(
                mapOf(
                    KEY_TITLE to chapter.name,
                    KEY_NUMBER to numOfChapter.toString(),
                )
            )
            dataArrayList.add(
                ListChapterData(
                    numOfChapter,
                    group?.name.toString(),
                    chapter.tests.testsList.size,
                )
            )
        }

        listViewAdapter.notifyDataSetChanged()

        listViewAdapter = ListChapterAdapter(this, dataArrayList)
        binding.groupSearchList.adapter = listViewAdapter

        binding.groupSearchList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val chapter = chapters[position]
            val chapterName = data[position][KEY_TITLE]

            if (chapterName != chapter.name) {
                Log.e("GroupActivity", "can not open chapter, chapter name and position mismatch")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
                return@OnItemClickListener
            }

            val chapterResponse = getChapter(chapter, false, this@GroupActivity, okHttpClient)

            if (chapterResponse == null) {
               Log.e("GroupActivity", "can't get chapter from sever")
            } else {
                val intent = Intent(this, ReadingChapterActivity::class.java)
                val bundle = Bundle()
                Log.e("Tests", chapterResponse.tests.testsList.size.toString())
                bundle.putSerializable("chapter", chapterResponse)
                bundle.putSerializable("text", "AAAAAAAAAAAABBBBBBBB")
                intent.putExtras(bundle)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun newChapterButtonPressed() {
        val intent = Intent(this@GroupActivity, ChapterUploadActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    companion object {
        @JvmStatic
        val KEY_TITLE = "title"

        @JvmStatic
        val KEY_NUMBER = "admin"
    }

}