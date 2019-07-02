/******************************************************************************
 * Class : RecorderService.kt
 * Record with projection service
 * Just for demo project
 * Version : v0.1
 * Date : Jul 01, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import com.zen8labs.screenrecorder.activity.MainActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * * TODO For long term we should separate record service, This service is not complete yet
 */
class RecorderService : Service(), IRecordable {
    companion object {
        val TAG = "RecorderService"
        var mScreenDensity: Int = 0
        val RECORD_STATE_UNDEFINED = -1
        val RECORD_STATE_IDLE = 0
        val RECORD_STATE_RECORDING = 1
        val RECORD_STATE_PAUSE = 2
        val RECORD_STATE_STOP = 3
    }

    private var mRecordState = RECORD_STATE_IDLE

    private var mProjectionManager: MediaProjectionManager? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private lateinit var mMediaProjectionCallback: MainActivity.MediaProjectionCallback
    private var mMediaRecorder: MediaRecorder? = null

    private val mBinder = ServiceBinder()

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
    }

    private fun initialize() {
        val dm = getResources().getDisplayMetrics()
        mScreenDensity = dm.densityDpi

        mProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    inner class ServiceBinder : Binder() {
        val service: RecorderService
            get() = this@RecorderService
    }

    fun getRecordServiceState(): Int {
        return mRecordState
    }

    private fun setRecordServiceState(state: Int) {
        when (state) {
            RECORD_STATE_IDLE, RECORD_STATE_PAUSE, RECORD_STATE_RECORDING, RECORD_STATE_STOP -> mRecordState = state

            else -> Log.d(TAG, "Not support this kind of state:$state")
        }
    }

    override fun startRecord() {
        val initMedia = prepareMediaRecorder()
        if (!initMedia) {
            return
        }

        if (mMediaProjection == null) {
            //startActivityForResult(mProjectionManager?.createScreenCaptureIntent(), MainActivity.PERMISSION_CODE)
            return
        }

        mVirtualDisplay = createVirtualDisplay()
    }

    private fun prepareMediaRecorder(): Boolean {
        mMediaRecorder = MediaRecorder()
        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mMediaRecorder?.setVideoEncodingBitRate(512 * 1000)
        mMediaRecorder?.setVideoFrameRate(30)
        mMediaRecorder?.setVideoSize(MainActivity.DISPLAY_WIDTH, MainActivity.DISPLAY_HEIGHT)
        mMediaRecorder?.setOutputFile(getFilePath())

        try {
            mMediaRecorder?.prepare()
        } catch (e: IOException) {
            mMediaRecorder = null
            return false
        }

        return true
    }

    private fun createVirtualDisplay(): VirtualDisplay? {
        return mMediaProjection?.createVirtualDisplay(
            "MainActivity",
            MainActivity.DISPLAY_WIDTH, MainActivity.DISPLAY_HEIGHT, MainActivity.mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mMediaRecorder?.surface, null /*Handler*/, null
        )/*Callbacks*/
    }


    private fun getFilePath(): String? {
        val directory = Environment.getExternalStorageDirectory().toString() + File.separator + "Recordings"
        if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
            return null
        }
        val folder = File(directory)
        var success = true
        if (!folder.exists()) {
            success = folder.mkdir()
        }
        val filePath: String
        if (success) {
            val videoName = "capture_" + getCurSysDate() + ".mp4"
            filePath = directory + File.separator + videoName
        } else {
            return null
        }
        return filePath
    }

    private fun getCurSysDate(): String {
        return SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
    }

    override fun stopRecord() {

    }
}
