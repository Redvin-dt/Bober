package ru.hse.client

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.ByteString
import ru.hse.client.Main.GroupSelectMenuActivity
import ru.hse.server.proto.EntitiesProto.UserModel
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

fun isNotValidEmail(target: CharSequence?): Boolean {
    return if (target == null) {
        true
    } else {
        !Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

fun isNotValidLogin(login: CharSequence?): Boolean {
    return if (login == null) {
        true
    } else {
        val loginPattern: String = "^(?=.*[a-z])([0-9]*)([A-Z]*)(?=\\S+$).{4,}$"
        val pattern: Pattern = Pattern.compile(loginPattern)
        val matcher: Matcher = pattern.matcher(login)
        !matcher.matches()
    }
}

fun writeErrorAboutLogin(login: CharSequence, activity: Activity) {
    val loginPatternWithoutLCaseLetter: String = "^([0-9]*)([A-Z]*)(?=\\S+$).{4,}$"
    var pattern: Pattern = Pattern.compile(loginPatternWithoutLCaseLetter)
    var matcher: Matcher = pattern.matcher(login)
    if (matcher.matches()) {
        Toast.makeText(activity, "Login must contain at least one lower case letter", Toast.LENGTH_LONG).show()
        return
    }
    val loginPatternWithSpace: String = "^(?=.*[a-z])([0-9]*)([A-Z]*).{4,}$"
    pattern = Pattern.compile(loginPatternWithSpace)
    matcher = pattern.matcher(login)
    if (matcher.matches()) {
        Toast.makeText(activity, "Login must not contain spaces", Toast.LENGTH_LONG).show()
        return
    }
    val loginPatternShortLen: String = "^([0-9]*)(?=.*[a-z])([A-Z]*).{0,3}$"
    pattern = Pattern.compile(loginPatternShortLen)
    matcher = pattern.matcher(login)
    if (matcher.matches()) {
        Toast.makeText(activity, "Login must be at least 4 in length", Toast.LENGTH_LONG).show()
        return
    }

    Toast.makeText(activity, "Incorrect login", Toast.LENGTH_LONG).show()
}


fun isNotValidPassword(password: CharSequence?): Boolean {
    return if (password == null) {
        true
    } else {
        val passwordPattern: String = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])([@#$%^&+=]*)(?=\\S+$).{4,}$"
        val pattern: Pattern = Pattern.compile(passwordPattern)
        val matcher: Matcher = pattern.matcher(password)
        !matcher.matches()
    }
}

fun writeErrorAboutPassword(password: CharSequence, activity: Activity) {
    val passwordPatternWithoutDigit: String = "^(?=.*[a-z])(?=.*[A-Z])([@#$%^&+=]*)(?=\\S+$).{4,}$"
    var pattern: Pattern = Pattern.compile(passwordPatternWithoutDigit)
    var matcher: Matcher = pattern.matcher(password)
    if (matcher.matches()) {
        Toast.makeText(activity, "Password must contain at least one digit", Toast.LENGTH_LONG).show()
        return
    }
    val passwordPatternWithoutLCaseLetter: String = "^(?=.*[0-9])(?=.*[A-Z])([@#$%^&+=]*)(?=\\S+$).{4,}$"
    pattern = Pattern.compile(passwordPatternWithoutLCaseLetter)
    matcher = pattern.matcher(password)
    if (matcher.matches()) {
        Toast.makeText(activity, "Password must contain at least one lower case letter", Toast.LENGTH_LONG).show()
        return
    }
    val passwordPatternWithoutUCaseLetter: String = "^(?=.*[0-9])(?=.*[a-z])([@#$%^&+=]*)(?=\\S+$).{4,}$"
    pattern = Pattern.compile(passwordPatternWithoutUCaseLetter)
    matcher = pattern.matcher(password)
    if (matcher.matches()) {
        Toast.makeText(activity, "Password must contain at least one upper case letter", Toast.LENGTH_LONG).show()
        return
    }
    val passwordPatternWithSpace: String = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])([@#$%^&+=]*).{4,}$"
    pattern = Pattern.compile(passwordPatternWithSpace)
    matcher = pattern.matcher(password)
    if (matcher.matches()) {
        Toast.makeText(activity, "Password must not contain spaces", Toast.LENGTH_LONG).show()
        return
    }
    val passwordPatternShortLen: String = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])([@#$%^&+=]*)(?=\\S+\$).{0,3}$"
    pattern = Pattern.compile(passwordPatternShortLen)
    matcher = pattern.matcher(password)
    if (matcher.matches()) {
        Toast.makeText(activity, "Password must be at least 4 in length", Toast.LENGTH_LONG).show()
        return
    }

    Toast.makeText(activity, "Incorrect password", Toast.LENGTH_LONG).show()
}


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
    val URlGetUser: String = ("http://" + getString(activity, R.string.IP) + "/users/userByLogin").toHttpUrlOrNull()
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
                val user_: UserModel =
                    UserModel.parseFrom(responseBody?.toByteArray())
                if (user_.hasPasswordHash()) {
                    if (user_.passwordHash == password) {
                        printOkAboutGoodUser(activity)
                        user.setUser(user_)
                        val intent = Intent(activity, GroupSelectMenuActivity::class.java)
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
        ("http://" + getString(activity, R.string.IP) + "/users/registration").toHttpUrlOrNull()
            ?.newBuilder()
            ?.build()
            .toString()

    val user_: UserModel =
        UserModel.newBuilder().setLogin(login).setEmail(email).setPasswordHash(password).build()
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
                    ("http://" + getString(activity, R.string.IP) + "/users/userByLogin").toHttpUrlOrNull()
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
                        if (response.code == 200) {
                            val responseBody: ByteString? = response.body?.byteString()
                            val registeredUser: UserModel = UserModel.parseFrom(responseBody?.toByteArray())
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    activity,
                                    "User has been successfully created",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            user.setUser(registeredUser)
                            val intent = Intent(activity, GroupSelectMenuActivity::class.java)
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

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

