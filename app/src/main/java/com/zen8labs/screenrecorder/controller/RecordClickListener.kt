/******************************************************************************
 * Class : RecordClickListener.kt
 * Handle click on record item
 * Just for demo project
 * Version : v0.1
 * Date : Jul 01, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.controller

import com.zen8labs.screenrecorder.model.RecordItem

interface RecordClickListener {
    fun onRecordClicked(item: RecordItem)
}