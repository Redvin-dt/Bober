package ru.hse.client.auth

import android.content.Intent
import ru.hse.client.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        hideKeyboard()

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

        val emailEditText: TextInputEditText = findViewById(R.id.email)
        val emailLayout: TextInputLayout = findViewById(R.id.email_box)
        emailEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val textWatcherForEmail: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val newText = charSequence.toString()
                if (isNotValidEmail(newText)) {
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

        val passwordEditText: TextInputEditText = findViewById(R.id.password)
        val passwordLayout: TextInputLayout = findViewById(R.id.password_box)
        passwordEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val passwordConfirmEditText: TextInputEditText = findViewById(R.id.password_confirm)
        val passwordConfirmLayout: TextInputLayout = findViewById(R.id.password_confirm_box)
        passwordConfirmEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        fun checkPasswords(passwordOriginal: CharSequence, passwordConfirm: CharSequence) {
            if (isNotValidPassword(passwordOriginal)) {
                passwordConfirmLayout.error = "Incorrect password"
                passwordLayout.error = "Incorrect password"
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

        val textWatcherForPassword: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val passwordOriginal = charSequence.toString()
                val passwordConfirm = passwordConfirmEditText.text.toString()
                checkPasswords(passwordOriginal, passwordConfirm)
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
                checkPasswords(passwordOriginal, passwordConfirm)

            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        passwordConfirmEditText.addTextChangedListener(textWatcherForPasswordConfirm)

        val signUpButton: Button = findViewById(R.id.sign_up_button)

        signUpButton.setOnClickListener {
            val email: String = emailEditText.text.toString()
            val login: String = loginEditText.text.toString()
            val password: String = passwordEditText.text.toString()
            val passwordConfirm: String = passwordConfirmEditText.text.toString()

            if (isNotValidEmail(email)) {
                Toast.makeText(this, "This is not an email", Toast.LENGTH_LONG).show()
                Log.e("Sign up", "wrong email: $email")
                return@setOnClickListener
            }

            if (isNotValidLogin(login)) {
                writeErrorAboutLogin(login, this)
                Log.e("Sign up", "incorrect login: $login")
                return@setOnClickListener
            }

            if (isNotValidPassword(password)) {
                writeErrorAboutPassword(password, this)
                Log.e("Sign up", "incorrect password: $password")
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                Toast.makeText(this, "Password mismatch", Toast.LENGTH_LONG).show()
                Log.e("Sign up", "password mismatch $password and $passwordConfirm")
                return@setOnClickListener
            }

            tryToRegisterUser(login, email, password, this@SignUpActivity, okHttpClient, loginLayout, emailLayout)

        }


        val haveAnAccountButton: Button = findViewById(R.id.have_an_account)
        haveAnAccountButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

}
