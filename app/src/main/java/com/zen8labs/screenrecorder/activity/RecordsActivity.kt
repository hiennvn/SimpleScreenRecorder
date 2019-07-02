/******************************************************************************
 * Class : RecordsActivity.kt
 * List of records
 * Just for demo project
 * Version : v0.1
 * Date : Jul 01, 2019
 * Copyright (c)-2019 ZEN8LABS
 ******************************************************************************/
package com.zen8labs.screenrecorder.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.airbnb.epoxy.EpoxyRecyclerView
import com.zen8labs.screenrecorder.R
import com.zen8labs.screenrecorder.controller.RecordClickListener
import com.zen8labs.screenrecorder.controller.RecordsController
import com.zen8labs.screenrecorder.model.RecordItem
import com.zen8labs.screenrecorder.storage.RecordConfig.Companion.FOLDER_NAME
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class RecordsActivity : AppCompatActivity(), RecordClickListener {

    override fun onRecordClicked(item: RecordItem) {
        Toast.makeText(this@RecordsActivity, item.path, Toast.LENGTH_LONG).show()
        val intent = Intent()
        intent.putExtra(FILE_PATH_KEY, item.path)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val FILE_PATH_KEY = "File_Path"

        fun callingIntent(context: Context, filePath: String): Intent {
            val intent = Intent(context, RecordsActivity::class.java)
            intent.putExtra("Current_File_Playing", filePath)
            return intent
        }
    }

    @BindView(R.id.info_menu_recycler_view)
    lateinit var recyclerView: EpoxyRecyclerView

    private lateinit var controller: RecordsController
    private var currentPlayingFile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)
        ButterKnife.bind(this)
        currentPlayingFile = intent.getStringExtra("Current_File_Playing")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.title = getString(R.string.list_record)
        controller = RecordsController()
        controller.recordListener = this
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.setController(controller)
        recyclerView.setHasFixedSize(false)
        controller.setData(getAllRecordFiles(), currentPlayingFile)
    }

    private fun getAllRecordFiles() : List<RecordItem> {
        val list: MutableList<RecordItem> = mutableListOf()

        val path = Environment.getExternalStorageDirectory().toString() + "/"+ FOLDER_NAME
        val directory = File(path)
        val files = directory.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (i in files.indices) {
                val recordItem = RecordItem(i + 1, files[i].name, "$path/${files[i].name}")
                list.add(recordItem)
            }
        }

        return list
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
