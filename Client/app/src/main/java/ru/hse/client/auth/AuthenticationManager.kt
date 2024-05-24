package ru.hse.client.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import ru.hse.client.main.MainActivity
import ru.hse.client.main.user


class AuthenticationManager(context: Context) {
    private var mContext: Context = context
    private var mAccountManager: AccountManager = AccountManager.get(mContext)
    private lateinit var mAccount: Account
    private var mAuthService: AuthenticatorService? = null
    private var ACCOUNT_TYPE: String = "ru.hse.client"
    private var AUTHENTICATION_TOKEN_TYPE = "token"

    fun tryToLogInAccount(): Boolean {
        var accounts: Array<out Account> = mAccountManager.getAccountsByType(ACCOUNT_TYPE)
        for (account: Account in accounts) {
            if (mAccountManager.getUserData(account, "is_account_active") == "true") {
                mAccount = account
                updateToken()
                return true
            }
        }
        return false
    }

    private fun updateToken() {
        val login: String = mAccountManager.getUserData(mAccount, "username")
        val password: String = mAccountManager.getUserData(mAccount, "password")
        val email: String = mAccount.name

        user.updateUserFromServer(mContext, login, password, email)
        mAccountManager.setAuthToken(mAccount, AUTHENTICATION_TOKEN_TYPE, user.getUserToken())
    }

    fun createAccount() {
        val accounts: Array<out Account> = mAccountManager.getAccountsByType(ACCOUNT_TYPE)
        for (account: Account in accounts) {
            if (account.name == user.getUserEmail()) {
                mAccountManager.setUserData(account, "is_account_active", "true")
                mAccount = account
                updateToken()
                return
            }
            mAccountManager.setUserData(account, "is_account_active", "false")
        }
        mAccount = Account(user.getUserEmail(), ACCOUNT_TYPE)
        val options = Bundle()
        options.putString("username", user.getUserLogin())
        options.putString("password", user.getUserClientPassword())
        options.putString("is_account_active", "true")
        if (mAccountManager.addAccountExplicitly(mAccount, user.getHashedPassword(), options)) {
            Log.d("Authenticator", "Account created, name: ${mAccount.name}")
        } else {
            Log.e("Authenticator", "Account not created, name: ${mAccount.name}")
        }
        mAccountManager.setAuthToken(mAccount, AUTHENTICATION_TOKEN_TYPE, user.getUserToken())
    }

    fun deleteAccount() {
        val account = Account(mAccount.name, ACCOUNT_TYPE)
        if (mAccountManager.removeAccountExplicitly(account)) {
            Log.d("Authenticator", "Account deleted successfully")
        } else {
            Log.e("Authenticator", "Account not deleted")
        }
    }

    fun logOut() {
        mAccountManager.setUserData(mAccount, "is_account_active", "false")
        val intent: Intent = Intent(mContext, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        mContext.startActivity(intent)
    }

    inner class OnTokenAcquired : AccountManagerCallback<Bundle> {

        override fun run(result: AccountManagerFuture<Bundle>) {
            val bundle: Bundle = result.result

            val token: String = bundle.getString(AccountManager.KEY_AUTHTOKEN).toString()
            val account: Account = this@AuthenticationManager.mAccount
            val email: String =
                this@AuthenticationManager.mAccountManager.getUserData(account, "email")

            user.setUserByEmail(mContext, email, token)
        }
    }

}

class SingletonController {
    interface Controller {
        fun set(context: Context)

        fun getManager() : AuthenticationManager
    }

    private class ControllerImpl() : Controller {
        private var mContext: Context?
        var mAuthManager: AuthenticationManager?

        override fun set(context: Context) {
            mContext = context
            mAuthManager = AuthenticationManager(mContext!!)
        }

        override fun getManager() : AuthenticationManager {
            return mAuthManager!!
        }

        init {
            mContext = null
            mAuthManager = null
        }

    }

    companion object {
        private var instance: Controller = ControllerImpl()

        fun getInstance() : Controller {
            return instance
        }
    }

}
