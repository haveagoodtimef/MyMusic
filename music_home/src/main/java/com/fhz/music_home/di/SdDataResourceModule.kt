package com.fhz.music_home.di

import android.content.Context
import com.fhz.mvvm.dataSource.local.HomeMusicSdCardDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 时间:2023/6/5
 * @author Mr.Feng
 * 简述: 提供一个全局的sd操作
 */
@InstallIn(SingletonComponent::class)
@Module
class SdDataResourceModule {

    @Singleton
    @Provides
    fun getSdDataSource(@ApplicationContext context: Context) : HomeMusicSdCardDataSource {
        return HomeMusicSdCardDataSource(context)
    }
}