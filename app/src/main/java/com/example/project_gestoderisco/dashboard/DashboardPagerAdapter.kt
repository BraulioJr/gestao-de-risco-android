package com.example.project_gestoderisco.dashboard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project_gestoderisco.dashboard.tabs.DashboardChartsFragment
import com.example.project_gestoderisco.dashboard.tabs.DashboardRankingFragment

class DashboardPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DashboardChartsFragment()
            1 -> DashboardRankingFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}