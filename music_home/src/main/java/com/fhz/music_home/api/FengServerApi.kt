package com.fhz.module_main.api

import com.fhz.music_lib_common.entity.Song
import com.fhz.mvitest.entity.ResponseEntity
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 时间:2023/4/26
 * @author Mr.Feng
 * 简述: 我自己的后台接口
 */
interface FengServerApi {
    /**
     * 请求音乐列表
     */
    @GET("/music/list")
    suspend fun getMusicList(@Query("page") page:Int,@Query("size") size:Int) : ResponseEntity<List<Song>>

    /**
     * 获取所有音乐
     */
    @GET("/music/all")
    suspend fun getMusicAll() : ResponseEntity<List<Song>>
}