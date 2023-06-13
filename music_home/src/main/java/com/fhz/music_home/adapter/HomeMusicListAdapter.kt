package com.fhz.music_home.adapter

import android.content.Context
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.fhz.music_home.databinding.HomeMusicRvLayoutBinding
import com.fhz.music_home.utils.format
import com.fhz.music_lib_common.entity.Song

/**
 * 时间:2023/5/23
 * @author Mr.Feng
 * 简述: Home页面的适配类
 */
class HomeMusicListAdapter : BaseQuickAdapter<MediaItem, HomeMusicListAdapter.VH>() {
    // 自定义ViewHolder类
    class VH(
        parent: ViewGroup,
        val binding:HomeMusicRvLayoutBinding = HomeMusicRvLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        // 返回一个 ViewHolder
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: MediaItem?) {
        // 设置item数据
        item?.let {
            holder.binding.tvHomeListTitle.text = it.description.title
            holder.binding.tvHomeListTitleContent.text = it.description.subtitle
//            holder.binding.tvHomeListTime.text = format(it.duration)
        }
    }
}