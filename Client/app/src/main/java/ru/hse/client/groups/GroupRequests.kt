package ru.hse.client.groups

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.ByteString
import ru.hse.client.R
import ru.hse.client.entry.generateHash
import ru.hse.client.utility.APPLICATION_PROTOBUF_MEDIA_TYPE
import ru.hse.client.utility.printMessageFromBadResponse
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


fun getGroupsByPrefix(
        prefixName: String,
        activity: Activity,
        okHttpClient: OkHttpClient): List<EntitiesProto.GroupModel>? {
    val urlGetGroupByPrefix: String =
            ("http://" + ContextCompat.getString(activity, R.string.IP) + "/groups/groupByPrefixName").toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.addQueryParameter("groupName", prefixName)
                    ?.build().toString()

    val requestForGetGroups: Request =
            Request.Builder()
                    .url(urlGetGroupByPrefix)
                    .header("Authorization", "Bearer " + user.getUserToken())
                    .build()

    var groupList: MutableList<EntitiesProto.GroupModel>? = null


    val countDownLatch = CountDownLatch(1)
    okHttpClient.newCall(requestForGetGroups).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Error", e.toString() + " " + e.message)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                        activity,
                        "Something went wrong, try again",
                        Toast.LENGTH_SHORT
                ).show()
            }

            countDownLatch.countDown()
        }

        override fun onResponse(call: Call, response: Response) {
            Log.i("Info", response.toString())
            if (response.isSuccessful) {
                val responseBody: ByteString? = response.body?.byteString()
                val groups: EntitiesProto.GroupList = EntitiesProto.GroupList.parseFrom(responseBody?.toByteArray())
                groupList = groups.groupsList

            } else {
                printMessageFromBadResponse(response.body?.string(), activity)
            }
            countDownLatch.countDown()
        }
    })

    countDownLatch.await(5, TimeUnit.SECONDS)
    return groupList
}

fun enterGroup(
        group: EntitiesProto.GroupModel,
        writeErrorMessage: Boolean,
        activity: Activity,
        okHttpClient: OkHttpClient
) : EntitiesProto.GroupModel? {
    val urlEnterGroup : String = ("http://" + ContextCompat.getString(activity, R.string.IP) + "/groups/enter").toHttpUrlOrNull()
            ?.newBuilder()
            ?.addQueryParameter("userLogin", user.getUserLogin())
            ?.build().toString()

    val requestBody: RequestBody = RequestBody.create(APPLICATION_PROTOBUF_MEDIA_TYPE, group.toByteArray())

    val requestForEnterGroup: Request =
            Request.Builder()
                .url(urlEnterGroup)
                .header("Authorization", "Bearer " + user.getUserToken())
                .post(requestBody)
                .build()

    var entryGroupModel : EntitiesProto.GroupModel? = null

    val countDownLatch = CountDownLatch(1);
    okHttpClient.newCall(requestForEnterGroup).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Error", e.toString() + " " + e.message)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                        activity,
                        "Something wrong try again",
                        Toast.LENGTH_SHORT
                ).show()
            }

            countDownLatch.countDown()
        }

        override fun onResponse(call: Call, response: Response) {
            Log.i("Info", response.toString())
            if (response.isSuccessful) {
                val responseBody: ByteString? = response.body?.byteString()
                entryGroupModel = EntitiesProto.GroupModel.parseFrom(responseBody?.toByteArray())
            } else {
                if (writeErrorMessage) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                                activity,
                                "Check password and try again",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            countDownLatch.countDown()
        }
    })

    countDownLatch.await(10, TimeUnit.SECONDS)
    return entryGroupModel
}

