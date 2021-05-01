package com.example.trelloclone.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trelloclone.R
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setupActionBar()

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_task_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24)
            actionBar.title = resources.getString(R.string.task_list)
        }
        toolbar_task_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}