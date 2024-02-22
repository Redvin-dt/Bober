package com.example.client
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


fun isValidEmail(target: CharSequence?): Boolean {
    return if (target == null || TextUtils.isEmpty(target)) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

fun isValidPassword(target: CharSequence?): Boolean{
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
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
                 if (!isValidPassword(newText)) {
                    loginLayout.setError("Unacceptable symbols in login")
                } else {
                    // TODO: get login from db and check
                    loginLayout.boxStrokeColor =
                        ContextCompat.getColor(this@SignUpActivity, R.color.green)
                    loginLayout.setError(null)
                }

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