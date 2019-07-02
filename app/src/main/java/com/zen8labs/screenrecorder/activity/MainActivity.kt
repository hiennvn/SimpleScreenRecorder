/******************************************************************************
 * Class : MainActivity.kt
 * This is entry point for Awesome Screen Recorder
 * Just for demo project
 * Version : v0.1
 * Date : Jun 30, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.snackbar.Snackbar
import com.halilibo.bvpkotlin.BetterVideoPlayer
import com.zen8labs.screenrecorder.R
import com.zen8labs.screenrecorder.activity.RecordsActivity.Companion.FILE_PATH_KEY
import com.zen8labs.screenrecorder.helper.PermissionHelper
import com.zen8labs.screenrecorder.storage.FileManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fab_menu.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "zen8labs"
        const val LIST_ITEM_REQUEST_CODE = 101
        const val PERMISSION_CODE = 1
        const val DISPLAY_WIDTH = 480
        const val DISPLAY_HEIGHT = 640
        var mScreenDensity: Int = 0
        var PERMISSION_ALL = 1

        var PERMISSIONS = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO
        )
    }

    private var isRecording = false
    private var mProjectionManager: MediaProjectionManager? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mMediaRecorder: MediaRecorder? = null
    private var mLatestFilepath: String? = null
    private lateinit var mMediaProjectionCallback: MediaProjectionCallback
    private lateinit var animation1: Animation
    private lateinit var animation2: Animation

    @BindView(R.id.player)
    lateinit var player: BetterVideoPlayer
    @BindView(R.id.empty_layout)
    lateinit var emptyLayout: LinearLayout
    @BindView(R.id.title)
    lateinit var tvTitle: TextView
    @BindView(R.id.message)
    lateinit var tvMessage: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        ButterKnife.bind(this)

        initialize()

        setupFloatingButton()

        if (!PermissionHelper.hasPermissions(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.RECORD_AUDIO
            )
        ) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                PERMISSION_ALL
            )
        }
    }

    private fun playMedia(uri: Uri, now: Boolean) {
        player.visibility = View.VISIBLE
        emptyLayout.visibility = View.GONE

        try {
            player.reset()
            player.setSource(uri)
            player.showControls()
            if (!player.isPlaying() && now) {
                player.setAutoPlay(true)
                player.start()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to play file $uri")
        }
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissionsList: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_ALL -> {
                if (grantResults.isNotEmpty()) {
                    var permissionsDenied = 0
                    for (per in grantResults) {
                        if (per == PackageManager.PERMISSION_DENIED) {
                            permissionsDenied += 1
                        }
                    }

                    if (permissionsDenied > 0) {
                        showDialogPermission()
                    }
                }
                return
            }
        }
    }

    private fun showDialogPermission() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(R.string.permission_denied)
        builder.setMessage(R.string.permission_denied_message)

        builder.setPositiveButton("OK") { _, _ ->
            finish()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun setupFloatingButton() {
        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.replace_action), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action), null).show()
        }
        fab.setOnMenuButtonClickListener(object: View.OnClickListener  {

            override fun onClick(v: View?) {
                if (isRecording) {
                    stopRecordingScreen()
                    Snackbar.make(v!!, getString(R.string.stop_record), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.action), null).show()
                    record.labelText = getString(R.string.record)
                    record.setImageResource(R.drawable.ic_record)
                    record.colorNormal = (ContextCompat.getColor(this@MainActivity, R.color.fabNormal))

                    playMediaFiletPath(mLatestFilepath, false)
                    isRecording = false

                    fab.menuButtonColorNormal = (ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                    fab.menuIconView.setImageResource(R.drawable.fab_add)
                } else {
                    if (fab.isOpened) {
                        fab.close(true)
                    } else {
                        fab.open(true)
                    }
                }
            }

        } )
        fab.setClosedOnTouchOutside(true)

        record.setOnClickListener { view ->
            if (isRecording) {
                stopRecordingScreen()
                Snackbar.make(view, getString(R.string.stop_record), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.action), null).show()
                record.labelText = getString(R.string.record)
                record.setImageResource(R.drawable.ic_record)
                record.colorNormal = (ContextCompat.getColor(this, R.color.fabNormal))

                playMediaFiletPath(mLatestFilepath, false)
            } else {
                requestStartRecordingScreen()
                Snackbar.make(view, getString(R.string.start_record), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.action), null).show()
                record.labelText = getString(R.string.stop)
                tvTitle.text = getString(R.string.recording)
                tvMessage.text = getString(R.string.recording_message)
                record.setImageResource(R.drawable.ic_stop)
                record.colorNormal = (ContextCompat.getColor(this, R.color.fabRecord))

                fab.menuButtonColorNormal = (ContextCompat.getColor(this, R.color.fabRecord))
                fab.menuIconView.setImageResource(R.drawable.ic_stop)
            }

            fab.close(true)
            isRecording = !isRecording
            updateRecording(isRecording)
        }

        list.setOnClickListener {
            val listRecords: Intent =
                RecordsActivity.callingIntent(context = this@MainActivity, filePath = mLatestFilepath ?: "")
            startActivityForResult(
                listRecords,
                LIST_ITEM_REQUEST_CODE
            )

            fab.close(true)
        }
    }

    private fun updateRecording(recording: Boolean) {
        if (recording) {
            player.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.fabRecord))
            animation1 = AlphaAnimation(0.0f, 1.0f)
            animation1.duration = 1000
            animation1.fillAfter = true

            animation2 = AlphaAnimation(1.0f, 0.0f)
            animation2.duration = 1000
            animation2.fillAfter = true

            animation1.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    tvTitle.startAnimation(animation2)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })


            animation2.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    tvTitle.startAnimation(animation1)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })

            tvTitle.startAnimation(animation1)
        } else {
            player.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
            tvTitle.clearAnimation()
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.textHintColor))
        }
    }

    private fun playMediaFiletPath(filePath: String?, now: Boolean) {
        filePath?.let {
            playMedia(Uri.parse(it), now)
        }
    }

    private fun initialize() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        mScreenDensity = metrics.densityDpi

        mProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                Toast.makeText(
                    this,
                    getString(R.string.zen8labs), Toast.LENGTH_SHORT
                ).show()

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (!allowBackWhileRecording()) {

        } else {
            super.onBackPressed()
        }
    }

    private fun allowBackWhileRecording(): Boolean {
        if (isRecording) {
            Toast.makeText(
                this,
                getString(R.string.stop_record_wrn), Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun requestStartRecordingScreen() {
        val initMedia = prepareMediaRecorder()
        if (!initMedia) {
            Log.w(TAG, getString(R.string.init_error_media_recorder))
            return
        }

        if (mMediaProjection == null) {
            startActivityForResult(
                mProjectionManager?.createScreenCaptureIntent(),
                PERMISSION_CODE
            )
            return
        }

        mVirtualDisplay = createVirtualDisplay()

    }

    private fun startRecording() {
        mVirtualDisplay = createVirtualDisplay()

        try {
            mMediaRecorder?.start()
        } catch (e: RuntimeException) {
            Log.d(TAG, getString(R.string.start_record_fail))
            finish()
        }
    }

    private fun stopRecordingScreen() {
        try {
            mMediaRecorder?.stop()
        } catch (e: Exception) {

        } finally {
            mMediaRecorder?.release()
            mMediaRecorder = null
        }

        try {
            mMediaProjection?.stop()
            mVirtualDisplay?.release()
        } catch (e: java.lang.Exception) {
            Log.d(TAG, getString(R.string.stop_record_fail))
        } finally {
            mMediaProjection = null
            mVirtualDisplay = null
        }

        Log.d(TAG, getString(R.string.stop_record_success))
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
        mMediaRecorder?.setVideoSize(
            DISPLAY_WIDTH,
            DISPLAY_HEIGHT
        )
        mLatestFilepath = FileManager.getFilePath(this)
        mMediaRecorder?.setOutputFile(mLatestFilepath)

        try {
            mMediaRecorder?.prepare()
        } catch (e: IOException) {
            Log.w(TAG, getString(R.string.record_error))
            mMediaRecorder = null
            return false
        }

        return true
    }

    inner class MediaProjectionCallback : MediaProjection.Callback() {
        override fun onStop() {

            Log.i(TAG, getString(R.string.media_stopped))
        }
    }

    private fun createVirtualDisplay(): VirtualDisplay? {
        return mMediaProjection?.createVirtualDisplay(
            "MainActivity",
            DISPLAY_WIDTH,
            DISPLAY_HEIGHT,
            mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mMediaRecorder?.surface, null /*Handler*/, null
        )/*Callbacks*/
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LIST_ITEM_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) run {
                mLatestFilepath = data!!.getStringExtra(FILE_PATH_KEY)
                playMediaFiletPath(mLatestFilepath, true)
            }
        }
        if (requestCode != PERMISSION_CODE) {
            Log.e(TAG, "Unknown request code: $requestCode")
            return
        }
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(
                this,
                getString(R.string.screen_cast_denied), Toast.LENGTH_SHORT
            ).show()

            showDialogPermission()
            return
        }

        initializeMediaProjection(resultCode, data!!)

        startRecording()
    }

    private fun initializeMediaProjection(resultCode: Int, data: Intent) {
        mMediaProjectionCallback = MediaProjectionCallback()

        mMediaProjection = mProjectionManager?.getMediaProjection(resultCode, data)
        mMediaProjection?.registerCallback(mMediaProjectionCallback, null)
    }



}
