package com.example.client

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun isValidEmail(target: CharSequence?): Boolean {
    return if (target == null || TextUtils.isEmpty(target)) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)
        var loginEditText: TextInputEditText = findViewById(R.id.login)
        var loginLayout: TextInputLayout = findViewById(R.id.login_box)

        val textWatcherForLogin: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                val newText = charSequence.toString()
                /* if (...) {
                    loginLayout.error = ""
                } else {
                    loginLayout.boxStrokeColor =
                        ContextCompat.getColor(this@SignUpActivity, R.color.green)
                    loginLayout.error = null
                }  TODO: get login from db and check */

            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        loginEditText.addTextChangedListener(textWatcherForLogin)

        var emailEditText: TextInputEditText = findViewById(R.id.email)
        var emailLayout: TextInputLayout = findViewById(R.id.email_box)

        val textWatcherforEmail: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
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
        emailEditText.addTextChangedListener(textWatcherforEmail)

        var passwordEditText: TextInputEditText = findViewById(R.id.password)
        var passwordLayout: TextInputLayout = findViewById(R.id.password_box)

        val textWatcherforPassword: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                val newText = charSequence.toString()
                if (newText.length <= 4) {
                    passwordLayout.error = "Too short password"
                } else {
                    passwordLayout.boxStrokeColor =
                        ContextCompat.getColor(this@SignUpActivity, R.color.green)
                    passwordLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        passwordEditText.addTextChangedListener(textWatcherforPassword)


        var signUpButton: Button = findViewById(R.id.sign_up_button)

        signUpButton.setOnClickListener {
            var data =
                emailEditText.text.toString() + " | " + loginEditText.text.toString()  + " | " + passwordEditText.text.toString() + "\n"
            if (!isValidEmail(emailEditText.text)) {
                Toast.makeText(this, "Wrong email", Toast.LENGTH_LONG).show()
                Log.e("Sign up : wrong email", data)
                emailEditText.text = null
            }
            /*
            if (...) {
                Toast.makeText(this, "User with this login already exists", Toast.LENGTH_LONG).show()
                Log.e("Sign up : login already exists", data)
            } TODO: get login from db and check */
            if (passwordEditText.text.toString().length <= 4) {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_LONG).show()
                Log.e("Sign up : wrong password", data)
                passwordEditText.text = null
            }
        }
    }
}