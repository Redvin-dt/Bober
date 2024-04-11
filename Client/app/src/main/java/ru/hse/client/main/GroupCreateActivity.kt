package ru.hse.client.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.ByteString
import ru.hse.client.R
import ru.hse.client.databinding.GroupCreateBinding
import ru.hse.client.databinding.GroupSelectMenuBinding
import ru.hse.client.auth.isNotValidLogin
import ru.hse.client.auth.isNotValidPassword
import ru.hse.client.auth.writeErrorAboutLogin
import ru.hse.client.auth.writeErrorAboutPassword
import ru.hse.client.main.GroupSelectMenuActivity
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.userModel

import ru.hse.client.main.user
import java.io.IOException

class GroupCreateActivity : AppCompatActivity() {

    private lateinit var binding: GroupCreateBinding
    private val context = this@GroupCreateActivity
    private val okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        startActivity(intent)
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
                    // TODO: update user
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