package com.example.studentroomdb

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: StudentDatabase
    private lateinit var adapter: StudentAdapter
    private lateinit var emptyState: LinearLayout
    private var selectedBirthDate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = StudentDatabase.getDatabase(this)

        val etName = findViewById<EditText>(R.id.etName)
        val etClass = findViewById<EditText>(R.id.etClass)
        val btnPickDate = findViewById<Button>(R.id.btnPickDate)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        emptyState = findViewById(R.id.emptyState)

        adapter = StudentAdapter(emptyList()) { student ->
            CoroutineScope(Dispatchers.IO).launch {
                db.studentDao().delete(student)
                loadStudents()
            }
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnPickDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select birth date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                selectedBirthDate = selection
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                btnPickDate.text = sdf.format(selection)
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val classDept = etClass.text.toString()

            if (name.isNotEmpty() && selectedBirthDate != 0L) {
                CoroutineScope(Dispatchers.IO).launch {
                    db.studentDao().insert(
                        Student(
                            name = name,
                            birthDate = selectedBirthDate,
                            classDepartment = classDept
                        )
                    )

                    runOnUiThread {
                        etName.text.clear()
                        etClass.text.clear()
                        btnPickDate.text = "Select Birth Date"
                        selectedBirthDate = 0
                    }

                    loadStudents()
                }
            }
        }

        loadStudents()
    }

    private fun loadStudents() {
        CoroutineScope(Dispatchers.IO).launch {
            val students = db.studentDao().getAllStudents()
            runOnUiThread {
                if (students.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    adapter.updateData(emptyList())
                } else {
                    emptyState.visibility = View.GONE
                    adapter.updateData(students)
                }
            }
        }
    }
}