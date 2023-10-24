package com.fhz.music_home.viewmodel

import androidx.lifecycle.viewModelScope
import com.fhz.music_home.intent.HomeMusicIntent
import com.fhz.music_home.repository.HomeMusicRespostory
import com.fhz.music_home.ui.state.HomeMusicUIState
import com.fhz.music_lib_common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dalvik.system.DexClassLoader
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeMusicListViewModel"
/**
 * 时间:2023/5/23
 * @author Mr.Feng
 * 简述: HomeMusic的viewModel
 */
@HiltViewModel
class HomeMusicListViewModel @Inject constructor(
    private val repository: HomeMusicRespostory,
    ) : BaseViewModel(){
    lateinit var channel :Channel<HomeMusicIntent>
    //不让外部调用
    private val _state = MutableStateFlow<HomeMusicUIState>(HomeMusicUIState.Init)
    //使用flow来监听
    val state: StateFlow<HomeMusicUIState> = _state

    override fun handlerIntent() {
        channel = Channel<HomeMusicIntent>(Channel.UNLIMITED)
        viewModelScope.launch {
            channel.consumeAsFlow().collect{
                when(it){
                    is HomeMusicIntent.GetMusicList -> getMusicList(it)
                    is HomeMusicIntent.PlayMusicAsPosition -> playMusicAsPosition(it)
                    is HomeMusicIntent.PlayForUri -> playForUri(it)
                    is HomeMusicIntent.GetMusicListFormSD -> getMusicFromSdCard()
                    is HomeMusicIntent.PlayAll -> playAll(it)
                }
            }
        }
    }

    private fun playAll(it: HomeMusicIntent.PlayAll) {
        viewModelScope.launch {
        }
    }

    private fun update(){

    }


    private fun playForUri(it: HomeMusicIntent.PlayForUri) {
        println(it.item.description.mediaUri)
        _state.value = it.item.description.mediaUri?.let { it1 -> HomeMusicUIState.PlayFormUri(it1) }!!
//        musicServiceConnection.transportControls.playFromUri(it.item.description.mediaUri,null)
//        musicServiceConnection.transportControls.()
    }

    /**
     * 播发指定位置的音乐
     */
    private fun playMusicAsPosition(it: HomeMusicIntent.PlayMusicAsPosition) {
        _state.value = HomeMusicUIState.Play
    }


    /**
     * 得到所有网络音乐
     */
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

    /**
     * 得到sd中所有音乐
     */
    private fun getMusicFromSdCard(){
        viewModelScope.launch {
//            val musicFromSdCard = repository.getMusicFromSdCard()
//            _state.value = HomeMusicUIState.SdResource(musicServiceConnection.mediaItemList)
//            Log.i(TAG, "getMusicFromSdCard: 歌曲有 ${musicFromSdCard.size} 个")
        }
    }
}