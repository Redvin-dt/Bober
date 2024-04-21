package ru.hse.client.profile

import android.os.Bundle
import ru.hse.client.databinding.ActivityProfileBinding
import ru.hse.client.utility.DrawerBaseActivity

class ProfileActivity: DrawerBaseActivity() {

    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Profile")
    }
}