package ru.hse.client.entry

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.ByteString
import ru.hse.client.groups.GroupSelectMenuActivity
import ru.hse.server.proto.EntitiesProto.UserModel
import java.io.IOException
import ru.hse.client.R
import ru.hse.client.utility.user

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


fun logInUser(
    login: String,
    password: String,
    activity: Activity,
    okHttpClient: OkHttpClient,
    loginLayout: TextInputLayout,
    passwordLayout: TextInputLayout
) {
    val passwordHash: String = generateHash(password, activity)

    val URlGetUser: String =
        ("http://" + ContextCompat.getString(activity, R.string.IP) + "/users/login").toHttpUrlOrNull()
            ?.newBuilder()
            ?.build()
            .toString()

    val user_: UserModel =
        UserModel.newBuilder().setLogin(login).setPasswordHash(passwordHash).build()

    val requestBody: RequestBody =
        RequestBody.create("application/x-protobuf".toMediaTypeOrNull(), user_.toByteArray())

    val requestForLogInUser: Request = Request.Builder()
        .url(URlGetUser)
        .post(requestBody)
        .build()

    okHttpClient.newCall(requestForLogInUser).enqueue(object : Callback {
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
                val user_: UserModel =
                    UserModel.parseFrom(responseBody?.toByteArray())
                if (user_.hasPasswordHash()) {
                    Log.i("Info", "success")
                    printOkAboutGoodUser(activity)
                    user.setUser(user_)
                    user.setUserClientPassword(passwordHash)
                    val data: Intent = Intent()
                    activity.setResult(RESULT_OK, data)
                    activity.finish()
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


fun registerUser(
    login: String,
    email: String,
    password: String,
    activity: Activity,
    okHttpClient: OkHttpClient,
    loginLayout: TextInputLayout,
    emailLayout: TextInputLayout
) {
    val passwordHash: String = generateHash(password, activity)

    val URlRegistration: String =
        ("http://" + ContextCompat.getString(activity, R.string.IP) + "/users/registration").toHttpUrlOrNull()
            ?.newBuilder()
            ?.build()
            .toString()

    val user_: UserModel =
        UserModel.newBuilder().setLogin(login).setEmail(email).setPasswordHash(passwordHash).build()
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
                val responseBody: ByteString? = response.body?.byteString()
                val registeredUser: UserModel = UserModel.parseFrom(responseBody?.toByteArray())
                val URlGetUser: String =
                    ("http://" + ContextCompat.getString(
                        activity,
                        R.string.IP
                    ) + "/users/userByLogin").toHttpUrlOrNull()
                        ?.newBuilder()
                        ?.addQueryParameter("login", login)
                        ?.build().toString()

                val requestForGetGeneratedUser: Request = Request.Builder()
                    .url(URlGetUser)
                    .header("Authorization", "Bearer " + registeredUser.accessToken)
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
                            val registeredUser: UserModel =
                                UserModel.parseFrom(responseBody?.toByteArray())
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    activity,
                                    "User has been successfully created",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            user.setUser(registeredUser)
                            user.setUserClientPassword(passwordHash)
                            val data: Intent = Intent()
                            activity.setResult(RESULT_OK, data)
                            activity.finish()
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

