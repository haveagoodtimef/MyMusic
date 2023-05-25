package com.fhz.music_home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.fhz.music_home.adapter.HomeMusicListAdapter
import com.fhz.music_home.databinding.ActivityMainBinding
import com.fhz.music_home.intent.HomeMusicIntent
import com.fhz.music_home.ui.state.HomeMusicUIState
import com.fhz.music_home.viewmodel.HomeMusicViewModel
import com.gyf.immersionbar.ImmersionBar
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val homeMusicViewModel:HomeMusicViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter:HomeMusicListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        //action_bar 悬浮
//        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        RxPermissions(this).request(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,)

        //状态栏设置
        ImmersionBar.with(this)
            .transparentBar()
            .init()

        adapter = HomeMusicListAdapter()
        binding.musicRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.musicRecyclerView.adapter = adapter

        adapter.setOnItemClickListener{ adapter, view, position ->
//            Tips.show("onItemClick $position")
            println(position)
        }

        lifecycleScope.launch{
            homeMusicViewModel.channel.send(HomeMusicIntent.GetMusicList(1,10))
        }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                homeMusicViewModel.state.collect{
                    when(it){
                        is HomeMusicUIState.Success -> {
                            println(it.result.data)
                            adapter.submitList(it.result.data)
                        }
                        is HomeMusicUIState.Fail -> {
                            println(it.result.data)
                            println("失败")
                            adapter.submitList(it.result.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}