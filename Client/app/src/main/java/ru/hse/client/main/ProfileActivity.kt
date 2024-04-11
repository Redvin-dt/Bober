package ru.hse.client.main

import ru.hse.client.R
import android.os.Bundle
import ru.hse.client.databinding.ActivityBaseBinding
import ru.hse.client.databinding.ActivityProfileBinding

class ProfileActivity: DrawerBaseActivity() {

    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Profile")
    }
}