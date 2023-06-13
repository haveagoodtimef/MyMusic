package com.fhz.music_lib_player

import android.widget.Toast
import com.fhz.music_lib_player.aidl.IMyMusicServiceInterface


/**
 * 时间:2023/5/25
 * @author Mr.Feng
 * 简述: 返回的Binder接口
 */
@Deprecated("不用了,换成MediaBrowser")
class MyBinder : IMyMusicServiceInterface.Stub(){

    override fun openFile(path: String?) {
        TODO("Not yet implemented")
    }

    override fun open(list: LongArray?, position: Int, sourceId: Long, sourceType: Int) {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun play() {

    }

    override fun playAsPosition(position: Int) {

    }

    override fun prev(forcePrevious: Boolean) {
        TODO("Not yet implemented")
    }

    override fun next() {
        TODO("Not yet implemented")
    }

    override fun enqueue(list: LongArray?, action: Int, sourceId: Long, sourceType: Int) {
        TODO("Not yet implemented")
    }

    override fun setQueuePosition(index: Int) {
        TODO("Not yet implemented")
    }

    override fun setShuffleMode(shufflemode: Int) {
        TODO("Not yet implemented")
    }

    override fun setRepeatMode(repeatmode: Int) {
        TODO("Not yet implemented")
    }

    override fun moveQueueItem(from: Int, to: Int) {
        TODO("Not yet implemented")
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }

    override fun playlistChanged() {
        TODO("Not yet implemented")
    }

    override fun isPlaying(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getQueue(): LongArray {
        TODO("Not yet implemented")
    }

    override fun getQueueItemAtPosition(position: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getQueueSize(): Int {
        TODO("Not yet implemented")
    }

    override fun getQueuePosition(): Int {
        TODO("Not yet implemented")
    }

    override fun getQueueHistoryPosition(position: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getQueueHistorySize(): Int {
        TODO("Not yet implemented")
    }

    override fun getQueueHistoryList(): IntArray {
        TODO("Not yet implemented")
    }

    override fun duration(): Long {
        TODO("Not yet implemented")
    }

    override fun position(): Long {
        TODO("Not yet implemented")
    }

    override fun seek(pos: Long): Long {
        TODO("Not yet implemented")
    }

    override fun seekRelative(deltaInMs: Long) {
        TODO("Not yet implemented")
    }

    override fun getAudioId(): Long {
        TODO("Not yet implemented")
    }

    override fun getNextAudioId(): Long {
        TODO("Not yet implemented")
    }

    override fun getPreviousAudioId(): Long {
        TODO("Not yet implemented")
    }

    override fun getArtistId(): Long {
        TODO("Not yet implemented")
    }

    override fun getAlbumId(): Long {
        TODO("Not yet implemented")
    }

    override fun getArtistName(): String {
        TODO("Not yet implemented")
    }

    override fun getTrackName(): String {
        TODO("Not yet implemented")
    }

    override fun getAlbumName(): String {
        TODO("Not yet implemented")
    }

    override fun getPath(): String {
        TODO("Not yet implemented")
    }

    override fun getShuffleMode(): Int {
        TODO("Not yet implemented")
    }

    override fun removeTracks(first: Int, last: Int): Int {
        TODO("Not yet implemented")
    }

    override fun removeTrack(id: Long): Int {
        TODO("Not yet implemented")
    }

    override fun removeTrackAtPosition(id: Long, position: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getRepeatMode(): Int {
        TODO("Not yet implemented")
    }

    override fun getMediaMountedCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getAudioSessionId(): Int {
        TODO("Not yet implemented")
    }

    override fun setLockscreenAlbumArt(enabled: Boolean) {
        TODO("Not yet implemented")
    }
}