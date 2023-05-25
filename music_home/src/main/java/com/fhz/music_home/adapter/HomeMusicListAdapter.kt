package com.fhz.music_home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.fhz.music_home.databinding.HomeMusicRvLayoutBinding
import com.fhz.music_home.entity.Song
import com.fhz.music_home.utils.format

/**
 * 时间:2023/5/23
 * @author Mr.Feng
 * 简述: Home页面的适配类
 */
class HomeMusicListAdapter : BaseQuickAdapter<Song, HomeMusicListAdapter.VH>() {
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

    override fun onBindViewHolder(holder: VH, position: Int, item: Song?) {
        // 设置item数据
        item?.let {
            holder.binding.tvHomeListTitle.text = it.title
            holder.binding.tvHomeListTitleContent.text = it.displayName
            holder.binding.tvHomeListTime.text = format(it.duration)
        }
    }
}