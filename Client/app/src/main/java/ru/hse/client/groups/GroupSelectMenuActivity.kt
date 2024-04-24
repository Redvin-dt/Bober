package ru.hse.client.groups

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import ru.hse.client.R
import ru.hse.client.databinding.ActivityGroupSelectMenuBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.client.utility.user

class GroupSelectMenuActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityGroupSelectMenuBinding
    private var dataArrayList = ArrayList<ListData?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupSelectMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Groups")

        binding.createGroupButton.setOnClickListener {
            onNewGroupPressed()
        }


        /*val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            user.updateUser(this)
            createGroupList()
            swipeRefreshLayout.isRefreshing = false
        }*/

        createGroupList()
    }


    private fun createGroupList() {
        val data: MutableList<Map<String, String>> = mutableListOf()
        val userGroups = user.getUserGroups()

        for (group in userGroups) {
            data.add(
                mapOf(
                    KEY_TITLE to group.name,
                    KEY_ADMIN to group.admin.login,
                )
            )
            dataArrayList.add(
                ListData(
                    group.name.toString(),
                    group.admin.login.toString(),
                    R.drawable.base_group_item_img
                )
            )
        }

        binding.groupSearchList.adapter = ListAdapter(this, dataArrayList)

        binding.groupSearchList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val group = userGroups[position]
            val groupName = data[position][KEY_TITLE]

            if (groupName != group.name) {
                Log.e("GroupSelectMenu", "can not open group, group name and position mismatch")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
                return@OnItemClickListener
            }

            val intent = Intent(this, GroupActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("group", group)
            intent.putExtras(bundle)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

    }


    private fun onNewGroupPressed() {
        Log.i("button pressed", "create group button pressed")
        val intent = Intent(this, GroupCreateActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    companion object {
        @JvmStatic
        val KEY_TITLE = "title"

        @JvmStatic
        val KEY_ADMIN = "admin"
    }
}