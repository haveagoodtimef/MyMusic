package com.fhz.music_home.repository

import com.fhz.music_home.entity.Song
import com.fhz.mvitest.entity.ResponseEntity
import com.fhz.mvvm.dataSource.local.HomeMusicLocalDataSource
import com.fhz.mvvm.dataSource.retrofit.HomeMusicListRemoteDataSource
import javax.inject.Inject

/**
 * 时间:2023/5/25
 * @author Mr.Feng
 * 简述: 首页音乐仓库层
 * 对外提供数据,封装了数据的来源
 */
class HomeMusicRespostory @Inject constructor (
    private val homeMusicLocalDataSource: HomeMusicLocalDataSource,
    private val homeMusicRemoteDataSource:HomeMusicListRemoteDataSource
    ){

    suspend fun getMusicAll() : ResponseEntity<List<Song>> {
        return homeMusicRemoteDataSource.getMusicAll()
    }



}