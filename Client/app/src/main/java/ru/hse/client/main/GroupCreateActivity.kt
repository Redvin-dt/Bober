package ru.hse.client.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import ru.hse.client.R
import ru.hse.client.databinding.ActivityGroupCreateBinding
import ru.hse.client.entry.*
import ru.hse.server.proto.EntitiesProto

import java.io.IOException

class GroupCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupCreateBinding
    private val context = this@GroupCreateActivity
    private val okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nameEditText: TextInputEditText = binding.name
        val nameLayout: TextInputLayout = binding.nameBox
        nameEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val textWatcherForName: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                charSequence: CharSequence, start: Int, before: Int, count: Int
            ) {
                val newText = charSequence.toString()
                if (isNotValidLogin(newText)) {
                    nameLayout.error = "Unacceptable symbols in login"
                } else {
                    nameLayout.boxStrokeColor =
                        ContextCompat.getColor(this@GroupCreateActivity, R.color.green)
                    nameLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        nameEditText.addTextChangedListener(textWatcherForName)



        val passwordEditText: TextInputEditText = binding.password
        val passwordLayout: TextInputLayout = binding.passwordBox
        passwordEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val textWatcherForPassword: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                charSequence: CharSequence, start: Int, before: Int, count: Int
            ) {
                val newText = charSequence.toString()
                if (isNotValidPassword(newText)) {
                    passwordLayout.error = "Incorrect password"
                } else {
                    passwordLayout.boxStrokeColor = ContextCompat.getColor(this@GroupCreateActivity,
                        R.color.green
                    )
                    passwordLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        passwordEditText.addTextChangedListener(textWatcherForPassword)


        binding.createGroupButton.setOnClickListener {
            onCreateButtonPressed()
        }
    }


    private fun onCreateButtonPressed() {
        val name: String = binding.name.text.toString()
        val password: String = binding.password.text.toString()

        if (isNotValidLogin(name)) {
            writeErrorAboutLogin(name, context) // TODO: set name not login
            Log.e("Create group", "incorrect name: $name")
            return
        }

        if (isNotValidPassword(password)) {
            writeErrorAboutPassword(password, context)
            Log.e("Create group", "incorrect password: $password")
            return
        }

        sendCreateGroupToServer(name, password)

        val intent = Intent(context, GroupSelectMenuActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun sendCreateGroupToServer(groupName: String, groupPassword: String){
        val URlCreateGroup: String =
                ("http://" + ContextCompat.getString(context, R.string.IP) + "/groups").toHttpUrlOrNull()
                        ?.newBuilder()
                        ?.build()
                        .toString()

        if (user.getUserModel() == null){
            Log.e("Group create","Cannot create group user model not found") // TODO: do some additional logic
            return
        }

        val userList: EntitiesProto.UserList = EntitiesProto.UserList.newBuilder().addUsers(user.getUserModel()).build()

        val group: EntitiesProto.GroupModel =
                EntitiesProto.GroupModel.newBuilder().setName(groupName).setPasswordHash(groupPassword).setAdmin(user.getUserModel()).setUsers(userList).build()

        val requestBody: RequestBody =
                RequestBody.create("application/x-protobuf".toMediaTypeOrNull(), group.toByteArray()) // TODO: rewrite depreceated method create

        val request: Request = Request.Builder()
                .url(URlCreateGroup)
                .post(requestBody)
                .header("Authorization", "Bearer " + user.getUserToken())
                .build()

        Log.i("Info", "Request has been sent $URlCreateGroup")

        val somethingWentWrongMessage: String = "Something went wrong, try again"
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", e.toString() + " " + e.message)
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, somethingWentWrongMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Info", response.toString())
                if (response.isSuccessful) {
                    user.updateUser(context)
                    Log.i("Info", "Group created")
                    return
                } else {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, response.body?.string(), Toast.LENGTH_SHORT).show() // TODO: set error message to layout?
                    }
                }
            }
        })

    }
}