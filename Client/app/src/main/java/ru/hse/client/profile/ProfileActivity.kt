package ru.hse.client.profile

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import ru.hse.client.auth.SingletonController
import ru.hse.client.databinding.ActivityProfileBinding
import ru.hse.client.groups.GroupActivity
import ru.hse.client.groups.GroupSelectMenuActivity
import ru.hse.client.groups.InvitesActivity
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.client.utility.user

class ProfileActivity: DrawerBaseActivity() {

    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        user.setUserByLogin(this, user.getUserLogin())
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

        binding.testsButton.setOnClickListener {
            user.setUserByLogin(this, user.getUserLogin())
            val intent = Intent(this, PassedTestActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            ContextCompat.startActivity(this, intent, null)
        }

        binding.inviteButton.setOnClickListener {
            user.setUserByLogin(this, user.getUserLogin())
            goToInvites()
        }

        binding.logOutButton.setOnClickListener {
            user.setUserByLogin(this, user.getUserLogin())
            val authManager = SingletonController.getInstance().getManager()
            authManager.logOut()
        }
    }

    private fun goToInvites() {
        val intent = Intent(this, InvitesActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

}