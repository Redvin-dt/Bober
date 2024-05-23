package ru.hse.client.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class AuthenticatorService : Service() {
    private val authenticator: AccountAuthenticator by lazy { AccountAuthenticator(this) }

    override fun onBind(intent: Intent?): IBinder? {
        return AuthenticatorServiceBinder(authenticator)
    }

    class AuthenticatorServiceBinder(private val accountAuthenticator: AccountAuthenticator) : Binder() {
        fun getAuthenticator(): AccountAuthenticator {
            return accountAuthenticator
        }
    }

}
