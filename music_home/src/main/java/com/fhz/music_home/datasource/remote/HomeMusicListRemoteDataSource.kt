package com.fhz.mvvm.dataSource.retrofit

import com.fhz.module_main.api.FengServerApi
import com.fhz.music_lib_common.entity.Song
import com.fhz.mvitest.entity.ResponseEntity
import javax.inject.Inject

/**
 * 时间:2023/3/27
 * @author Mr.Feng
 * 简述: 远程请求,使用api
 */
class HomeMusicListRemoteDataSource @Inject constructor(private val serviceApi: FengServerApi) {
//    从服务器请数据
   suspend fun getMusicList(page:Int,size:Int) : ResponseEntity<List<Song>> {
       return serviceApi.getMusicList(page,size)
    }

    suspend fun getMusicAll() : ResponseEntity<List<Song>> {
        return serviceApi.getMusicAll()
    }

}