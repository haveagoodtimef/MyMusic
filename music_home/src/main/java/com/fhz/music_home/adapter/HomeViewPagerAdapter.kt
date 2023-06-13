package com.fhz.music_home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * 时间:2023/5/30
 * @author Mr.Feng
 * 简述: home也的viewpager 用来放fragment
 */
class HomeViewPagerAdapter(
    var list:List<Fragment>,
    fragmentManager: FragmentManager,
    behavior:Int) : FragmentPagerAdapter(fragmentManager,behavior) {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Fragment = list[position]
}