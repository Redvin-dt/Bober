package ru.hse.client

import ru.hse.server.proto.EntitiesProto.GroupList
import ru.hse.server.proto.EntitiesProto.GroupModel
import ru.hse.server.proto.EntitiesProto.UserModel
import java.util.Optional

class User {
    private var user: UserModel? = null
    private var groups: List<GroupModel> = listOf()

    private fun userExist(): Boolean {
        return user != null
    }

    fun setUser(newUser: UserModel) {
        user = newUser

        if (newUser.hasUserOfGroups()) {
            var groupListProto = newUser.userOfGroups

            groups = groupListProto.groupsList
        }
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

    fun getUserGroups(): List<GroupModel> {
        return groups
    }
}

var user: User = User()
