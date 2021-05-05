package com.example.trelloclone.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloclone.R
import com.example.trelloclone.adapter.TaskListItemAdapter
import com.example.trelloclone.firebase.FireStoreClass
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.Task
import com.example.trelloclone.utils.Constants
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }


        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this@TaskListActivity, boardDocumentId)


    }


    private fun setupActionBar(title: String) {

        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24)
            actionBar.title = title
        }

        toolbar_task_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun boardDetails(board: Board) {

        hideProgressDialog()
        setupActionBar(board.name)

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        rvTasklist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTasklist.setHasFixedSize(true)
        val taskAdapter = TaskListItemAdapter(this, board.taskList)
        rvTasklist.adapter=taskAdapter
        // END
    }
    // END
}
