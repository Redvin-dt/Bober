package ru.hse.client.auth

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import java.util.*


class AccountAuthenticator(context: Context?) : AbstractAccountAuthenticator(context) {

    private val TOKEN = "BOBERAuthenticator"
    private val mContext: Context? = context

    override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle {
        TODO("Not yet implemented")
    }

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        Log.d("auth", "add account")
        val result = Bundle()
        val username = options?.getString("username") ?: ""
        val password = options?.getString("password") ?: ""
        val account = Account(username, accountType)
        result.putParcelable(AccountManager.KEY_ACCOUNT_NAME, account)
        result.putParcelable(AccountManager.KEY_ACCOUNT_TYPE, accountType as Parcelable?)
        result.putString(AccountManager.KEY_PASSWORD, password)
        val token = UUID.randomUUID().toString()
        result.putString(AccountManager.KEY_AUTHTOKEN, token)
        return result
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        Log.d("auth", "get token")

        // check that authToken type is supported
        if (!authTokenType.equals("Read only") && !authTokenType.equals("Full access")) {
            val result = Bundle()
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType")
            return result
        }

        val accountManager = AccountManager.get(mContext)
        val authToken = accountManager.peekAuthToken(account, authTokenType)

        Log.d("auth", "peekAuthToken returned: $authToken")

        // if there is no token, then the client needs to log into the account
        if (TextUtils.isEmpty(authToken)) {
            val intent = Intent(mContext, AuthenticatorActivity::class.java)
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            val bundle = Bundle()
            bundle.putParcelable(AccountManager.KEY_INTENT, intent)
            return bundle
        }

        val bundle = Bundle()
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account!!.name)
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
        bundle.putString(AccountManager.KEY_AUTHTOKEN, authToken)
        return bundle
    }

    override fun getAuthTokenLabel(authTokenType: String?): String {
        TODO("Not yet implemented")
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun getAccountRemovalAllowed(response: AccountAuthenticatorResponse?, account: Account?): Bundle {
        return super.getAccountRemovalAllowed(response, account)
    }

}