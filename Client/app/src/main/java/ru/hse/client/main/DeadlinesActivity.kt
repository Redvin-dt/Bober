package ru.hse.client.main

import ru.hse.client.R
import android.os.Bundle
import ru.hse.client.databinding.ActivityDeadlinesBinding
import ru.hse.client.databinding.ActivityProfileBinding

class DeadlinesActivity: DrawerBaseActivity() {
    lateinit var binding: ActivityDeadlinesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeadlinesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Deadlines")
    }
}