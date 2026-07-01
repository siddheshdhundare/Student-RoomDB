package com.example.studentroomdb

import androidx.room.*

@Dao
interface StudentDao {

    @Insert
    suspend fun insert(student: Student)

    @Update
    suspend fun update(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<Student>
}