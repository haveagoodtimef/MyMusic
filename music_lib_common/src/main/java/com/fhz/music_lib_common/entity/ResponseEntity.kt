package com.fhz.mvitest.entity

/**
 * 时间:2023/5/19
 * @author Mr.Feng
 * 简述: 服务器返回的封装类
 */
data class ResponseEntity<T>(val errorCode:Int,val data:T,val errorMsg:String)
