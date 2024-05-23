package ru.hse.client.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.hse.client.main.GroupSelectMenuActivity
import ru.hse.client.main.user
import javax.security.auth.callback.Callback


class AuthenticationManager(context: Context) {
    private var mContext: Context = context
    private var mAccountManger: AccountManager = AccountManager.get(mContext)
    private lateinit var mAccount: Account
    private var mAuthService: AuthenticatorService? = null
    private var ACCOUNT_TYPE: String = "ru.hse.client"
    private var AUTHENTICATION_TOKEN_TYPE = "oauth"


    fun tryToLogInAccount() {
        var accounts: Array<out Account> = mAccountManger.getAccountsByType(ACCOUNT_TYPE)
        for (account: Account in accounts) {
            if (mAccountManger.getUserData(account, "is_account_active") == "true") {
                mAccount = account
                if (accountHasValidToken()) {
                    mAccountManger.getAuthToken(
                        account,
                        AUTHENTICATION_TOKEN_TYPE,
                        null,
                        false,
                        OnTokenAcquired(),
                        null
                    );
                }
            }
        }
    }

    private fun accountHasValidToken(): Boolean {
        TODO("Not implemented yet")
    }

    inner class OnTokenAcquired : AccountManagerCallback<Bundle> {

        override fun run(result: AccountManagerFuture<Bundle>) {
            val bundle: Bundle = result.result

            val token: String = bundle.getString(AccountManager.KEY_AUTHTOKEN).toString()
            val account: Account = this@AuthenticationManager.mAccount
            val email: String =
                this@AuthenticationManager.mAccountManger.getUserData(account, "email")

            user = getUserByEmail(email, token)

            val intent: Intent = Intent(mContext, GroupSelectMenuActivity::class.java)
            mContext.startActivity(intent)
        }
    }


}

