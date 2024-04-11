package ru.hse.client.main

import ru.hse.client.R
import android.os.Bundle
import ru.hse.client.databinding.ActivityDeadlinesBinding
import ru.hse.client.databinding.ActivityGroupsBinding

class GroupsActivity: DrawerBaseActivity() {

    lateinit var binding: ActivityGroupsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Groups")
    }
}