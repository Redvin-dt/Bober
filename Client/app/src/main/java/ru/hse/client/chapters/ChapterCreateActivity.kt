package ru.hse.client.chapters

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.text.TextUtils.isEmpty
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import okio.ByteString
import okio.source
import ru.hse.client.R
import ru.hse.client.databinding.ActivityCreateChapterBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.client.utility.user
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.ChapterModel
import ru.hse.server.proto.EntitiesProto.GroupModel
import ru.hse.server.proto.EntitiesProto.TestList
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class ChapterCreateActivity : DrawerBaseActivity() {
    private lateinit var binding: ActivityCreateChapterBinding
    private lateinit var pageAdapter: ChapterCreatePageAdapter
    private lateinit var group: GroupModel
    private lateinit var text: String
    private lateinit var filepath: Uri
    private var testStartPositions: MutableList<Int> = ArrayList()
    private var okHttpClient = OkHttpClient()
    private lateinit var testListBuilder: TestList.Builder
    private lateinit var chapterTestsCreationFragmentRef:  AtomicReference<ChapterTestsCreationFragment?>
    private lateinit var chapterSettingsFragmentRef:  AtomicReference<ChapterSettingsFragment?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateChapterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val groupInBytes = intent!!.extras!!.get("group info") as ByteArray
        group = GroupModel.parseFrom(groupInBytes)
        testListBuilder = TestList.newBuilder()

        chapterTestsCreationFragmentRef = AtomicReference<ChapterTestsCreationFragment?>(null)
        chapterSettingsFragmentRef = AtomicReference<ChapterSettingsFragment?>(null)
        pageAdapter = ChapterCreatePageAdapter(supportFragmentManager, lifecycle, this, testStartPositions, testListBuilder, chapterTestsCreationFragmentRef, chapterSettingsFragmentRef)

        binding.viewPager.adapter = pageAdapter
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })

        binding.chooseFileButton.setOnClickListener {
            Log.d(this.localClassName, "chooseFileButton pressed")
            testStartPositions.clear()
            startFileChooser()
        }

        binding.uploadFileButton.setOnClickListener {
            if (chapterTestsCreationFragmentRef.get()!!.isTextEmpty()) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this,
                        "To begin, select a file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (isEmpty(binding.chapterName.text)) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this,
                        "Name the chapter",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Log.d(this.localClassName, "uploadFileButton pressed")
                var chapterModel = EntitiesProto.ChapterModel.newBuilder()
                    .setName(binding.chapterName.text.toString())
                    .setTextFile(text)
                    .setTests(testListBuilder)
                    .setGroup(group)
                    .setDeadlineTs(chapterSettingsFragmentRef.get()?.getDeadline() ?: -1)
                    .build()

                val newChapter = sendChapterToServer(chapterModel)
                if (newChapter != null) {
                    chapterModel = newChapter
                    setChapterText(chapterModel, text, false, this@ChapterCreateActivity, okHttpClient)
                }
                val data: Intent = Intent()
                this.setResult(AppCompatActivity.RESULT_OK, data)
                this.finish()
            }
        }

    }



    fun setChapterText(
        chapter: EntitiesProto.ChapterModel,
        text: String,
        writeErrorMessage: Boolean,
        activity: Activity,
        okHttpClient: OkHttpClient
    ) : EntitiesProto.ChapterModel? {
        val file = File.createTempFile("file_to_send", chapter.id.toString() + ".txt")
        file.writeText(text, StandardCharsets.UTF_8)

        val urlChapterGet : String = ("http://" + ContextCompat.getString(activity, R.string.IP) + "/files").toHttpUrlOrNull()
            ?.newBuilder()
            ?.addQueryParameter("chapterId", chapter.id.toString())
            ?.build().toString()

        class InputStreamRequestBody(
            private val contentType: MediaType,
            private val contentResolver: ContentResolver,
            private val uri: Uri
        ) : RequestBody() {
            override fun contentType() = contentType

            override fun contentLength(): Long = -1

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                val input = contentResolver.openInputStream(uri)

                input?.use { sink.writeAll(it.source()) }
                    ?: throw IOException("Could not open $uri")
            }
        }
        val contentResolver = applicationContext.getContentResolver()
        val contentPart = InputStreamRequestBody("multipart/form-data".toMediaType(), contentResolver, Uri.fromFile(file))
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("file", "file.txt", contentPart)
            .build()
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

    private fun sendChapterToServer(chapterModel: ChapterModel) : EntitiesProto.ChapterModel? {
        val URlGetUser: String =
            ("http://" + ContextCompat.getString(this, R.string.IP) + "/chapters").toHttpUrlOrNull()
                ?.newBuilder()
                ?.build()
                .toString()

        val requestBody: RequestBody =
            RequestBody.create("application/x-protobuf".toMediaTypeOrNull(), chapterModel.toByteArray())

        val requestForAddChapter: Request = Request.Builder()
            .url(URlGetUser)
            .header("Authorization", "Bearer ${user.getUserToken()}")
            .post(requestBody)
            .build()

        var chapterModel : EntitiesProto.ChapterModel? = null

        val okHttpClient = OkHttpClient()
        val countDownLatch = CountDownLatch(1)
        okHttpClient.newCall(requestForAddChapter).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(this@ChapterCreateActivity.toString(), e.toString() + " " + e.message)
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this@ChapterCreateActivity,
                        "Something went wrong, try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i(this@ChapterCreateActivity.toString(), response.toString())
                if (response.isSuccessful) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this@ChapterCreateActivity,
                            "The chapter has been added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val responseBody: ByteString? = response.body?.byteString()
                    chapterModel = EntitiesProto.ChapterModel.parseFrom(responseBody?.toByteArray())
                } else {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this@ChapterCreateActivity,
                            "Something went wrong, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                countDownLatch.countDown()
            }
        })
        countDownLatch.await()
        return chapterModel
    }


    private fun startFileChooser() {
        val intent = Intent()
        intent.setType("text/plain").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Choose file"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            filepath = data.data!!
            val istream = contentResolver.openInputStream(filepath!!)
            text = istream!!.bufferedReader().use { it.readText() }
            istream.close()
            chapterTestsCreationFragmentRef.get()!!.setText(text)
        }
    }
}