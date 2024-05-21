package ru.hse.client.auth

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.OkHttpClient
import ru.hse.client.R


class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val okHttpClient = OkHttpClient()

        val loginEditText: TextInputEditText = findViewById(R.id.login)
        val loginLayout: TextInputLayout = findViewById(R.id.login_box)
        loginEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val textWatcherForLogin: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                charSequence: CharSequence, start: Int, before: Int, count: Int
            ) {
                val newText = charSequence.toString()
                if (isNotValidLogin(newText)) {
                    loginLayout.error = "Unacceptable symbols in login"
                } else {
                    loginLayout.boxStrokeColor =
                        ContextCompat.getColor(this@SignInActivity, R.color.green)
                    loginLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        loginEditText.addTextChangedListener(textWatcherForLogin)

        val passwordEditText: TextInputEditText = findViewById(R.id.password)
        val passwordLayout: TextInputLayout = findViewById(R.id.password_box)
        passwordEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val textWatcherForPassword: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val passwordOriginal = charSequence.toString()
                if (isNotValidPassword(passwordOriginal)) {
                    passwordLayout.error = "Incorrect password"
                } else {
                    passwordLayout.boxStrokeColor = ContextCompat.getColor(this@SignInActivity,
                        R.color.green
                    )
                    passwordLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        passwordEditText.addTextChangedListener(textWatcherForPassword)

        val signInButton: Button = findViewById(R.id.sign_in_button)

        signInButton.setOnClickListener {
            val login: String = loginEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            if (isNotValidLogin(login)) {
                writeErrorAboutLogin(login, this)
                Log.e("Sign in", "incorrect login: $login")
                return@setOnClickListener
            }

            if (isNotValidPassword(password)) {
                writeErrorAboutPassword(password, this)
                Log.e("Sign in", "incorrect password: $password")
                return@setOnClickListener
            }

            tryToLogInUser(login, password, this@SignInActivity, okHttpClient, loginLayout, passwordLayout)

        }

        val dontHaveAnAccountButton: Button = findViewById(R.id.dont_have_an_account)
        dontHaveAnAccountButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val forgotPasswordButton: Button = findViewById(R.id.forgot_password)
        forgotPasswordButton.setOnClickListener {
            // TODO: create an opportunity to restore data
        }
    }

}