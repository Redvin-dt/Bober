package ru.hse.client.main

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okio.ByteString
import ru.hse.client.R
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.GroupList
import ru.hse.server.proto.EntitiesProto.GroupModel
import ru.hse.server.proto.EntitiesProto.UserModel
import ru.hse.server.proto.userModel
import java.io.IOException
import java.util.Optional

class User {
    private var user: UserModel? = null
    private var groups: List<GroupModel> = listOf()
    private val okHttpClient = OkHttpClient()

    private fun userExist(): Boolean {
        return user != null
    }

    fun setUser(newUser: UserModel) {
        user = newUser

        if (newUser.hasUserOfGroups()) {
            val groupListProto = newUser.userOfGroups
            groups = groupListProto.groupsList
        }
    }

    fun getUserModel(): UserModel? {
        return user
    }

    fun isUserHasLogin(): Boolean {
        return userExist() && user!!.hasLogin()
    }

    fun getUserLogin(): String {
        return user!!.getLogin()
    }

    fun isUserHasEmail(): Boolean {
        return userExist() && user!!.hasEmail()
    }

    fun getUserEmail(): String {
        return user!!.getEmail()
    }

    fun getUserGroups(): List<GroupModel> {
        return groups
    }

    private fun setUserByLogin(activity: Activity, login: String) {
        val URlGetUser: String =
            ("http://" + ContextCompat.getString(activity, R.string.IP) + "/users/userByLogin").toHttpUrlOrNull()
                ?.newBuilder()
                ?.addQueryParameter("login", login)
                ?.build().toString()

        val requestForGetGeneratedUser: Request = Request.Builder()
            .url(URlGetUser)
            .build()

        okHttpClient.newCall(requestForGetGeneratedUser).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", e.toString() + " " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Info", response.toString())
                if (response.isSuccessful) {
                    val responseBody: ByteString? = response.body?.byteString()
                    val registeredUser: EntitiesProto.UserModel = EntitiesProto.UserModel.parseFrom(responseBody?.toByteArray())
                    setUser(registeredUser)
                } else {
                    response.body?.let { Log.i("Error", it.string()) }
                }
            }
        })
    }

    fun updateUser(activity: Activity) {
        if (user == null) {
            Log.e("User update", "cannot update user since he have not set")
            return
        }

        setUserByLogin(activity, getUserLogin())
    }
}

var user: User = User()
