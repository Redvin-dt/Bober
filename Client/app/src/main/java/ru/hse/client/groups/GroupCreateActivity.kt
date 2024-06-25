package ru.hse.client.groups

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.OkHttpClient
import ru.hse.client.R
import ru.hse.client.databinding.ActivityGroupCreateBinding
import ru.hse.client.entry.*
import ru.hse.client.utility.DrawerBaseActivity
import java.util.regex.Matcher
import java.util.regex.Pattern


class GroupCreateActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityGroupCreateBinding
    private val context = this@GroupCreateActivity
    private val okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Group create")

        val nameEditText: TextInputEditText = binding.name
        val nameLayout: TextInputLayout = binding.nameBox
        nameEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val textWatcherForName: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                charSequence: CharSequence, start: Int, before: Int, count: Int
            ) {
                val newText = charSequence.toString()
                if (isNotValidGroupName(newText)) {
                    nameLayout.error = "Unacceptable symbols in login"
                } else {
                    nameLayout.boxStrokeColor =
                        ContextCompat.getColor(this@GroupCreateActivity, R.color.green)
                    nameLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        nameEditText.addTextChangedListener(textWatcherForName)



        val passwordEditText: TextInputEditText = binding.password
        val passwordLayout: TextInputLayout = binding.passwordBox
        passwordEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        val textWatcherForPassword: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                charSequence: CharSequence, start: Int, before: Int, count: Int
            ) {
                val newText = charSequence.toString()
                if (isNotValidPassword(newText)) {
                    passwordLayout.error = "Incorrect password"
                } else {
                    passwordLayout.boxStrokeColor = ContextCompat.getColor(this@GroupCreateActivity,
                        R.color.green
                    )
                    passwordLayout.error = null
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        passwordEditText.addTextChangedListener(textWatcherForPassword)


        binding.createGroupButton.setOnClickListener {
            onCreateButtonPressed()
        }
    }


    private fun onCreateButtonPressed() {
        val name: String = binding.name.text.toString()
        val password: String = binding.password.text.toString()

        if (isNotValidGroupName(name)) {
            writeErrorAboutGroupName(name, context)
            Log.e("Create group", "incorrect name: $name")
            return
        }

        if (isNotValidPassword(password)) {
            writeErrorAboutPassword(password, context)
            Log.e("Create group", "incorrect password: $password")
            return
        }

        createGroupRequest(name, password, this@GroupCreateActivity, okHttpClient)

        val intent = Intent(context, GroupSelectMenuActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}

fun isNotValidGroupName(name: CharSequence?): Boolean {
    return if (name == null) {
        true
    } else {
        val namePattern: String = "^([0-9]*)([A-Z]*)([a-z]*)([_-]*).{3,}$"
        val pattern: Pattern = Pattern.compile(namePattern)
        val matcher: Matcher = pattern.matcher(name)
        !matcher.matches()
    }
}

fun writeErrorAboutGroupName(name: CharSequence, activity: Activity) {
    val groupNameSmallLength: String = "^([0-9]*)([A-Z]*)([a-z]*)([_-]*).{0,2}$"
    var pattern: Pattern = Pattern.compile(groupNameSmallLength)
    var matcher: Matcher = pattern.matcher(name)
    if (matcher.matches()) {
        Toast.makeText(activity, "Group name must be at least 3 in length", Toast.LENGTH_LONG).show()
        return
    }

    Toast.makeText(activity, "Group name must consist of letters, numbers and spaces", Toast.LENGTH_LONG).show()
}