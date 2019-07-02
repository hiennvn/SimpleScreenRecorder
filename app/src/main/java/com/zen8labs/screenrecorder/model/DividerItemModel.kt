/******************************************************************************
 * Class : DividerItemModel.kt
 * Divider view
 * Just for demo project
 * Version : v0.1
 * Date : Jul 01, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.model

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.zen8labs.screenrecorder.model.holder.BaseEpoxyHolder
import com.zen8labs.screenrecorder.R

@EpoxyModelClass(layout = R.layout.view_holder_item_divider)
abstract class DividerItemModel : EpoxyModelWithHolder<RecordDividerViewHolder>() {
}

class RecordDividerViewHolder : BaseEpoxyHolder() {

}