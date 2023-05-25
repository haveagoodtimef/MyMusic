package com.fhz.music_home.di

import android.content.Context
import com.fhz.music_home.dao.SongDao
import com.fhz.music_home.utils.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 时间:2023/3/28
 * @author Mr.Feng
 * 简述: 创建数据库注入类
 */
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule  {

    @Singleton
    @Provides
    fun getDataBase(@ApplicationContext context: Context) : AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun providesSongDao(appDatabase: AppDatabase) : SongDao{
       return  appDatabase.songDao()
    }

}