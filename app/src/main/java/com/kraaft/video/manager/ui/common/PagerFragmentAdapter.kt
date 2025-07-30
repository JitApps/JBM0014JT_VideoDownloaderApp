package com.kraaft.driver.manager.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private var arrayList: ArrayList<Fragment> = ArrayList()
    private var mFragmentTitleList: ArrayList<String> = ArrayList()

    fun addFragment(fragment: Fragment, title: String) {
        arrayList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun createFragment(position: Int): Fragment {
        return arrayList[position]
    }
}