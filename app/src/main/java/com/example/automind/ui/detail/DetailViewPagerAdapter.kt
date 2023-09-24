package com.example.automind.ui.detail

import androidx.fragment.app.Fragment
import com.example.automind.ui.detail.list.ListFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.automind.ui.detail.mindmap.MindMapFragment
import com.example.automind.ui.detail.original.OriginalFragment
import com.example.automind.ui.detail.summary.SummaryFragment

class DetailViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // Return the total count of fragments
    override fun getItemCount(): Int = 4 // 4 Fragments: "Original", "Summary", "List", "Mindmap"

    // Create and return a new fragment for the specified position
    override fun createFragment(position: Int): Fragment {
        return when (position % 4) {
            0 -> OriginalFragment()
            1 -> SummaryFragment()
            2 -> ListFragment()
            3 -> MindMapFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}
