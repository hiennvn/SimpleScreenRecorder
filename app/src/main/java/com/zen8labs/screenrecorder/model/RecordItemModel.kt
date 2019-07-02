/******************************************************************************
 * Class : RecordItemModel.kt
 * Model for record item
 * Just for demo project
 * Version : v0.1
 * Date : Jul 01, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.model

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.zen8labs.screenrecorder.R
import com.zen8labs.screenrecorder.model.holder.BaseEpoxyHolder
import com.zen8labs.screenrecorder.storage.FileManager
import java.io.File

@EpoxyModelClass(layout = R.layout.view_holder_item_record)
abstract class RecordItemModel : EpoxyModelWithHolder<RecordItemViewHolder>() {

    @EpoxyAttribute lateinit var item: RecordItem
    @EpoxyAttribute
    lateinit var onClickListener: () -> Unit
    @EpoxyAttribute
    var playingRecord: Boolean = false

    override fun bind(holder: RecordItemViewHolder) {
        holder.itemContentView.setOnClickListener { onClickListener.invoke() }
        Glide.with(holder.itemThumbnail.context)
            .asBitmap()
            .load(R.drawable.mp4)
            .apply(RequestOptions.fitCenterTransform())
            .into(holder.itemThumbnail)

        holder.itemIndexTitle.text =
            holder.itemIndexTitle.context.getString(R.string.record_item_description, item.index)
        holder.itemDateTitle.text = item.name.substring(item.name.indexOf("_") + 1, item.name.indexOf("."))
        holder.itemDuration.text = FileManager.getDuration(item.path)
    }
}

class RecordItemViewHolder : BaseEpoxyHolder() {
    val itemContentView by bind<ConstraintLayout>(R.id.item_content_view)
    val itemThumbnail by bind<ImageView>(R.id.iv_thumbnail)
    val itemIndexTitle by bind<TextView>(R.id.record_index_title)
    val itemDateTitle by bind<TextView>(R.id.record_datetime_title)
    val itemDuration by bind<TextView>(R.id.record_duration_title)
    val itemStatus by bind<ImageView>(R.id.iv_status)
}