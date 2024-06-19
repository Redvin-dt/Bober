package ru.hse.client.groups

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import okhttp3.OkHttpClient
import ru.hse.client.R
import ru.hse.client.databinding.ActivityGroupEnterBinding
import ru.hse.client.entry.generateHash
import ru.hse.client.entry.hideKeyboard
import ru.hse.client.entry.isNotValidPassword
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

        val bundle = intent.extras
        val groupId : Long?
        val groupName : String?

        if (bundle != null) {
            groupId = bundle.getLong("groupId")
            groupName = bundle.getString("groupName")
        } else {
            Log.e("Error", "group enter activity has not bundle")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                        this@GroupEnterActivity,
                        "Something wrong try again",
                        Toast.LENGTH_SHORT
                ).show()
            }
            finish()
            return
        }

        if (groupName == null) {
            Log.e("Error", "group enter activity has not id and name")
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                        this@GroupEnterActivity,
                        "Something wrong try again",
                        Toast.LENGTH_SHORT
                ).show()
            }
            finish()
            return
        }

        val passwordEditText = binding.password
        val passwordLayout = binding.passwordBox

        passwordEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val textWatcherForPassword: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val passwordOriginal = charSequence.toString()
                if (isNotValidPassword(passwordOriginal)) {
                    passwordLayout.error = "Incorrect password"
                } else {
                    passwordLayout.boxStrokeColor = ContextCompat.getColor(this@GroupEnterActivity,
                            R.color.green
                    )
                    passwordLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        passwordEditText.addTextChangedListener(textWatcherForPassword)

        binding.enterGroupButton.setOnClickListener {
            onClickButtonEnterGroup(groupId, groupName, passwordEditText)
        }
    }

    private fun onClickButtonEnterGroup(groupId : Long, groupName : String, passwordEditText : TextInputEditText) {
        val password = passwordEditText.text.toString()

        if (isNotValidPassword(password)) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                        this@GroupEnterActivity,
                        "Incorrect password",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }

        val groupModel = EntitiesProto.GroupModel.newBuilder().setId(groupId).setName(groupName).setPasswordHash(
            generateHash(password, this)).build()

        val responseGroupModel = enterGroup(groupModel, true, this@GroupEnterActivity, okHttpClient)

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