package ru.hse.client.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class AuthenticatorService : Service() {
    private val authenticator: AccountManager by lazy { AccountManager.get(this) }

    override fun onBind(intent: Intent?): IBinder? {
        return AuthenticatorServiceBinder(authenticator)
    }

    class AuthenticatorServiceBinder(private val accountAuthenticator: AccountManager) : Binder() {
        fun getAuthenticator(): AccountManager {
            return accountAuthenticator
        }
    }

}