fun createGroupRequest(groupName: String, groupPassword: String, activity: Activity, okHttpClient: OkHttpClient){
    val URlCreateGroup: String =
            ("http://" + ContextCompat.getString(activity, R.string.IP) + "/groups").toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.build()
                    .toString()

    if (user.getUserModel() == null){
        Log.e("Group create","Cannot create group user model not found") // TODO: do some additional logic
        return
    }

    val userList: EntitiesProto.UserList = EntitiesProto.UserList.newBuilder().addUsers(user.getUserModel()).build()
    val groupHashedPassword = generateHash(groupPassword, activity)
    val group: EntitiesProto.GroupModel =
            EntitiesProto.GroupModel.newBuilder().setName(groupName).setPasswordHash(groupHashedPassword).setAdmin(user.getUserModel()).setUsers(userList).build()

    val requestBody: RequestBody =
            RequestBody.create("application/x-protobuf".toMediaTypeOrNull(), group.toByteArray())

    val request: Request = Request.Builder()
            .url(URlCreateGroup)
            .post(requestBody)
            .header("Authorization", "Bearer " + user.getUserToken())
            .build()

    Log.i("Info", "Request has been sent $URlCreateGroup")
    val somethingWentWrongMessage: String = "Something went wrong, try again"
    val countDownLatch = CountDownLatch(1)
    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Error", e.toString() + " " + e.message)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(activity, somethingWentWrongMessage, Toast.LENGTH_SHORT).show()
            }
            countDownLatch.countDown()
        }

        override fun onResponse(call: Call, response: Response) {
            Log.i("Info", response.toString())
            if (response.isSuccessful) {
                user.updateUser(activity)
                Log.i("Info", "Group created")
            } else {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(activity, response.body?.string(), Toast.LENGTH_SHORT).show()
                }
            }
            countDownLatch.countDown()
        }
    })

    countDownLatch.await()
}

fun acceptInvite(groupId: Long, activity: Activity, okHttpClient: OkHttpClient) : Boolean {
    val URlCreateGroup: String =
            ("http://" + ContextCompat.getString(activity, R.string.IP) + "/invitations/accept/user").toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.addQueryParameter("groupId", groupId.toString())
                    ?.addQueryParameter("userId", user.getId().toString())
                    ?.build()
                    .toString()

    val requestBody: RequestBody = RequestBody.create(null, ByteArray(0))

    val request: Request = Request.Builder()
            .url(URlCreateGroup)
            .post(requestBody)
            .header("Authorization", "Bearer " + user.getUserToken())
            .build()

    Log.i("GroupRequest", "Request has been sent $URlCreateGroup")

    val somethingWentWrongMessage: String = "Something went wrong, try again"
    val countDownLatch = CountDownLatch(1)
    var isSuccess = false
    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("GroupRequest", e.toString() + " " + e.message)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(activity, somethingWentWrongMessage, Toast.LENGTH_SHORT).show()
            }
            countDownLatch.countDown()
        }

        override fun onResponse(call: Call, response: Response) {
            Log.i("Info", response.toString())
            if (response.isSuccessful) {
                isSuccess = true
                Log.i("GroupRequest", "user accept invite")
            } else {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(activity, response.body?.string(), Toast.LENGTH_SHORT).show()
                }
            }
            countDownLatch.countDown()
        }
    })

    countDownLatch.await()
    return isSuccess
}

fun declineInvite(groupId: Long, activity: Activity, okHttpClient: OkHttpClient) : Boolean {
    val URlCreateGroup: String =
            ("http://" + ContextCompat.getString(activity, R.string.IP) + "/invitations/decline/user").toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.build()
                    .toString()

    val requestBody: RequestBody = RequestBody.create(null, ByteArray(0))

    val request: Request = Request.Builder()
            .url(URlCreateGroup)
            .post(requestBody)
            .header("Authorization", "Bearer " + user.getUserToken())
            .build()

    Log.i("GroupRequest", "Request has been sent $URlCreateGroup")

    val somethingWentWrongMessage: String = "Something went wrong, try again"
    val countDownLatch = CountDownLatch(1)
    var isSuccess = false
    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("GroupRequest", e.toString() + " " + e.message)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(activity, somethingWentWrongMessage, Toast.LENGTH_SHORT).show()
            }
            countDownLatch.countDown()
        }

        override fun onResponse(call: Call, response: Response) {
            Log.i("Info", response.toString())
            if (response.isSuccessful) {
                isSuccess = true
                Log.i("GroupRequest", "user decline invite")
            } else {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(activity, response.body?.string(), Toast.LENGTH_SHORT).show()
                }
            }
            countDownLatch.countDown()
        }
    })

    countDownLatch.await()
    return isSuccess
}
