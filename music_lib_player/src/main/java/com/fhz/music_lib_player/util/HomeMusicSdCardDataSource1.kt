package com.fhz.mvvm.dataSource.local

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.fhz.music_lib_common.entity.Song

/**
 * 时间:2023/3/27
 * @author Mr.Feng
 * 简述: 本地数据操作sd卡
 */
class HomeMusicSdCardDataSource1  {

    private val TAG = "HomeMusicSdCardDataSour1"

    fun getMusicFromSD(context:Context) : List<Song>{
        //这个判断语句使我们常用的：判断手机是否插入了SD卡和是否有访问权限，如果都有就返回true
        var list =  ArrayList<Song>()
        var i = 0
        var cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        Log.i(TAG, "getMusicData: "+MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.path)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val displayname = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val album = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                var albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
                i++
                val song = Song(i,displayname,deleteFileType(title),artist,getAlbum(context,albumId),path,duration,size,false,0)
                if (size > 1000 * 800) {//过滤掉短音频
                    // 分离出歌曲名和歌手
//                    if (song.getSong().contains("-")) {
//                        String[] str = song.getSong().split("-");
//                        song.setSinger( str[0]);
//                        song.setSong( str[1]);
//                    }
                    list.add(song)
                }
            }
            // 释放资源
            cursor.close()
        }else{
            Log.i(TAG, "getMusicData: 没有")
        }
        return list
    }

    private fun deleteFileType(s:String) :String {
        if (s.isNotEmpty() && s.lastIndexOf(".") > 0){
            return s.substring(0,s.lastIndexOf("."))
        }
        return s
    }

    //取到专辑的图片
    fun getAlbum(context:Context,  id:Int) : String{
        val mUriAlbums = "content://media/external/audio/albums"
        val projection = mutableListOf("album_art")
        val contentResolver = context.contentResolver
        val query = contentResolver.query(Uri.parse("$mUriAlbums/$id"), projection.toTypedArray(), null, null, null)
        if (query != null) {
//            //过滤一下短音频
            if (query.count > 0 && query.columnCount > 0) {
                query.moveToNext()
                var string = query.getString(0)
//                Log.i(TAG, "getAlbum: $string")
                if(!string.isNullOrEmpty()){
                    return string
                }
            }
            query.close()
        }
        return ""
    }
}