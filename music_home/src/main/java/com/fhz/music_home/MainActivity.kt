package com.fhz.music_home

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.fhz.music_home.adapter.HomeViewPagerAdapter
import com.fhz.music_home.databinding.ActivityMainBinding
import com.fhz.music_home.ui.HomeMusicListFragment
import com.fhz.music_home.ui.state.HomeMusicUIState
import com.fhz.music_home.utils.toast
import com.gyf.immersionbar.ImmersionBar
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: HomeViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //权限
        requestPermissions()
       // 状态栏设置
        setBar()
        //设置viewpager
        setViewPagerAdapter()
    }

    private fun setViewPagerAdapter() {
        val list = mutableListOf<Fragment>()
        list.add(HomeMusicListFragment())
        viewPagerAdapter = HomeViewPagerAdapter(list,supportFragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        binding.homeViewPager.adapter = viewPagerAdapter

    }

    private fun requestPermissions() {
        RxPermissions(this).request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE
        ).forEach {
            if (!it) {
                toast("必须要同意哦!")
            }
        }
    }

    private fun setBar() {
        ImmersionBar.with(this)
            .transparentBar()
            .init()
    }
}