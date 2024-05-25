package ru.hse.client.groups

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import okhttp3.OkHttpClient
import ru.hse.client.databinding.ActivityGroupEnterBinding
import ru.hse.client.entry.hideKeyboard
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto

class GroupEnterActivity : DrawerBaseActivity() {

    private lateinit var binding : ActivityGroupEnterBinding
    private val okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupEnterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        allocateActivityTitle("GroupsEnter")

        val nameEditText = binding.name // TODO: remove name
        val passwordEditText = binding.password

        nameEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        // TODO: add text watchers



        binding.enterGroupButton.setOnClickListener {
            onClickButtonEnterGroup(nameEditText, passwordEditText)
        }
    }

    private fun onClickButtonEnterGroup(nameEditText : TextInputEditText, passwordEditText : TextInputEditText) {
        val name = nameEditText.text.toString()
        val password = passwordEditText.text.toString()

        val groupModel = EntitiesProto.GroupModel.newBuilder().setName(name).setPasswordHash(password).build()

        val responseGroupModel = enterGroup(groupModel, this@GroupEnterActivity, okHttpClient)

        if (responseGroupModel == null) {
            Log.e("Error", "failed to entry group")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                        this@GroupEnterActivity,
                        "Something wrong try again",
                        Toast.LENGTH_SHORT
                ).show()
            }
            return
        }

        val intent = Intent(this, GroupActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("group", responseGroupModel)
        intent.putExtras(bundle)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}