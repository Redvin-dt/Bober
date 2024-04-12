package ru.hse.client.auth

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.ByteString
import ru.hse.client.main.GroupSelectMenuActivity
import ru.hse.server.proto.EntitiesProto.UserModel
import ru.hse.server.proto.EntitiesProto
import java.io.IOException
import ru.hse.client.R
import ru.hse.client.main.*

fun printErrorAboutBadUser(activity: Activity, loginLayout: TextInputLayout, passwordLayout: TextInputLayout) {
    Log.e("Info", "no such user with this login")
    val badUserMessage: String = "incorrect login/password"
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(
            activity,
            badUserMessage,
            Toast.LENGTH_SHORT
        ).show()
        loginLayout.error = badUserMessage
        passwordLayout.error = badUserMessage
    }
}

fun printOkAboutGoodUser(activity: Activity) {
    Log.e("Info", "user exist")
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(
            activity,
            "Welcome",
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun printMessageFromBadResponse(message: CharSequence?, activity: Activity) {
    if (message == null) {
        Log.e("Error", "null response message")
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                activity,
                "Something went wrong, try again",
                Toast.LENGTH_SHORT
            ).show()
        }
        return
    }
    Log.e("Info", message.toString())
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}


fun tryToLogInUser(
    login: String,
    password: String,
    activity: Activity,
    okHttpClient: OkHttpClient,
    loginLayout: TextInputLayout,
    passwordLayout: TextInputLayout
) {
    val URlGetUser: String = ("http://" + ContextCompat.getString(activity, R.string.IP) + "/users/userByLogin").toHttpUrlOrNull()
        ?.newBuilder()
        ?.addQueryParameter("login", login)
        ?.build().toString()

    val requestForGetUser: Request = Request.Builder()
        .url(URlGetUser)
        .build()

    okHttpClient.newCall(requestForGetUser).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Error", e.toString() + " " + e.message)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    activity,
                    "Something went wrong, try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onResponse(call: Call, response: Response) {
            Log.i("Info", response.toString())
            if (response.isSuccessful) {
                val responseBody: ByteString? = response.body?.byteString()
                val user_: EntitiesProto.UserModel =
                    EntitiesProto.UserModel.parseFrom(responseBody?.toByteArray())
                if (user_.hasPasswordHash()) {
                    if (user_.passwordHash == password) {
                        printOkAboutGoodUser(activity)
                        user.setUser(user_)
                        val intent = Intent(activity, GroupSelectMenuActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(activity, intent, null)
                    } else {
                        Log.e("Info", "no such login/password")
                        printErrorAboutBadUser(activity, loginLayout, passwordLayout)
                    }
                } else {
                    Log.e("Info", "no such login/password")
                    printErrorAboutBadUser(activity, loginLayout, passwordLayout)
                }
            } else {
                printMessageFromBadResponse(response.body?.string(), activity)
            }
        }
    })
}


fun tryToRegisterUser(
    login: String,
    email: String,
    password: String,
    activity: Activity,
    okHttpClient: OkHttpClient,
    loginLayout: TextInputLayout,
    emailLayout: TextInputLayout
) {
    val URlRegistration: String =
        ("http://" + ContextCompat.getString(activity, R.string.IP) + "/users/registration").toHttpUrlOrNull()
            ?.newBuilder()
            ?.build()
            .toString()

    val user_: EntitiesProto.UserModel =
        EntitiesProto.UserModel.newBuilder().setLogin(login).setEmail(email).setPasswordHash(password).build()
    val requestBody: RequestBody =
        RequestBody.create("application/x-protobuf".toMediaTypeOrNull(), user_.toByteArray())

    val requestForRegisterUser: Request = Request.Builder()
        .url(URlRegistration)
        .post(requestBody)
        .build()

    Log.i("Info", "Request has been sent $URlRegistration")
    val somethingWentWrongMessage: String = "Something went wrong, try again"
    okHttpClient.newCall(requestForRegisterUser).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Error", e.toString() + " " + e.message)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(activity, somethingWentWrongMessage, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResponse(call: Call, response: Response) {
            Log.i("Info", response.toString())
            if (response.isSuccessful) {
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
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                activity,
                                "$somethingWentWrongMessage with another login",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.i("Info", response.toString())
                        if (response.isSuccessful) {
                            val responseBody: ByteString? = response.body?.byteString()
                            val registeredUser: EntitiesProto.UserModel = EntitiesProto.UserModel.parseFrom(responseBody?.toByteArray())
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    activity,
                                    "User has been successfully created",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            user.setUser(registeredUser)
                            val intent = Intent(activity, GroupSelectMenuActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(activity, intent, null)
                        }
                    }
                })

            } else {
                val loginErrorMessage: String = "user with that login already exist"
                val emailErrorMessage: String = "user with that email already exist"
                val responseMessage = response.body?.string()
                if (responseMessage == loginErrorMessage) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            activity,
                            loginErrorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        loginLayout.error = loginErrorMessage
                    }
                } else if (responseMessage == emailErrorMessage) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            activity,
                            emailErrorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        emailLayout.error = emailErrorMessage
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            activity,
                            somethingWentWrongMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    })
}


