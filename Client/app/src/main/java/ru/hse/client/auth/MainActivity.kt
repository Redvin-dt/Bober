package ru.hse.client.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ru.hse.client.entry.SignInActivity
import ru.hse.client.groups.GroupSelectMenuActivity


class MainActivity : AppCompatActivity() {

    private lateinit var mAuthManager: AuthenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val controller = SingletonController.getInstance()
        controller.set(this)
        mAuthManager = controller.getManager()

        if (mAuthManager.tryToLogInAccount()) {
            val intent = Intent(this, GroupSelectMenuActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            ContextCompat.startActivity(this, intent, null)
        }
         else {
            val intent = Intent(this, SignInActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivityForResult(intent, 100)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            mAuthManager.createAccount()
            val intent = Intent(this, GroupSelectMenuActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            ContextCompat.startActivity(this, intent, null)
        }
    }
}
