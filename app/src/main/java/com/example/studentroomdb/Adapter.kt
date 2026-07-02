package com.example.studentroomdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StudentAdapter(
    private var list: List<Student>,
    private val onDeleteClick: (Student) -> Unit,
    private val onPhotoClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProfile: ImageView = view.findViewById(R.id.ivProfile)
        val name: TextView = view.findViewById(R.id.tvName)
        val classDept: TextView = view.findViewById(R.id.tvClass)
        val birthDate: TextView = view.findViewById(R.id.tvBirthDate)
        val daysRemaining: TextView = view.findViewById(R.id.tvDaysRemaining)
        val birthdayBadge: TextView = view.findViewById(R.id.tvBirthdayBadge)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = list[position]
        holder.name.text = student.name
        holder.classDept.text = student.classDepartment ?: "No Department"

        if (student.profilePhoto != null) {
            val uri = android.net.Uri.parse(student.profilePhoto)
            holder.ivProfile.setImageURI(uri)
            holder.ivProfile.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            // Default circular avatar placeholder
            holder.ivProfile.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val birthDateCalendar = Calendar.getInstance().apply {
            timeInMillis = student.birthDate
        }

        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthDateCalendar.get(Calendar.YEAR)
        
        val birthMonth = birthDateCalendar.get(Calendar.MONTH)
        val birthDay = birthDateCalendar.get(Calendar.DAY_OF_MONTH)
        val currMonth = today.get(Calendar.MONTH)
        val currDay = today.get(Calendar.DAY_OF_MONTH)
        
        if (currMonth < birthMonth || (currMonth == birthMonth && currDay < birthDay)) {
            age--
        }

        holder.birthDate.text = "Born: ${sdf.format(student.birthDate)} ($age yrs)"

        val daysLeft = getDaysUntilNextBirthday(student.birthDate)

        if (daysLeft == 0L) {
            holder.daysRemaining.text = "TODAY"
            holder.daysRemaining.setBackgroundResource(R.drawable.bg_rounded_days)
            holder.daysRemaining.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFEF4444.toInt())
            holder.birthdayBadge.visibility = View.VISIBLE
            
            val anim = AnimationUtils.loadAnimation(holder.itemView.context, android.R.anim.fade_in)
            holder.birthdayBadge.startAnimation(anim)
        } else {
            holder.daysRemaining.text = "In $daysLeft Days"
            holder.daysRemaining.setBackgroundResource(R.drawable.bg_rounded_days)
            holder.daysRemaining.backgroundTintList = null
            holder.birthdayBadge.visibility = View.GONE
        }

        holder.btnDelete.setOnClickListener { onDeleteClick(student) }
        holder.ivProfile.setOnClickListener { onPhotoClick(student) }
    }

    private fun getDaysUntilNextBirthday(birthDate: Long): Long {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val nextBirthday = Calendar.getInstance().apply {
            timeInMillis = birthDate
            set(Calendar.YEAR, today.get(Calendar.YEAR))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (nextBirthday.before(today)) {
            nextBirthday.add(Calendar.YEAR, 1)
        }

        val diff = nextBirthday.timeInMillis - today.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<Student>) {
        list = newList.sortedWith(compareBy({
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.birthDate
            cal.get(Calendar.MONTH)
        }, {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.birthDate
            cal.get(Calendar.DAY_OF_MONTH)
        }))
        notifyDataSetChanged()
    }
}