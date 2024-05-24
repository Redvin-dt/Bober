package ru.hse.client.entry

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.IOException
import java.security.spec.KeySpec
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import ru.hse.client.auth.MainActivity


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

object Util {
    @Throws(IOException::class)
    fun getProperty(key: String?, context: Context): String {
        val properties: Properties = Properties()

        val assetManager = context.assets
        val inputStream = assetManager.open("app.properties")
        properties.load(inputStream)
        return properties.getProperty(key)
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun generateHash(password: String, context: Context): String {
    val secretKey = Util.getProperty("SECRET_KEY", context)
    val algorithm = Util.getProperty("ALGORITHM", context)
    val iterations = Util.getProperty("ITERATIONS", context).toInt()
    val keyLength = Util.getProperty("KEY_LENGTH", context).toInt()
    val combined = secretKey.toByteArray()
    val factory: SecretKeyFactory = SecretKeyFactory.getInstance(algorithm)
    val spec: KeySpec = PBEKeySpec(password.toCharArray(), combined, iterations, keyLength)
    val key: SecretKey = factory.generateSecret(spec)
    val hash: ByteArray = key.encoded
    return hash.toHexString()
}

