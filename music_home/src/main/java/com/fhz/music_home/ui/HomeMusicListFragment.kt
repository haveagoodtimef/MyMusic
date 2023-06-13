package com.fhz.music_home.ui

import android.content.ComponentName
import android.media.session.PlaybackState
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fhz.music_home.R
import com.fhz.music_home.adapter.MyMusicItemRecyclerViewAdapter
import com.fhz.music_home.databinding.FragmentHomeMusicListListBinding
import com.fhz.music_home.intent.HomeMusicIntent
import com.fhz.music_home.ui.state.HomeMusicUIState
import com.fhz.music_home.viewmodel.HomeMusicListViewModel
import com.fhz.music_lib_player.service.MyMediaPlaybackService
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val TAG = "HomeMusicListFragment"
@AndroidEntryPoint
/**
 * A fragment representing a list of Items.
 */
class HomeMusicListFragment : Fragment() {

    private val homeMusicViewModel: HomeMusicListViewModel by viewModels()
    private lateinit var binding:FragmentHomeMusicListListBinding
    private lateinit var mAdapter: MyMusicItemRecyclerViewAdapter
    private lateinit var mediaBrowser: MediaBrowserCompat
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var mediaMetadataItemList :ArrayList<MediaMetadataCompat>

    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: ")
        mediaBrowser = MediaBrowserCompat(
            context,
            context?.let { ComponentName(it, MyMediaPlaybackService::class.java) },
            browserConnectionCallback,
            null
        ).apply { connect() }
    }
    /**
     * MediaBrowser,链接后的回调
     */
    private var browserConnectionCallback: MediaBrowserCompat.ConnectionCallback = (object:
        MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            //链接成功自动回调
            Log.i(TAG, "onConnected: ")

            //必须在确保连接成功的前提下执行订阅的操作
            if (mediaBrowser.isConnected) {
                /**
                 * mediaId即为MediaBrowserService.onGetRoot的返回值
                 * 若Service允许客户端连接，则返回结果不为null，其值为数据内容层次结构的根ID
                 * 若拒绝连接，则返回null
                 */
                val mediaId = mediaBrowser.root

                /**
                 * Browser通过订阅的方式向Service请求数据，发起订阅请求需要两个参数，其一为mediaId
                 * 而如果该mediaId已经被其他Browser实例订阅，则需要在订阅之前取消mediaId的订阅者
                 * 虽然订阅一个 已被订阅的mediaId 时会取代原Browser的订阅回调，但却无法触发onChildrenLoaded回调
                 */
                mediaBrowser.unsubscribe(mediaId)

                /**
                 * 之前说到订阅的方法还需要一个参数，即设置订阅回调SubscriptionCallback
                 * 当Service获取数据后会将数据发送回来，此时会触发SubscriptionCallback.onChildrenLoaded回调
                 */
                mediaBrowser.subscribe(mediaId, browserSubscriptionCallback)
                mediaBrowser.getItem(mediaId,itemCallback)

                /**
                 * 到Controller类
                 */
                mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
                mediaController.registerCallback(controllerCallback())
            }
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            Log.i(TAG, "onConnectionSuspended: ")
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            Log.i(TAG, "onConnectionFailed: ")
        }
    })

    /**
     * 媒体浏览器订阅以后的回调
     * 也就是服务端返回数据的地方
     * 服务器返回的数据是MediaBrowser.MediaItem
     * 需要将MediaBrowser.MediaItem 转化成bean 放入到适配中.
     */
    private val browserSubscriptionCallback: MediaBrowserCompat.SubscriptionCallback = (object :
        MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)
            //children 即为Service的onLoadChildren发送回来的媒体数据集合
            for (media:MediaBrowserCompat.MediaItem in children){
                Log.i(TAG, "onChildrenLoaded:的id ${media.mediaId}")
                val metadata = MediaMetadataCompat.Builder().apply {
                    media.mediaId?.let { putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, it) }
                    putString(MediaMetadataCompat.METADATA_KEY_TITLE,media.description.title.toString())
                    putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,media.description.subtitle.toString())
                }.build()
                mediaMetadataItemList.add(metadata)
