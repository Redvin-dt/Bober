package ru.hse.client.groups

import android.os.Bundle
import android.util.Log
import ru.hse.client.databinding.ActivityGroupBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto

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

    }

}