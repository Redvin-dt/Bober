package ru.hse.client

import android.R
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


open class BaseActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ru.hse.client.R.layout.base)

        drawerLayout = findViewById(ru.hse.client.R.id.drawer_layout)
        navigationView = findViewById(ru.hse.client.R.id.nav_view)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                ru.hse.client.R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
                ru.hse.client.R.id.nav_groups -> startActivity(Intent(this, GroupsActivity::class.java))
                ru.hse.client.R.id.nav_deadlines -> startActivity(Intent(this, DeadlinesActivity::class.java))
            }
            true
        }
    }
}