package com.example.studentroomdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
    private var list: List<Student>,
    private val onDeleteClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name = view.findViewById<TextView>(R.id.tvName)
        val course = view.findViewById<TextView>(R.id.tvCourse)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val student = list[position]
        holder.name.text = student.name
        holder.course.text = student.course
        holder.btnDelete.setOnClickListener {
            onDeleteClick(student)
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<Student>) {

        list = newList
        notifyDataSetChanged()
    }
}