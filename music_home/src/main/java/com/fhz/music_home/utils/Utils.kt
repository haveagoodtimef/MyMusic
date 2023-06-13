package com.fhz.music_home.utils

import android.content.Context
import android.widget.Toast

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

/**
 * toast的两个封装方法
 */
fun Context.toast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, content, duration).apply {
        show()
    }
}
fun Any.toast(context: Context, content: String, duration: Int = Toast.LENGTH_SHORT) {
    context.toast(content, duration)
}