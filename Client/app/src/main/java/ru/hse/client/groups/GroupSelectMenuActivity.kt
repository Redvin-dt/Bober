package ru.hse.client.groups

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import okhttp3.OkHttpClient
import ru.hse.client.R
import ru.hse.client.databinding.ActivityGroupSelectMenuBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto

class GroupSelectMenuActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityGroupSelectMenuBinding
    private var dataArrayList = ArrayList<ListData?>()

    private var okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupSelectMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Groups")

        binding.createGroupButton.setOnClickListener {
            onNewGroupPressed()
        }

        binding.groupSearchView.setOnSearchClickListener {
            drawGroupsByPrefix("")
        }

        binding.groupSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are checking
                // if query exist or not.
                //if (programmingLanguagesList.contains(query)) {
                //    // if query exist within list we
                //    // are filtering our list adapter.
                //    listAdapter.filter.filter(query)
                //} else {
                //    // if query is not present we are displaying
                //    // a toast message as no  data found..
                //    Toast.makeText(this@MainActivity, "No Language found..", Toast.LENGTH_LONG)
                //            .show()
                //} // TODO: add filters
                if (query != null) {
                    drawGroupsByPrefix(query)
                } else {
                    drawUserGroupList() // TODO: mb set smth else
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // if query text is change in that case we
                // are filtering our adapter with
                // new text on below line.
                return false // TODO: add filters
            }
        }) // TODO: remove comments


        /*val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            user.updateUser(this)
            createGroupList()
            swipeRefreshLayout.isRefreshing = false
        }*/

        drawUserGroupList()
    }

    private fun drawGroupsByPrefix(prefixName: String) {
        val groups = getGroupsByPrefix(prefixName, this@GroupSelectMenuActivity, okHttpClient)
        if (groups == null) {
            Log.e("Error", "can not find group with this prefix") // TODO: add smth else, rename
            drawUserGroupList(); // TODO: mb remove
            return;
        }

        drawGroupsList(groups)
    }

    private fun drawUserGroupList() {
        val userGroups = user.getUserGroups()
        drawGroupsList(userGroups)

    }

    private fun drawGroupsList(groups : List<EntitiesProto.GroupModel>) {
        val data: MutableList<Map<String, String>> = mutableListOf()

        for (group in groups) {
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
            val group = groups[position]
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