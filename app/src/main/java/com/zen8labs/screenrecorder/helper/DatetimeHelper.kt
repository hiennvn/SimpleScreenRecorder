/******************************************************************************
 * Class : DatetimeHelper.kt
 * For date time manipulate
 * Just for demo project
 * Version : v0.1
 * Date : Jul 01, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.helper

import java.text.SimpleDateFormat
import java.util.*


class DatetimeHelper {
    companion object {
        const val DATE_TIME_FORMAT = "yyyy-MM-dd_HH-mm-ss"

        fun getCurSysDate(): String {
            return SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(Date())
        }
    }
}