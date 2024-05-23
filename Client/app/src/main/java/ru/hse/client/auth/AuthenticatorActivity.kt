package ru.hse.client.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.OperationCanceledException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ru.hse.client.databinding.ActivityAuthenticatorBinding


class AuthenticatorActivity: AppCompatActivity(), AccountManagerCallback<Bundle> {

    companion object {
        const val ARG_ACCOUNT_NAME: String = "ACCOUNT_NAME"
        const val ARG_ACCOUNT_TYPE: String = "ACCOUNT_TYPE"
        const val TOKEN: String = "TOKEN"
        const val ARG_IS_ADDING_NEW_ACCOUNT: String = "IS_ADDING_ACCOUNT"
    }

    private lateinit var accountManger: AccountManager
    private lateinit var account: Account
    private lateinit var binding: ActivityAuthenticatorBinding
    private var authService: AuthenticatorService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountManger = AccountManager.get(this)

        binding.createAccount.setOnClickListener {
            createAccount()
        }

        binding.deleteAccount.setOnClickListener {
            deleteAccount()
        }

        val intent = Intent(this, AuthenticatorService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

    }

    override fun onDestroy() {
        super.onDestroy()

        authService?.let {
            unbindService(connection)
            authService = null
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            authService = (service as AuthenticatorService.AuthenticatorServiceBinder)?.getAuthenticator()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            authService = null
        }
    }

    private fun deleteAccount() {
        val accountManager = AccountManager.get(this)
        val account = Account("name", "ru.hse.client")
        accountManager.removeAccountExplicitly(account)
    }

    private fun createAccount() {
        val accountManager = AccountManager.get(this)
        val account = Account("name", "ru.hse.client")
        val options = Bundle()
        options.putString("username", "name1")
        options.putString("password", "passwrd1")
        accountManager.addAccountExplicitly(account, "abc", options)

        Log.d("Authenticator", "Account created")
    }

}