package com.fhz.music_lib_common.base

import androidx.lifecycle.ViewModel

/**
 * 时间:2023/5/23
 * @author Mr.Feng
 * 简述: 封装通用的viewmodel
 */
abstract class BaseViewModel():ViewModel(){
    init {
        handlerIntent()
    }
    abstract fun handlerIntent()
}