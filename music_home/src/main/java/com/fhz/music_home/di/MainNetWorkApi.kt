package com.fhz.module_main.di

import com.fhz.lib_network.provides.FengRetrofit
import com.fhz.lib_network.provides.YaoRetrofit
import com.fhz.module_main.api.FengServerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 时间:2023/4/25
 * @author Mr.Feng
 * 简述: main 里面的api
 */
@InstallIn(SingletonComponent::class)
@Module
class MainNetWorkApi {

    @Singleton
    @Provides
    fun providesFengService(@FengRetrofit retrofit : Retrofit): FengServerApi {
        return retrofit.create(FengServerApi::class.java)
    }

//    @Singleton
//    @Provides
//    fun providesKaiYanService(@KaiYanRetrofit retrofit : Retrofit): KaiYanServerApi {
//        return retrofit.create(KaiYanServerApi::class.java)
//    }

//    @Singleton
//    @Provides
//    fun providesKaiService(retrofit : Retrofit): KaiServiceApi {
//        return retrofit.create(YaoServiceApi::class.java)
//    }
}