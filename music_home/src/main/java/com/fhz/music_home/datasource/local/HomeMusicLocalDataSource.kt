package com.fhz.mvvm.dataSource.local

import com.fhz.music_home.dao.SongDao
import com.fhz.music_home.entity.Song
import javax.inject.Inject

/**
 * 时间:2023/3/27
 * @author Mr.Feng
 * 简述: 本地数据操作数据库
 */
class HomeMusicLocalDataSource @Inject constructor(val songDao: SongDao)  {

    //存入到本地,返回当前的uid
     suspend fun insertUser(song: Song) {
        songDao.insertUser(song)
    }

    //从本地查找-> 可以判断是不是为空
//    fun findUserByName(name:String) = userDao.findUserByName(name)

}