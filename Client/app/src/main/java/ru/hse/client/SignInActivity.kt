package ru.hse.client


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okio.ByteString
import ru.hse.server.proto.EntitiesProto
import java.io.IOException


class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

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

            override fun onTextChanged(
                charSequence: CharSequence, start: Int, before: Int, count: Int
            ) {
                val newText = charSequence.toString()
                if (!isValidLogin(newText)) {
                    loginLayout.error = "Unacceptable symbols in login"
                } else {
                    // TODO: get login from db and check
                    loginLayout.boxStrokeColor =
                        ContextCompat.getColor(this@SignInActivity, R.color.green)
                    loginLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        loginEditText.addTextChangedListener(textWatcherForLogin)

        var passwordEditText: TextInputEditText = findViewById(R.id.password)
        var passwordLayout: TextInputLayout = findViewById(R.id.password_box)
        passwordEditText.setOnFocusChangeListener{ v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val textWatcherForPassword: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val passwordOriginal = charSequence.toString()
                if (passwordOriginal.length <= 3) {
                    passwordLayout.error = "Original password is too short"
                } else {
                    passwordLayout.boxStrokeColor = ContextCompat.getColor(this@SignInActivity, R.color.green)
                    passwordLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        passwordEditText.addTextChangedListener(textWatcherForPassword)

        var signInButton: Button = findViewById(R.id.sign_in_button)

        signInButton.setOnClickListener {
            val login: String = loginEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            if (!isValidLogin(login)) {
                writeErrorAboutLogin(login, this)
                Log.e("Sign in", "incorrect login: $login")
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                writeErrorAboutPassword(password, this)
                Log.e("Sign in", "incorrect password: $password")
                return@setOnClickListener
            }

            val URlGetUser: String = ("http://" + getString(R.string.IP) + "/users/userByLogin").toHttpUrlOrNull()
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
                            this@SignInActivity,
                            "Something went wrong, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                var badUserMessage: String = "incorrect login/password"

                fun printErrorAboutBadUser() {
                    Log.e("Info", "no such user with this login")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this@SignInActivity,
                            badUserMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        loginLayout.error = badUserMessage
                        passwordLayout.error = badUserMessage
                    }
                }

                fun printOkAboutGoodUser() {
                    Log.e("Info", "user exist")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this@SignInActivity,
                            "Welcome",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Info", response.toString())
                    if (response.isSuccessful) {
                        val responseBody: ByteString? = response.body?.byteString()
                        val user: EntitiesProto.UserModel =
                            EntitiesProto.UserModel.parseFrom(responseBody?.toByteArray())
                        if (user.hasPasswordHash()) {
                            if (user.passwordHash == password) {
                                printOkAboutGoodUser()
                                // TODO: create main Activity
                            } else {
                                Log.e("Info", "no such login/password")
                                printErrorAboutBadUser()
                            }
                        } else {
                            Log.e("Info", "no such login/password")
                            printErrorAboutBadUser()
                        }
                    } else {
                        response.body?.string()?.let { it1 -> Log.e("Info", it1) }
                        printErrorAboutBadUser()
                    }
                }
            })

        }

        var dontHaveAnAccountButton: Button = findViewById(R.id.dont_have_an_account)
        dontHaveAnAccountButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        var forgotPasswordButton: Button = findViewById(R.id.forgot_password)
        forgotPasswordButton.setOnClickListener {
            // TODO: create an opportunity to restore data
        }
    }

}