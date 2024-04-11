package ru.hse.client

import ru.hse.server.proto.EntitiesProto.UserModel

class User {
    private var user: UserModel? = null

    private fun userExist(): Boolean {
        return user != null
    }

    fun setUser(newUser: UserModel) {
        user = newUser
    }

    fun isUserHasLogin(): Boolean {
        return userExist() && user!!.hasLogin()
    }

    fun getUserLogin(): String {
        return user!!.getLogin()
    }

    fun isUserHasEmail(): Boolean {
        return userExist() && user!!.hasEmail()
    }

    fun getUserEmail(): String {
        return user!!.getEmail()
    }

}

var user: User = User()

