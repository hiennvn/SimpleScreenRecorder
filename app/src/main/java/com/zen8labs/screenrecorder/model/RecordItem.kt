/******************************************************************************
 * Class : RecordItem.kt
 * Record item
 * Just for demo project
 * Version : v0.1
 * Date : Jul 01, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.model

data class RecordItem(
    var index: Int = 0,
    var name: String = "",
    var path: String = ""
)