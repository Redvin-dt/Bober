package ru.hse.client.Main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import ru.hse.client.R
import ru.hse.client.databinding.GroupCreateBinding
import ru.hse.client.databinding.GroupSelectMenuBinding

class GroupCreateActivity : AppCompatActivity() {

    private lateinit var binding: GroupCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupCreateBinding.inflate(layoutInflater)
        setContentView(R.layout.group_select_menu)

        setCreateButton()
    }

    private fun setCreateButton() {
        val button = binding.createGroupButton

        val context = this@GroupCreateActivity

        button.setOnClickListener {

            val URlRegistration: String =
                    ("http://" + ContextCompat.getString(context, R.string.IP) + "/groups/registration").toHttpUrlOrNull()
                            ?.newBuilder()
                            ?.build()
                            .toString()

            val intent = Intent(context, GroupSelectMenuActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }
    }
}