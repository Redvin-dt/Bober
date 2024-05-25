package ru.hse.client.groups

import android.content.Intent
import android.graphics.Color
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
import java.util.regex.Pattern


class GroupSelectMenuActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityGroupSelectMenuBinding
    private lateinit var dataArrayList: ArrayList<ListData?>
    private lateinit var listViewAdapter: ListAdapter

    private var okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupSelectMenuBinding.inflate(layoutInflater)

        dataArrayList = ArrayList()
        listViewAdapter = ListAdapter(this, dataArrayList)

        setContentView(binding.root)
        allocateActivityTitle("Groups")

        binding.groupSearchList.adapter = listViewAdapter

        binding.createGroupButton.setOnClickListener {
            onNewGroupPressed()
        }

        binding.groupSearchView.setOnSearchClickListener {
            drawGroupsByPrefix("")
        }

        val textView: AutoCompleteTextView = binding.groupSearchView.findViewById(binding.groupSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null))
        textView.setTextColor(Color.BLACK)

        binding.groupSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null && query != "") {
                    if (!filterSearchText(query)) {
                        Toast.makeText(this@GroupSelectMenuActivity, "Incorrect group name", Toast.LENGTH_LONG).show()
                        drawUserGroupList()
                        return false
                    }
                    drawGroupsByPrefix(query)
                } else {
                    drawUserGroupList()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null) {
                    textView.setTextColor(Color.BLACK)
                    return false
                }

                if (filterSearchText(newText)) {
                    textView.setTextColor(Color.BLACK)
                } else {
                    textView.setTextColor(Color.RED)
                }
                return false
            }
        })

        binding.groupSearchView.setOnCloseListener {
            drawUserGroupList()
            false;
        }

        drawUserGroupList()
    }

    private fun drawGroupsByPrefix(prefixName: String) {
        val groups = getGroupsByPrefix(prefixName, this@GroupSelectMenuActivity, okHttpClient)
        if (groups == null) {
            Log.e("Error", "can not find group with this prefix")
            drawUserGroupList();
            return;
        }

        drawGroupsList(groups)
    }

    private fun drawUserGroupList() {
        val userGroups = user.getUserGroups()

        val stringBuilder : StringBuilder = StringBuilder()

        for (i in userGroups) {
            stringBuilder.append(i.name)
            stringBuilder.append(" ")
        }

        Log.i("Info", "userGroupList is $stringBuilder")
        drawGroupsList(userGroups)
    }

    private fun drawGroupsList(groups: List<EntitiesProto.GroupModel>) {
        val data: MutableList<Map<String, String>> = mutableListOf()

        dataArrayList.clear()

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

        listViewAdapter.notifyDataSetChanged()

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

            val groupResponse = enterGroup(group, this@GroupSelectMenuActivity, okHttpClient)

            if (groupResponse == null) {
                val intent = Intent(this, GroupEnterActivity::class.java)
                startActivity(intent)
                finish()
            }

            val intent = Intent(this, GroupActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("group", groupResponse)
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

    private fun filterSearchText(text: String): Boolean {
        val patternAsString: String = "^(?=.*[a-z])([0-9]*)([A-Z]*)(?=\\S+$).*$"
        val pattern: Pattern = Pattern.compile(patternAsString)
        return pattern.matcher(text).matches()
    }

    companion object {
        @JvmStatic
        val KEY_TITLE = "title"

        @JvmStatic
        val KEY_ADMIN = "admin"
    }
}