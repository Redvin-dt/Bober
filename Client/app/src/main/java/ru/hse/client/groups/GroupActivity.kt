package ru.hse.client.groups

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import okhttp3.OkHttpClient
import ru.hse.client.databinding.ActivityGroupBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto.GroupModel
import ru.hse.client.chapters.ChapterUploadActivity
import ru.hse.client.chapters.ReadingChapterActivity
import ru.hse.client.chapters.getChapter
import ru.hse.client.chapters.getChapterText
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto

class GroupActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityGroupBinding
    private lateinit var pageAdapter: GroupPageAdapter
    private lateinit var group: GroupModel
    private var okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        user.setUserByLogin(this, user.getUserLogin())
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.extras

        if (bundle != null) {
            group = bundle.getSerializable("group") as GroupModel
        }

        Log.i("GroupActivity", group.name)
        group = enterGroup(group, false, this, okHttpClient)!!

        allocateActivityTitle(group.name.toString())
        if (user.getId() != group.admin.id) {
            binding.newChapterButton.visibility = View.INVISIBLE
        }
        binding.newChapterButton.setOnClickListener {
            newChapterButtonPressed()
        }

        pageAdapter = GroupPageAdapter(supportFragmentManager, lifecycle, this, group)
        binding.viewPager.adapter = pageAdapter
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }

    private fun newChapterButtonPressed() {
        val intent = Intent(this@GroupActivity, ChapterUploadActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("group info", group.toByteArray())
        startActivityForResult(intent, 100)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            group = enterGroup(group, false, this, okHttpClient)!!
            Log.e("Chapters", group.chapters.chaptersList.size.toString())
            pageAdapter = GroupPageAdapter(supportFragmentManager, lifecycle, this, group)
            binding.viewPager.adapter = pageAdapter
            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    binding.viewPager.currentItem = tab!!.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}

            })

            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
                }
            })
        }
    }
}