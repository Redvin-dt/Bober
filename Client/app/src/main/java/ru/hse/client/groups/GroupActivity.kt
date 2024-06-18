package ru.hse.client.groups

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
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
import ru.hse.server.proto.EntitiesProto

class GroupActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityGroupBinding
    private lateinit var pageAdapter: GroupPageAdapter
    private lateinit var group: GroupModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.extras

        if (bundle != null) {
            group = bundle.getSerializable("group") as GroupModel
        }

        Log.i("GroupActivity", group.name)


        allocateActivityTitle(group.name.toString())

        binding.groupTitle.text = group.name.toString()
        binding.groupTitle.ellipsize = TextUtils.TruncateAt.END
        binding.groupTitle.maxLines = 1

        binding.groupAdmin.text = group.admin.login.toString()
        binding.groupAdmin.ellipsize = TextUtils.TruncateAt.END
        binding.groupAdmin.maxLines = 1

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
        startActivity(intent)
    }

}