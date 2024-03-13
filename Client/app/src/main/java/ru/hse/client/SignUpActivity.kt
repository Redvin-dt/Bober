package ru.hse.client

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.ByteString
import ru.hse.server.proto.EntitiesProto.UserModel
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

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        hideKeyboard()

        val okHttpClient = OkHttpClient()

        var loginEditText: TextInputEditText = findViewById(R.id.login)
        var loginLayout: TextInputLayout = findViewById(R.id.login_box)
        loginEditText.setOnFocusChangeListener{ v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

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
        emailEditText.setOnFocusChangeListener{ v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

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
        passwordEditText.setOnFocusChangeListener{ v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        var passwordConfirmEditText: TextInputEditText = findViewById(R.id.password_confirm)
        var passwordConfirmLayout: TextInputLayout = findViewById(R.id.password_confirm_box)
        passwordConfirmEditText.setOnFocusChangeListener{ v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

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
            val email: String = emailEditText.text.toString()
            val login: String = loginEditText.text.toString()
            val password: String = passwordEditText.text.toString()
            val password_confirm: String = passwordConfirmEditText.text.toString()

            if (!isValidEmail(email)) {
                Toast.makeText(this, "This is not an email", Toast.LENGTH_LONG).show()
                Log.e("Sign up", "wrong email: $email")
                return@setOnClickListener
            }

            if (login.isEmpty() || !isValidLogin(login)) {
                Toast.makeText(this, "Incorrect login", Toast.LENGTH_LONG).show()
                loginLayout.error = "Invalid login"
                Log.e("Sign up", "bad login: $login")
                return@setOnClickListener
            }

            if (password.length <= 3) {
                Toast.makeText(this, "Password is too short", Toast.LENGTH_LONG).show()
                Log.e("Sign up", "short password:    $password")
                return@setOnClickListener
            }

            if (password != password_confirm) {
                Toast.makeText(this, "Password mismatch", Toast.LENGTH_LONG).show()
                Log.e("Sign up", "password missmath $password and $password_confirm")
                return@setOnClickListener
            }

            val URlRegistration: String = ("http://" + getString(R.string.IP) + "/users/registration").toHttpUrlOrNull()
                ?.newBuilder()
                ?.build()
                .toString()

            val user: UserModel = UserModel.newBuilder().setLogin(login).setEmail(email).setPasswordHash(password).build()
            val requestBody: RequestBody = RequestBody.create("application/x-protobuf".toMediaTypeOrNull(), user.toByteArray());

            val requestForRegisterUser: Request = Request.Builder()
                .url(URlRegistration)
                .post(requestBody)
                .build()

            Log.i("Info", "Request has been sent $URlRegistration")
            val sometghinWentWrongMessage: String = "Something went wrong, try again"
            okHttpClient.newCall(requestForRegisterUser).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("Error", e.toString() + " " + e.message)
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@SignUpActivity, sometghinWentWrongMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Info", response.toString())
                    if (response.code == 200) {
                        val URlGetUser: String = ("http://" + getString(R.string.IP) + "/users/userByLogin").toHttpUrlOrNull()
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
                                    Toast.makeText(this@SignUpActivity, sometghinWentWrongMessage + " with another login", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onResponse(call: Call, response: Response) {
                                Log.i("Info", response.toString())
                                if (response.code == 200) {
                                    val responseBody: ByteString? = response.body?.byteString()
                                    val registredUser: UserModel = UserModel.parseFrom(responseBody?.toByteArray())
                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(this@SignUpActivity, "User has been successfully created", Toast.LENGTH_SHORT).show()
                                    }
                                    // TODO: create main Activity
                                }
                            }
                        })

                    } else if (response.code == 400) {
                        val loginErrorMessage: String = "user with that login already exist"
                        val emailErrorMessage: String = "user with that email already exist"
                        if (response.body?.string() == loginErrorMessage) {
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    loginErrorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                                loginLayout.error = loginErrorMessage
                            }
                        } else if (response.body?.string() == emailErrorMessage) {
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    emailErrorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                                emailLayout.error = emailErrorMessage
                            }
                        } else {
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    sometghinWentWrongMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            })
        }

        var haveAnAccountButton: Button = findViewById(R.id.have_an_account)
        haveAnAccountButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

}
