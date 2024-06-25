package ru.hse.client.utility

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import ru.hse.client.R
import ru.hse.client.auth.SingletonController
//import ru.hse.client.profile.DeadlinesActivity
import ru.hse.client.groups.GroupSelectMenuActivity
import ru.hse.client.profile.ProfileActivity

open class DrawerBaseActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun setContentView(view: View) {
        drawerLayout = layoutInflater.inflate(R.layout.activity_drawer_base, null) as DrawerLayout
        val container: FrameLayout = drawerLayout.findViewById(R.id.activity_container)
        container.addView(view)
        super.setContentView(drawerLayout)

        val toolbar: Toolbar = drawerLayout.findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)

        val navigationView: NavigationView = drawerLayout.findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)

        when (item.itemId) {
            R.id.nav_groups -> {
                val intent = Intent(this, GroupSelectMenuActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            R.id.nav_deadilnes -> {
               // val intent = Intent(this, DeadlinesActivity::class.java)
               // startActivity(intent)
               // overridePendingTransition(0, 0)
            }
            R.id.nav_log_out -> {
                val authManager = SingletonController.getInstance().getManager()
                authManager.logOut()
            }
        }

        return false
    }

    protected fun allocateActivityTitle(titleString: String) {
        supportActionBar?.title = titleString
    }
}