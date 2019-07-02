/******************************************************************************
 * Class : FileManager
 * File/Folder storage handling
 * Just for demo project
 * Version : v0.1
 * Date : Jul 01, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.storage

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.widget.Toast
import com.zen8labs.screenrecorder.R
import com.zen8labs.screenrecorder.helper.DatetimeHelper
import com.zen8labs.screenrecorder.storage.RecordConfig.Companion.FOLDER_NAME
import com.zen8labs.screenrecorder.storage.RecordConfig.Companion.NAME_PREFIX
import com.zen8labs.screenrecorder.storage.RecordConfig.Companion.OUTPUT_EXT
import java.io.File
import java.util.concurrent.TimeUnit


class FileManager {
    companion object {
        fun getFilePath(context: Context): String? {
            val directory = Environment.getExternalStorageDirectory().toString() + File.separator + FOLDER_NAME
            if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
                Toast.makeText(context, context.getString(R.string.fail_storage), Toast.LENGTH_SHORT).show()
                return null
            }
            val folder = File(directory)
            var success = true
            if (!folder.exists()) {
                success = folder.mkdir()
            }
            val filePath: String
            if (success) {
                val videoName = NAME_PREFIX + DatetimeHelper.getCurSysDate() + OUTPUT_EXT
                filePath = directory + File.separator + videoName
            } else {
                Toast.makeText(context, context.getString(R.string.fail_directory), Toast.LENGTH_SHORT).show()
                return null
            }
            return filePath
        }

        fun getDuration(filePath: String): String {
            val retriever = MediaMetadataRetriever()
            var duration: Long = 0
            try {
                retriever.setDataSource(filePath)
                duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
            } catch (e: Exception) {

            }
            retriever.release()
            return String.format(
                "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(
                        duration
                    )
                )
            )
        }
    }
}