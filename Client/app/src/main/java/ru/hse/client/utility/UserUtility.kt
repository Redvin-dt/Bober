package ru.hse.client.utility

import android.app.Activity
import android.content.Context
import android.util.Log
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.ByteString
import ru.hse.client.R
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.GroupModel
import ru.hse.server.proto.EntitiesProto.UserModel
import java.io.IOException
import java.util.concurrent.CountDownLatch

class User {
    private var user: UserModel? = null
    private lateinit var clientHashedPassword: String
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
        return user!!.login
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

    fun getUserToken(): String {
        return user!!.accessToken
    }

    fun getHashedPassword(): String {
        return user!!.passwordHash
    }

    fun setUserClientPassword(password: String) {
        clientHashedPassword = password
    }

    fun getUserClientPassword(): String? {
        return clientHashedPassword
    }

    fun setUserByLogin(context: Context, login: String) {
        val URlGetUser: String =
            ("http://" + context.resources.getString(R.string.IP) + "/users/userByLogin").toHttpUrlOrNull()
                ?.newBuilder()
                ?.addQueryParameter("login", login)
                ?.build().toString()

        val requestForGetGeneratedUser: Request = Request.Builder()
            .url(URlGetUser)
            .header("Authorization", "Bearer " + getUserToken())
            .build()

        okHttpClient.newCall(requestForGetGeneratedUser).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", e.toString() + " " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Info", response.toString())
                if (response.isSuccessful) {
                    val responseBody: ByteString? = response.body?.byteString()
                    val registeredUser: EntitiesProto.UserModel =
                        EntitiesProto.UserModel.parseFrom(responseBody?.toByteArray())
                    setUser(registeredUser)
                } else {
                    response.body?.let { Log.i("Error", it.string()) }
                }
            }
        })
    }

    fun setUserByEmail(context: Context, email: String, token: String) {
        val URlGetUser: String =
            ("http://" + context.resources.getString(R.string.IP) + "/users/userByEmail").toHttpUrlOrNull()
                ?.newBuilder()
                ?.addQueryParameter("email", email)
                ?.build().toString()

        val requestForGetGeneratedUser: Request = Request.Builder()
            .url(URlGetUser)
            .header("Authorization", "Bearer ${token}")
            .build()

        okHttpClient.newCall(requestForGetGeneratedUser).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", e.toString() + " " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Info", response.toString())
                if (response.isSuccessful) {
                    val responseBody: ByteString? = response.body?.byteString()
                    val registeredUser: EntitiesProto.UserModel =
                        EntitiesProto.UserModel.parseFrom(responseBody?.toByteArray())
                    setUser(registeredUser)
                } else {
                    response.body?.let { Log.i("Error", it.string()) }
                }
            }
        })
    }

    fun check_token_is_valid(context: Context, login: String, token: String): Boolean {
        val URlGetUser: String =
            ("http://" + context.resources.getString(R.string.IP) + "/users/userByLogin").toHttpUrlOrNull()
                ?.newBuilder()
                ?.addQueryParameter("login", login)
                ?.build().toString()

        val requestForGetGeneratedUser: Request = Request.Builder()
            .url(URlGetUser)
            .header("Authorization", "Bearer $token")
            .build()

        var answer: Boolean = false
        okHttpClient.newCall(requestForGetGeneratedUser).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", e.toString() + " " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Info", response.toString())
                if (response.isSuccessful) {
                    answer = true
                }
            }
        })
        return answer
    }


    fun updateUserFromServer(context: Context, login: String, password: String, email: String) {
        val user_: EntitiesProto.UserModel =
            EntitiesProto.UserModel.newBuilder().setLogin(login).setEmail(email).setPasswordHash(password).build()

        val requestBody: RequestBody =
            RequestBody.create("application/x-protobuf".toMediaTypeOrNull(), user_.toByteArray())

        val URlRegistration: String =
            ("http://" + context.resources.getString(R.string.IP) + "/users/login").toHttpUrlOrNull()
                ?.newBuilder()
                ?.build()
                .toString()

        val requestForLogIn: Request = Request.Builder()
            .url(URlRegistration)
            .post(requestBody)
            .build()

        val countDownLatch = CountDownLatch(1)
        okHttpClient.newCall(requestForLogIn).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", e.toString() + " " + e.message)
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Info", response.toString())
                if (response.isSuccessful) {
                    val responseBody: ByteString? = response.body?.byteString()
                    val registeredUser: EntitiesProto.UserModel =
                        EntitiesProto.UserModel.parseFrom(responseBody?.toByteArray())
                    setUser(registeredUser)
                } else {
                    response.body?.let { Log.i("Error", it.string()) }
                }
                countDownLatch.countDown()
            }
        })

        countDownLatch.await();
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
