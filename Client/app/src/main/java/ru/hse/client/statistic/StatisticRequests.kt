package ru.hse.client.statistic

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import ru.hse.client.R
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto.UserStatistic
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun getTestByGroupStatistic(activity: Activity, okHttpClient: OkHttpClient) : UserStatistic? {
    val URlCreateGroup: String =
            ("http://" + ContextCompat.getString(activity, R.string.IP) + "/statistic/test/group").toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.addQueryParameter("userId", user.getId().toString())
                    ?.build()
                    .toString()

    val request: Request = Request.Builder()
            .url(URlCreateGroup)
            .get()
            .header("Authorization", "Bearer " + user.getUserToken())
            .build()

    Log.i("StatisticRequest", "Request has been sent $URlCreateGroup")

    val somethingWentWrongMessage: String = "Something went wrong, try again"
    val countDownLatch = CountDownLatch(1)
    var userStatistic : UserStatistic? = null
    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("StatisticRequest", e.toString() + " " + e.message)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(activity, somethingWentWrongMessage, Toast.LENGTH_SHORT).show()
            }
            countDownLatch.countDown()
        }

        override fun onResponse(call: Call, response: Response) {
            Log.i("Info", response.toString())
            if (response.isSuccessful) {
                userStatistic = UserStatistic.parseFrom(response.body?.bytes())
                Log.i("StatisticRequest", "get test by group statistic")
            } else {
                Log.e("StatisticRequest", "failed to get test by group statistic response status $response")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(activity, response.body?.string(), Toast.LENGTH_SHORT).show()
                }
            }
            countDownLatch.countDown()
        }
    })

    countDownLatch.await(10, TimeUnit.SECONDS)
    return userStatistic
}

fun getTestByDateStatistic(activity: Activity, okHttpClient: OkHttpClient) : UserStatistic? {
    val URlCreateGroup: String =
            ("http://" + ContextCompat.getString(activity, R.string.IP) + "/statistic/test/date").toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.addQueryParameter("userId", user.getId().toString())
                    ?.build()
                    .toString()

    val request: Request = Request.Builder()
            .url(URlCreateGroup)
            .get()
            .header("Authorization", "Bearer " + user.getUserToken())
            .build()

    Log.i("StatisticRequest", "Request has been sent $URlCreateGroup")

    val somethingWentWrongMessage: String = "Something went wrong, try again"
    val countDownLatch = CountDownLatch(1)
    var userStatistic : UserStatistic? = null
    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("StatisticRequest", e.toString() + " " + e.message)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(activity, somethingWentWrongMessage, Toast.LENGTH_SHORT).show()
            }
            countDownLatch.countDown()
        }

        override fun onResponse(call: Call, response: Response) {
            Log.i("Info", response.toString())
            if (response.isSuccessful) {
                userStatistic = UserStatistic.parseFrom(response.body?.bytes())
                Log.i("StatisticRequest", "get test by group statistic")
            } else {
                Log.e("StatisticRequest", "failed to get test by group statistic response status $response")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(activity, response.body?.string(), Toast.LENGTH_SHORT).show()
                }
            }
            countDownLatch.countDown()
        }
    })

    countDownLatch.await(10, TimeUnit.SECONDS)
    return userStatistic
}
