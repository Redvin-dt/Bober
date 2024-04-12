package ru.hse.client.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AdapterView
import android.widget.SimpleAdapter

import android.widget.Toast
import ru.hse.client.databinding.GroupSelectMenuBinding

class GroupSelectMenuActivity : DrawerBaseActivity() {

    private lateinit var binding: GroupSelectMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupSelectMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Groups")

        binding.createGroupButton.setOnClickListener {
            onNewGroupPressed()
        }

        createGroupList()
    }

    private fun createGroupList(){
        val data : MutableList<Map<String, String>> = mutableListOf()
        val userGroups = user.getUserGroups()

        for (group in userGroups) {
            data.add(mapOf(
                    KEY_TITLE to group.name,
                    KEY_ADMIN to group.admin.login,
                    )
            )
        }

        val adapter = SimpleAdapter(
                this,
                data,
                android.R.layout.simple_list_item_2,
                arrayOf(KEY_TITLE, KEY_ADMIN),
                intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        binding.groupSearchList.adapter = adapter

        binding.groupSearchList.onItemClickListener = AdapterView.OnItemClickListener {parent, view, position, id ->
            val group = userGroups[position];
            val groupName = data[position][KEY_TITLE];

            if (groupName != group.name) {
                Log.e("GroupSelectMenu", "can not open group, group name and position mismatch")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Ops, something went wrong", Toast.LENGTH_SHORT).show()
                }
                return@OnItemClickListener
            }

            val intent = Intent(this, GroupSelectMenuBinding::class.java) // TODO: set new class
            val bundle = Bundle()
            bundle.putSerializable("group", group)
            intent.putExtras(bundle)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()

            // Possible code in activity
            //val bundle = getIntent().extras
            //var group EntitiesProto.GroupModel // or other values


            //if (bundle != null)
            // group = bundle.getSerializable("group")
            // TODO: remove
        }
    }


    private fun onNewGroupPressed() {
        Log.i("button pressed", "create group button pressed")
        val intent = Intent(this, GroupCreateActivity::class.java)
        startActivity(intent)
    }

    companion object{
        @JvmStatic val KEY_TITLE = "title"
        @JvmStatic val KEY_ADMIN = "admin"
    }
}