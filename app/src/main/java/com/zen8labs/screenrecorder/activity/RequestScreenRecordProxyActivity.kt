package com.zen8labs.screenrecorder.activity

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class RequestScreenRecordProxyActivity : AppCompatActivity() {
    private var mediaProjectionManager: MediaProjectionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }
}