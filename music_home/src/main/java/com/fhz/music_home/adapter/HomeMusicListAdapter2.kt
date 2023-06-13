package com.fhz.music_home.adapter

import android.media.browse.MediaBrowser.MediaItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fhz.music_home.databinding.HomeMusicRvLayoutBinding

/**
 * 时间:2023/5/23
 * @author Mr.Feng
 * 简述: 原生适配器的用法
 */
class HomeMusicListAdapter2(var list:List<MediaItem>) : RecyclerView.Adapter<HomeMusicListAdapter2.VH>() {
    // 自定义ViewHolder类
    class VH(binding: HomeMusicRvLayoutBinding) : RecyclerView.ViewHolder(binding.root){

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding:HomeMusicRvLayoutBinding = HomeMusicRvLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val get = list[position]

//        holder.binding.tvHomeListTitle.text = get.title
//        holder.binding.tvHomeListTitleContent.text = it.displayName
//        holder.binding.tvHomeListTime.text = format(it.duration)

    }

    override fun getItemCount(): Int {
        return list.size
    }
}