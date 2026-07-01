package com.example.studentroomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val birthDate: Long, // Store as timestamp
    val classDepartment: String? = null,
    val profilePhoto: String? = null
)