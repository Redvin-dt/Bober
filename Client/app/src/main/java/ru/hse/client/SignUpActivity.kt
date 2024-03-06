package ru.hse.client

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import java.io.IOException

fun isValidEmail(target: CharSequence?): Boolean {
    return if (target == null || TextUtils.isEmpty(target)) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

fun isValidLogin(target: CharSequence?): Boolean {
    if (target != null) {
        for (c in target) {
            if (!c.isDigit() && !c.isLetter() && c != '_' && c != '-') {
                return false
            }
        }
        return true
    } else {
        return false
    }
}

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)
        val okHttpClient = OkHttpClient()

        var loginEditText: TextInputEditText = findViewById(R.id.login)
        var loginLayout: TextInputLayout = findViewById(R.id.login_box)

        val textWatcherForLogin: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int
            ) {
                val newText = charSequence.toString()
                if (!isValidLogin(newText)) {
                    loginLayout.error = "Unacceptable symbols in login"
                } else {
                    // TODO: get login from db and check
                    loginLayout.boxStrokeColor =
                        ContextCompat.getColor(this@SignUpActivity, R.color.green)
                    loginLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        loginEditText.addTextChangedListener(textWatcherForLogin)

        var emailEditText: TextInputEditText = findViewById(R.id.email)
        var emailLayout: TextInputLayout = findViewById(R.id.email_box)

        val textWatcherForEmail: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val newText = charSequence.toString()
                if (!isValidEmail(newText)) {
                    emailLayout.error = "This is not a valid email"
                } else {
                    emailLayout.boxStrokeColor =
                        ContextCompat.getColor(this@SignUpActivity, R.color.green)
                    emailLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        emailEditText.addTextChangedListener(textWatcherForEmail)

        var passwordEditText: TextInputEditText = findViewById(R.id.password)
        var passwordLayout: TextInputLayout = findViewById(R.id.password_box)

        var passwordConfirmEditText: TextInputEditText = findViewById(R.id.password_confirm)
        var passwordConfirmLayout: TextInputLayout = findViewById(R.id.password_confirm_box)

        val textWatcherForPassword: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val passwordOriginal = charSequence.toString()
                val passwordConfirm = passwordConfirmEditText.text.toString()
                if (passwordOriginal.length <= 3) {
                    passwordConfirmLayout.error = "Original password is too short"
                    passwordLayout.error = "Password is too short"
                } else if (passwordOriginal != passwordConfirm) {
                    passwordLayout.error = null
                    passwordLayout.boxStrokeColor =
                        ContextCompat.getColor(this@SignUpActivity, R.color.green)
                    passwordConfirmLayout.error = "Passwords are different"
                } else {
                    passwordConfirmLayout.boxStrokeColor =
                        ContextCompat.getColor(this@SignUpActivity, R.color.green)
                    passwordLayout.boxStrokeColor = passwordConfirmLayout.boxStrokeColor
                    passwordConfirmLayout.error = null
                    passwordLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        passwordEditText.addTextChangedListener(textWatcherForPassword)

        val textWatcherForPasswordConfirm: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val passwordOriginal = passwordEditText.text.toString()
                val passwordConfirm = charSequence.toString()
                if (passwordOriginal.length <= 3) {
                    passwordConfirmLayout.error = "Original password is too short"
                    passwordLayout.error = "Password is too short"
                } else if (passwordOriginal != passwordConfirm) {
                    passwordLayout.error = null
                    passwordLayout.boxStrokeColor =
                        ContextCompat.getColor(this@SignUpActivity, R.color.green)
                    passwordConfirmLayout.error = "Passwords are different"
                } else {
                    passwordConfirmLayout.boxStrokeColor =
                        ContextCompat.getColor(this@SignUpActivity, R.color.green)
                    passwordLayout.boxStrokeColor = passwordConfirmLayout.boxStrokeColor
                    passwordConfirmLayout.error = null
                    passwordLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        passwordConfirmEditText.addTextChangedListener(textWatcherForPasswordConfirm)

        var signUpButton: Button = findViewById(R.id.sign_up_button)

        signUpButton.setOnClickListener {

            val request: Request = Request.Builder()
                .url("http://192.168.0.107:8080/users/userById?id=1")
                .build()

            Log.i("Info", "Request has been sent")
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("Error", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Info", response.toString())
                }
            })
        }
    }
}
