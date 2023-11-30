package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

class DetailTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        //TODO 11 : Show detail task and implement delete action
        val vModel = ViewModelFactory.getInstance(this)
        val detailVModel : DetailTaskViewModel = ViewModelProvider(this, vModel).get(DetailTaskViewModel::class.java)

        detailVModel.setTaskId(intent.getIntExtra(TASK_ID, 0))
        detailVModel.task.observe(this){
            if (it != null){
                val editTitleDetail = findViewById<TextView>(R.id.detail_ed_title)
                editTitleDetail.text = it.title
                //---
                val editDescriptionDetail = findViewById<TextView>(R.id.detail_ed_description)
                editDescriptionDetail.text = it.description
                //---
                val editDueDateDetail = findViewById<TextView>(R.id.detail_ed_due_date)
                editDueDateDetail.text = DateConverter.convertMillisToString(it.dueDateMillis)
                //---
                val deleteTask = findViewById<Button>(R.id.btn_delete_task)
                deleteTask.setOnClickListener{
                    detailVModel.deleteTask()
                    Toast.makeText(applicationContext," Task anda sudah terhapus!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    }
}