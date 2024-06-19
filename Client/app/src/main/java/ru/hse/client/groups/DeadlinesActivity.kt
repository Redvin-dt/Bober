package ru.hse.client.groups

import android.os.Bundle
import ru.hse.client.databinding.ActivityDeadlinesBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.client.utility.user

class DeadlinesActivity: DrawerBaseActivity() {
    private lateinit var binding: ActivityDeadlinesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        user.setUserByLogin(this, user.getUserLogin())
        super.onCreate(savedInstanceState)
        binding = ActivityDeadlinesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Deadlines")
    }
}