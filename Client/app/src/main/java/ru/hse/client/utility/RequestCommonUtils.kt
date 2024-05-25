package ru.hse.client.utility

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout

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