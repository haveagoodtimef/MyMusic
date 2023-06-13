package com.fhz.music_home.adapter

import android.media.MediaMetadata
import android.media.browse.MediaBrowser
import android.media.browse.MediaBrowser.MediaItem
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fhz.music_home.databinding.HomeMusicRvLayoutBinding
import com.fhz.music_lib_common.entity.Song


/**
 * homeMusicListFragment中的播放列表适配器
 */
private const val TAG = "MyMusicItemRecyclerView"

class MyMusicItemRecyclerViewAdapter(
    private val itemClickedListener: (MediaMetadataCompat) -> Unit,
    private val list: List<MediaMetadataCompat>,
) : RecyclerView.Adapter<MyMusicItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HomeMusicRvLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvHomeListTitle.text = item.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        holder.tvHomeListTitleContent.text = item.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE)
        holder.tvHomeListNum.text = position.plus(1).toString()
//        holder.tvHomeListTime.text = item.getString(MediaMetadataCompat.METADATA_KEY_DURATION)
        holder.item = item
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(
        binding: HomeMusicRvLayoutBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        val tvHomeListTitle: TextView = binding.tvHomeListTitle
        val tvHomeListTitleContent: TextView = binding.tvHomeListTitleContent
        val tvHomeListNum: TextView = binding.tvHomeListNum
        val tvHomeListTime: TextView = binding.tvHomeListTime

        var item: MediaMetadataCompat? = null

        init {
            binding.root.setOnClickListener {
                item?.let {
                    itemClickedListener(it)
                }
            }
        }
    }
}