package ru.hse.client.groups

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okio.ByteString
import ru.hse.client.R
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

    val requestForGetGroups: Request = Request.Builder().url(urlGetGroupByPrefix).build()

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
            Log.i("Info", response.toString());
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
        activity: Activity,
        okHttpClient: OkHttpClient
) {
    val urlEnterGroup : String = ("http://" + ContextCompat.getString(activity, R.string.IP) + "/groups/enter").toHttpUrlOrNull()
            ?.newBuilder()
            ?.addQueryParameter("userId", user.getUserLogin())
            ?.build().toString()

    val requestForEnterGroup =
}