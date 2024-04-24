package ru.hse.client.groups

import android.os.Bundle
import ru.hse.client.databinding.ActivityDeadlinesBinding
import ru.hse.client.utility.DrawerBaseActivity

class DeadlinesActivity: DrawerBaseActivity() {
    lateinit var binding: ActivityDeadlinesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeadlinesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Deadlines")
    }
}