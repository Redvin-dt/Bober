package ru.hse.client.profile

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import ru.hse.client.auth.SingletonController
import ru.hse.client.databinding.ActivityProfileBinding
import ru.hse.client.groups.GroupSelectMenuActivity
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.client.utility.user

class ProfileActivity: DrawerBaseActivity() {

    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Profile")

        binding.login.text = user.getUserLogin()
        binding.email.text = user.getUserEmail()
        binding.metaInfo.text = user.getUserMetaInfo()

        binding.statisticButton.setOnClickListener {
            // TODO: set function for statistic
        }

        binding.groupsButton.setOnClickListener {
            goToGroupSelect()
        }

        binding.inviteButton.setOnClickListener {
            // TODO: set invites
        }

        binding.logOutButton.setOnClickListener {
            val authManager = SingletonController.getInstance().getManager()
            authManager.logOut()
        }
    }

    fun goToGroupSelect() {
        val intent = Intent(this, GroupSelectMenuActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        ContextCompat.startActivity(this, intent, null)
    }
}