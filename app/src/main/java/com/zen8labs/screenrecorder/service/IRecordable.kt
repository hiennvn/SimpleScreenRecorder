/******************************************************************************
 * Class : IRecordable.kt
 * Interface for record service
 * Just for demo project
 * Version : v0.1
 * Date : Jul 01, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.service

/**
 * TODO For long term we should separate record service
 */
interface IRecordable {
    fun startRecord()
    fun stopRecord()
}