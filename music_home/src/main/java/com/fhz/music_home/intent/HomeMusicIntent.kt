package com.fhz.music_home.intent

/**
 * 时间:2023/5/23
 * @author Mr.Feng
 * 简述: Home界面上Music的意图
 */
sealed class HomeMusicIntent {
    data class GetMusicList(var page:Int,var size:Int): HomeMusicIntent()
}