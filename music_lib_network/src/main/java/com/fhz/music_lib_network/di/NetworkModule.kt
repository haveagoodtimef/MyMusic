package com.fhz.lib_network.di

import com.fhz.lib_network.provides.FengRetrofit
import com.fhz.lib_network.provides.YaoRetrofit
import com.fhz.music_lib_network.Cons
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * 时间:2023/4/25
 * @author Mr.Feng
 * 简述: 使用hilt 提供网络依赖
 */
@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
//            .sslSocketFactory(new NetworkSSL(TrustManager.trustAllCert), TrustManager.trustAllCert)
//            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
//            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    @FengRetrofit
    fun  providesFengRetrofit(okHttpClient:OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Cons.FENG_HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

//    @Singleton
//    @Provides
//    @KaiYanRetrofit
//    fun  providesKaiYanRetrofit(okHttpClient:OkHttpClient ) : Retrofit {
//        return Retrofit.Builder()
//            .client(okHttpClient)
//            .baseUrl("http://yaoayao.com")
////            .addCallAdapterFactory(LiveDataCallAdapterFactory())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
}