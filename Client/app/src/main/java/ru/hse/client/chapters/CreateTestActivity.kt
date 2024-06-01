package ru.hse.client.chapters

import android.os.Bundle
import ru.hse.client.databinding.ActivityCreateTestBinding
import ru.hse.client.databinding.ActivityUploadChapterBinding
import ru.hse.client.utility.DrawerBaseActivity

class CreateTestActivity : DrawerBaseActivity() {

    private lateinit var binding: ActivityCreateTestBinding
    private lateinit var startPositions: ArrayList<Int>
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTestBinding.inflate(layoutInflater)
        setContentView(binding.root)


        startPositions = intent.extras?.get("startPositions") as ArrayList<Int>
        binding.logo.text = "Test number ${currentPosition} of ${startPositions.size}"

    }


}