package ru.hse.client.groups

import android.content.Intent
import android.os.Bundle
import android.util.Log
import ru.hse.client.databinding.ActivityGroupBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto
import ru.hse.client.chapters.ChapterUploadActivity

class GroupActivity: DrawerBaseActivity() {

    private lateinit var binding: ActivityGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.extras
        var group: EntitiesProto.GroupModel? = null

        if (bundle != null) {
            group = bundle.getSerializable("group") as EntitiesProto.GroupModel
        }



        if (group != null) {
            Log.i("INFO", group.name)
        } else {
            Log.e("ERROR", "ERROR")
            // TODO: go to groups select and throw error
        }

        allocateActivityTitle(group!!.name.toString())

        binding.groupTitle.text = group.name.toString()
        binding.groupAdmin.text = group.admin.login.toString()


        binding.newChapterButton.setOnClickListener {
            newChapterButtonPressed()
        }

    }

    private fun newChapterButtonPressed() {
        val intent = Intent(this@GroupActivity, ChapterUploadActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

}