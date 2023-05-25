package com.fhz.music_home.utils

/**
 * 时间:2023/5/23
 * @author Mr.Feng
 * 简述: 格式化时间的显示
 */
fun format(time:Int) : String{
    return if (time / 1000 % 60 < 10) {
        (time / 1000 / 60).toString()+ ":0" + time / 1000 % 60  //3:09
    } else {
        (time / 1000 / 60).toString()+ ":" + time / 1000 % 60  //3:11
    }
}
