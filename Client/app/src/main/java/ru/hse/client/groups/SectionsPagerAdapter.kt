package ru.hse.client.groups

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ru.hse.client.R

class SectionsPagerAdapter(context: Context, fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    private var tabTitles: ArrayList<Int> = listOf(R.string.chapters1, R.string.members) as ArrayList<Int>
    private lateinit var mContext: Context

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItem(position: Int): Fragment {
        TODO("Not yet implemented")
    }

}
