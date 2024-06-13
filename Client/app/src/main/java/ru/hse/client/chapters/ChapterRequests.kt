package ru.hse.client.chapters

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
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.TestModel
import ru.hse.server.proto.EntitiesProto.UserModel
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


fun getChapter(
    chapter: EntitiesProto.ChapterModel,
    writeErrorMessage: Boolean,
    activity: Activity,
    okHttpClient: OkHttpClient
) : EntitiesProto.ChapterModel? {
    val urlChapterGet : String = ("http://" + ContextCompat.getString(activity, R.string.IP) + "/chapters/chapterById").toHttpUrlOrNull()
        ?.newBuilder()
        ?.addQueryParameter("id", chapter.id.toString())
        ?.build().toString()

    val requestForGetGroups: Request =
        Request.Builder()
            .url(urlChapterGet)
            .header("Authorization", "Bearer " + user.getUserToken())
            .build()


    var getChapterModel : EntitiesProto.ChapterModel? = null

    val countDownLatch = CountDownLatch(1);
    okHttpClient.newCall(requestForGetGroups).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Error while getChapter", e.toString() + " " + e.message)
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
                getChapterModel = EntitiesProto.ChapterModel.parseFrom(responseBody?.toByteArray())
            } else {
                if (writeErrorMessage) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            activity,
                            "Check connection, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            countDownLatch.countDown()
        }
    })

    countDownLatch.await(10, TimeUnit.SECONDS)
    return getChapterModel
}

fun getChapterText(
    chapter: EntitiesProto.ChapterModel,
    writeErrorMessage: Boolean,
    activity: Activity,
    okHttpClient: OkHttpClient
) : String? {
    val urlFileChapterGet : String = ("http://" + ContextCompat.getString(activity, R.string.IP) + "/files/getFile").toHttpUrlOrNull()
        ?.newBuilder()
        ?.addQueryParameter("fileName", chapter.textFile)
        ?.build().toString()

    val requestForGetChapterFile: Request =
        Request.Builder()
            .url(urlFileChapterGet)
            .header("Authorization", "Bearer " + user.getUserToken())
            .build()

    var result: String? = null
    val countDownLatch = CountDownLatch(1);
    okHttpClient.newCall(requestForGetChapterFile).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Error while getChapterText", e.toString() + " " + e.message)
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
                val `in` = response.body!!.byteStream()
                val reader = BufferedReader(InputStreamReader(`in`))
                var line = reader.readLine()
                result = line
                while ((reader.readLine().also { line = it }) != null) {
                    result += line
                }
                println(result)
                response.body!!.close()
            } else {
                if (writeErrorMessage) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            activity,
                            "Check connection, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            countDownLatch.countDown()
        }
    })

    countDownLatch.await(10, TimeUnit.SECONDS)
    return result
}

fun setChapterText(
    chapter: EntitiesProto.ChapterModel,
    text: String,
    writeErrorMessage: Boolean,
    activity: Activity,
    okHttpClient: OkHttpClient
) : EntitiesProto.ChapterModel? {
    val file = File.createTempFile("file_to_send", chapter.id.toString())
    file.writeText(text, StandardCharsets.UTF_8)

    val urlChapterGet : String = ("http://" + ContextCompat.getString(activity, R.string.IP) + "/files/loadFileToChapter").toHttpUrlOrNull()
        ?.newBuilder()
        ?.addQueryParameter("id", chapter.id.toString())
        ?.build().toString()

    val body: RequestBody = RequestBody.create("media/type".toMediaTypeOrNull(), file)

    val requestForGetGroups: Request =
        Request.Builder()
            .url(urlChapterGet)
            .post(body)
            .header("Authorization", "Bearer " + user.getUserToken())
            .build()


    var chapterModelAfterInsertion : EntitiesProto.ChapterModel? = null

    val countDownLatch = CountDownLatch(1);
    okHttpClient.newCall(requestForGetGroups).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Error while setChapterText", e.toString() + " " + e.message)
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
                chapterModelAfterInsertion = EntitiesProto.ChapterModel.parseFrom(responseBody?.toByteArray())
            } else {
                if (writeErrorMessage) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            activity,
                            "Check connection, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            countDownLatch.countDown()
        }
    })

    countDownLatch.await(10, TimeUnit.SECONDS)
    return chapterModelAfterInsertion
}

fun addTestToUser(
    chapter: EntitiesProto.ChapterModel,
    test: TestModel,
    rightAns : Int,
    questNum : Int,
    writeErrorMessage: Boolean,
    activity: Activity,
    okHttpClient: OkHttpClient
) : EntitiesProto.UserModel? {
    val urlChapterGet : String = ("http://" + ContextCompat.getString(activity, R.string.IP) + "/users/userPassedTest").toHttpUrlOrNull()
        ?.newBuilder()
        ?.addQueryParameter("userId", user.getUserModel()?.id.toString())
        ?.addQueryParameter("testId", test.id.toString())
        ?.addQueryParameter("chapterId", chapter.id.toString())
        ?.addQueryParameter("testName", test.name)
        ?.addQueryParameter("chapterName", chapter.name)
        ?.addQueryParameter("rightAns", rightAns.toString())
        ?.addQueryParameter("questNum", questNum.toString())
        ?.build().toString()

    val requestForGetGroups: Request =
        Request.Builder()
            .url(urlChapterGet)
            .header("Authorization", "Bearer " + user.getUserToken())
            .build()


    var getUserModel : EntitiesProto.UserModel? = null

    val countDownLatch = CountDownLatch(1);
    okHttpClient.newCall(requestForGetGroups).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Error while addTestToUser", e.toString() + " " + e.message)
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
                getUserModel = EntitiesProto.UserModel.parseFrom(responseBody?.toByteArray())
            } else {
                if (writeErrorMessage) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            activity,
                            "Check connection, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            countDownLatch.countDown()
        }
    })

    countDownLatch.await(10, TimeUnit.SECONDS)
    return getUserModel
}