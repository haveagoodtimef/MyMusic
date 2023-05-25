package com.fhz.music_home.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import com.fhz.music_home.intent.HomeMusicIntent
import com.fhz.music_home.repository.HomeMusicRespostory
import com.fhz.music_home.ui.state.HomeMusicUIState
import com.fhz.music_lib_common.base.BaseViewModel
import com.fhz.mvvm.dataSource.retrofit.HomeMusicListRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 时间:2023/5/23
 * @author Mr.Feng
 * 简述: HomeMusic的viewModel
 */
@HiltViewModel
class HomeMusicViewModel @Inject constructor(
    private val repository:HomeMusicRespostory
    ) : BaseViewModel(){

    //通道
//    private val _tickFlow = MutableSharedFlow<Unit>(replay = 0)

    val channel = Channel<HomeMusicIntent>(Channel.UNLIMITED)
    //不让外部调用
    private val _state = MutableStateFlow<HomeMusicUIState>(HomeMusicUIState.Init)
    //使用flow来监听
    val state: StateFlow<HomeMusicUIState> = _state


    override fun handlerIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect{
                when(it){
                    is HomeMusicIntent.GetMusicList -> getMusicList(it)
                }
            }
        }
    }

    private fun getMusicList(it: HomeMusicIntent.GetMusicList) {
        viewModelScope.launch {
            val musicList = repository.getMusicAll()
            if(musicList.errorCode == 200){
                _state.value = HomeMusicUIState.Success(musicList)
            }else{
                _state.value = HomeMusicUIState.Fail(musicList)
            }
        }
    }
}