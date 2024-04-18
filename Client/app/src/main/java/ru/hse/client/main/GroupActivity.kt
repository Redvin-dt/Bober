package ru.hse.client.main

import android.os.Bundle
import android.util.Log
import ru.hse.client.databinding.ActivityGroupBinding
import ru.hse.client.databinding.ActivityGroupSelectMenuBinding
import ru.hse.server.proto.EntitiesProto

class GroupActivity: DrawerBaseActivity() {

    lateinit var binding: ActivityGroupBinding

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
        }

        allocateActivityTitle(group!!.name.toString())

        binding.groupLabel.text = "Group label: " + group!!.name.toString()
        binding.groupAdmin.text = "Group admin: " + " test"
    }

}