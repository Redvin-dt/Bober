package ru.hse.client.main

import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.hse.client.databinding.ActivityGroupSelectMenuBinding


class GroupSelectMenuActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityGroupSelectMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupSelectMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Groups")

        binding.createGroupButton.setOnClickListener {
            onNewGroupPressed()
        }


        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            user.updateUser(this)
            createGroupList()
            swipeRefreshLayout.isRefreshing = false
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

        val adapter = object: SimpleAdapter(
            this,
            data,
            R.layout.simple_list_item_2,
            arrayOf(KEY_TITLE, KEY_ADMIN),
            intArrayOf(R.id.text1, R.id.text2)
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val text1 = view.findViewById<TextView>(android.R.id.text1)
                text1.setTextColor(Color.BLACK)
                return view
            }
        }
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