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
import ru.hse.client.databinding.ActivityInviteBinding
import ru.hse.client.databinding.ActivityInvitesBinding
import ru.hse.client.entry.generateHash
import ru.hse.client.entry.hideKeyboard
import ru.hse.client.entry.isNotValidLogin
import ru.hse.client.entry.isNotValidPassword
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto

class InviteActivity : DrawerBaseActivity(){
    private lateinit var binding : ActivityInviteBinding
    private val okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInviteBinding.inflate(layoutInflater)

        setContentView(binding.root)
        allocateActivityTitle("InviteUser")

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
                        this@InviteActivity,
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
                        this@InviteActivity,
                        "Something wrong try again",
                        Toast.LENGTH_SHORT
                ).show()
            }
            finish()
            return
        }

        val loginEditText = binding.login
        val loginLayout = binding.loginBox

        loginEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val textWatcherForLogin: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val passwordOriginal = charSequence.toString()
                if (isNotValidLogin(passwordOriginal)) {
                    loginLayout.error = "Incorrect login"
                } else {
                    loginLayout.boxStrokeColor = ContextCompat.getColor(this@InviteActivity,
                            R.color.green
                    )
                    loginLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        loginEditText.addTextChangedListener(textWatcherForLogin)

        binding.enterInviteButton.setOnClickListener {
            onClickButtonEnterGroup(groupId, loginEditText)
        }
    }

    private fun onClickButtonEnterGroup(groupId : Long, loginEditText : TextInputEditText) {
        val userLogin = loginEditText.text.toString()

        if (isNotValidLogin(userLogin)) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                        this@InviteActivity,
                        "Incorrect password",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }


        if (sendInvite(groupId, userLogin, this@InviteActivity, okHttpClient)) {
            finish()
        }
    }
}
