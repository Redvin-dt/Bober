package ru.hse.client.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class AuthenticatorService : Service() {
    private val authenticator: Authenticator by lazy { Authenticator(this) }

    override fun onBind(intent: Intent?): IBinder? {
        return AuthenticatorServiceBinder(authenticator)
    }

    class AuthenticatorServiceBinder(private val authenticator: Authenticator) : Binder() {
        fun getAuthenticator(): Authenticator {
            return authenticator
        }
    }

}
