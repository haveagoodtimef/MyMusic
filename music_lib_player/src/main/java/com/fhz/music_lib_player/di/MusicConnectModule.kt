package com.fhz.music_lib_player.di

import android.content.ComponentName
import android.content.Context
import com.fhz.music_lib_player.connect.MusicServiceConnection
import com.fhz.music_lib_player.service.MyMediaPlaybackService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Deprecated("不用了")
/**
 * 时间:2023/3/28
 * @author Mr.Feng
 * 简述: 创建数据库注入类
 */
@InstallIn(SingletonComponent::class)
@Module
class MusicConnectModule  {

    @Singleton
    @Provides
    fun getMusicConnect(@ApplicationContext context: Context) : MusicServiceConnection {
        return MusicServiceConnection(context, ComponentName(context,MyMediaPlaybackService::class.java))
    }



}