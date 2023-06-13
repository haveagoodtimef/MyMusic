package com.fhz.music_home.repository

import android.content.Context
import com.fhz.music_lib_common.entity.Song
import com.fhz.mvitest.entity.ResponseEntity
import com.fhz.mvvm.dataSource.local.HomeMusicLocalDataSource
import com.fhz.mvvm.dataSource.local.HomeMusicSdCardDataSource
import com.fhz.mvvm.dataSource.retrofit.HomeMusicListRemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * 时间:2023/5/25
 * @author Mr.Feng
 * 简述: 首页音乐仓库层
 * 对外提供数据,封装了数据的来源
 */
class HomeMusicRespostory @Inject constructor (
    private val homeMusicLocalDataSource: HomeMusicLocalDataSource,
    private val homeMusicRemoteDataSource:HomeMusicListRemoteDataSource,
    private val homeMusicSdCardDataSource: HomeMusicSdCardDataSource,
    ){

    suspend fun getMusicAll() : ResponseEntity<List<Song>> {
        return homeMusicRemoteDataSource.getMusicAll()
    }



    suspend fun getMusicFromSdCard() : List<Song> {
        return homeMusicSdCardDataSource.getMusicFromSD()
    }

}