//                adapter.submitList(it.result.data)
            }
            mAdapter.notifyDataSetChanged()
        }
    })


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView: ")
        binding = FragmentHomeMusicListListBinding.inflate(inflater,container,false)
        //设置状态栏
        setBar()
        //设置适配器
        setRecycleViewAdapter()
        //处理流事件
        dispatchUIState()
        /**
         * 1,先从sd中读取所有数据
         * 2,把数据全部放入到播放列表里面
         * 3,点击播放全部.从头开始
         * 4,点击列表,从列表处开始播放
         *
         */
        lifecycleScope.launch{
            homeMusicViewModel.channel.send(HomeMusicIntent.GetMusicListFormSD)
        }
        //播放全部
        playAll()
        //点击暂停
        playAndPause()
        //下一首
        playNext()
        return binding.root
    }

    //播放指定位置的歌曲
    private fun playAsPosition(position:Int) {

    }

    //下一首
    private fun playNext() {
        binding.playNext.setOnClickListener{
            mediaController.transportControls.skipToNext()
        }
    }

    //播放和暂停
    private fun playAndPause() {
        binding.navBarPlayAndPause.setOnClickListener{
            if(isPlaying){
                mediaController.transportControls.pause()
            }else{
                mediaController.transportControls.play()
            }
        }
    }

    //播放全部按钮
    private fun playAll() {
        binding.playAll.setOnClickListener {
            mediaController.transportControls.play()
        }
    }


    /**
     * 设置适配器
     */
    private fun setRecycleViewAdapter() {
        val linearLayoutManager = LinearLayoutManager(context)
        mediaMetadataItemList = ArrayList()
        mAdapter = MyMusicItemRecyclerViewAdapter( { v -> itemClick(v)},mediaMetadataItemList)
        binding.musicRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.musicRecyclerView.layoutManager = linearLayoutManager
        binding.musicRecyclerView.adapter = mAdapter
    }

    //适配器列表的点击事件
    private fun itemClick(item:MediaMetadataCompat){
        println(item.description.title)
        println(item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
        mediaController.transportControls.skipToQueueItem(item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).toLong()-1)
//        item.mediaId?.let { mediaController.transportControls.skipToQueueItem(it.toLong()-1) }
//        lifecycleScope.launch{
//            homeMusicViewModel.channel.send(HomeMusicIntent.PlayForUri(item))
//        }
    }

    private fun dispatchUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeMusicViewModel.state.collect {
                    when (it) {
                        is HomeMusicUIState.Success -> {
                            println(it.result.data)
                        }
                        is HomeMusicUIState.Fail -> {
                            println(it.result.data)
                            println("失败")
//                            adapter.submitList(it.result.data)
                        }
                        is HomeMusicUIState.Play -> {
                            //更新ui
                            mediaController.transportControls.play()

                        }
                        is HomeMusicUIState.PlayFormUri -> {
                            mediaController.transportControls.playFromUri(it.uri,null)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    /**
     * 设置状态栏
     */
    private fun setBar() {
        ImmersionBar.with(this)
            .transparentBar()
            .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
            .init()
    }

    /**
     * 每一个媒体信息
     */
    private val itemCallback = object : MediaBrowserCompat.ItemCallback(){

        override fun onItemLoaded(item: MediaBrowserCompat.MediaItem?) {
            super.onItemLoaded(item)
            Log.i(TAG, "onItemLoaded: ${item?.description}")
        }
        override fun onError(mediaId: String) {
            super.onError(mediaId)
            Log.i(TAG, "onError: $mediaId")
        }
    }

    /**
     * 媒体控制器控制播放过程中的回调接口，可以用来根据播放状态更新UI
     */
    fun controllerCallback() = (object : MediaControllerCompat.Callback() {

        /**
         * 音乐播放状态改变的回调
         * @param state
         */
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            Log.i(TAG, "onPlaybackStateChanged: ")
            when (state?.state){
                //初始状态
                PlaybackState.STATE_NONE -> {
                    //处理ui
                    Log.d(TAG, "onPlaybackStateChanged: STATE_NONE")
                    binding.navBarPlayAndPause.setImageResource(R.mipmap.nav_bar_play_pause)
                }
                PlaybackState.STATE_PAUSED -> {
//                    btnPlay.setText("开始");
                    Log.d(TAG, "onPlaybackStateChanged: STATE_PAUSED")
                    binding.navBarPlayAndPause.setImageResource(R.mipmap.nav_bar_play)
                    isPlaying = false
                }
//
                PlaybackState.STATE_PLAYING -> {
                    Log.d(TAG, "onPlaybackStateChanged: STATE_PLAYING")
                    binding.navBarPlayAndPause.setImageResource(R.mipmap.nav_bar_play_pause)
                    isPlaying = true
                }
            }
        }

        /**
         * 播放音乐改变的回调
         * @param metadata
         */
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            Log.i(TAG, "onMetadataChanged: ${metadata?.description}")
            metadata?.let {
                val title = it.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                if(!title.isNullOrEmpty()){
                    binding.navaBarTitle.text = title?.substring(0,title.lastIndexOf("."))
                }
                if(it.getString(MediaMetadataCompat.METADATA_KEY_AUTHOR) == "<unknown>"){
                    binding.navaBarAuthor.text = "未知"
                }else{
                    binding.navaBarAuthor.text = it.getString(MediaMetadataCompat.METADATA_KEY_AUTHOR)
                }
                val options: RequestOptions = RequestOptions()
                    .placeholder(R.mipmap.album_image)
                    .error(R.mipmap.album_image)
                context?.let {
                        it1 -> Glide.with(it1)
                    .load(it.getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
                    .apply(options)
                    .into(binding.navBarAlbumImg) }

            }
        }

        //  //音频信息，音量
        override fun onAudioInfoChanged(info: MediaControllerCompat.PlaybackInfo?) {
            super.onAudioInfoChanged(info)
            Log.i(TAG, "onAudioInfoChanged: ")
        }
    })
}