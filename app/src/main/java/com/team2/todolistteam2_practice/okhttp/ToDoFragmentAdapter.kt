package com.team2.todolistteam2_practice.okhttp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ToDoFragmentAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    private val fragments = arrayListOf<Fragment>()

    fun getFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount() = fragments.size

}