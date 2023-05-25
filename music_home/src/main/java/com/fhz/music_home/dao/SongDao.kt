package com.fhz.music_home.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fhz.music_home.entity.Song

/**
 * 时间:2023/5/25
 * @author Mr.Feng
 * 简述: 音乐的dao
 */
@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(song: Song):Long
//
//    @Query("select * from user where username = :name" )
//    fun findUserByName(name:String) : LiveData<User>
}