package com.fhz.music_home.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 时间:2023/5/23
 * @author Mr.Feng
 * 简述: 歌曲类
 */
@Entity
data class Song(
    val id:Int,
    val title:String,
    val displayName:String,
    val artist:String,
    val album:String,
    val path:String,
    val duration:Int,
    val size:Int,
    val favorite:Boolean,
    @PrimaryKey(autoGenerate = true)
    val db_id:Int
)
