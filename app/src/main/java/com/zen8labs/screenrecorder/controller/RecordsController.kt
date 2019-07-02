/******************************************************************************
 * Class : RecordsController.kt
 * Controller for list record
 * Just for demo project
 * Version : v0.1
 * Date : Jul 01, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.controller

import com.airbnb.epoxy.EpoxyController
import com.zen8labs.screenrecorder.model.RecordItem
import com.zen8labs.screenrecorder.model.dividerItem
import com.zen8labs.screenrecorder.model.recordItem
import java.util.*

class RecordsController : EpoxyController() {

    private var records: List<RecordItem> = Collections.emptyList()
    private var currentFile: String = ""
    var recordListener: RecordClickListener? = null

    fun setData(records: List<RecordItem>, playingFile: String) {
        this.records = records
        this.currentFile = playingFile
        requestModelBuild()
    }

    private fun buildModels(records: List<RecordItem>) {
        if (records.isNotEmpty()) {
            for ((index, item) in records.sortedBy { it.index }.iterator().withIndex()) {
                recordItem {
                    id("RecordItem_$index")
                    item(item)
                    onClickListener {
                        recordListener?.onRecordClicked(item)
                    }
                    if (currentFile.trim().isNotEmpty() && item.path.contains(currentFile)) {
                        playingRecord(true)
                    } else {
                        playingRecord(false)
                    }

                }

                dividerItem {
                    id("Divider_$index")
                }
            }
        }
    }

    override fun buildModels() {
        if (!isBuildingModels) {
            throw IllegalStateException(
                "You cannot call `buildModels` directly. Call `setData` instead to trigger a model " + "refresh with new data.")
        }
        buildModels(records)
    }
}