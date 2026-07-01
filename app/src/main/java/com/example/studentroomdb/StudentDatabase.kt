package com.example.studentroomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Student::class], version = 2)
abstract class StudentDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {

        private var INSTANCE: StudentDatabase? = null

        fun getDatabase(context: Context): StudentDatabase {

            if (INSTANCE == null) {

                INSTANCE = Room.databaseBuilder(
                    context,
                    StudentDatabase::class.java,
                    "student_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return INSTANCE!!
        }
    }
}