package com.example.studentroomdb

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var db: StudentDatabase
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = StudentDatabase.getDatabase(this)

        val etName = findViewById<EditText>(R.id.etName)
        val etCourse = findViewById<EditText>(R.id.etCourse)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val recycler = findViewById<RecyclerView>(R.id.recyclerView)

        adapter = StudentAdapter(emptyList()) { student ->
            CoroutineScope(Dispatchers.IO).launch {
                db.studentDao().delete(student)
                loadStudents()
            }
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        loadStudents()

        btnSave.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                db.studentDao().insert(
                    Student(
                        name = etName.text.toString(),
                        course = etCourse.text.toString()
                    )
                )

                runOnUiThread {
                    etName.text.clear()
                    etCourse.text.clear()
                }

                loadStudents()
            }
        }
    }

    private fun loadStudents() {

        CoroutineScope(Dispatchers.IO).launch {

            val students = db.studentDao().getAllStudents()

            runOnUiThread {

                adapter.updateData(students)
            }
        }
    }
}