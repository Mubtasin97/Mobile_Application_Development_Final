package com.university.coursemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CourseAdapter(
    private var courseList: List<Course>,
    private val onCourseClick: (Course) -> Unit,
    private val onEditClick: (Course) -> Unit,
    private val onDeleteClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCourseName: TextView = itemView.findViewById(R.id.textCourseName)
        val textCourseCode: TextView = itemView.findViewById(R.id.textCourseCode)
        val textInstructor: TextView = itemView.findViewById(R.id.textInstructor)
        val textCredits: TextView = itemView.findViewById(R.id.textCredits)
        val textSchedule: TextView = itemView.findViewById(R.id.textSchedule)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]

        holder.textCourseName.text = course.courseName
        holder.textCourseCode.text = "Code: ${course.courseCode}"
        holder.textInstructor.text = "Instructor: ${course.instructor}"
        holder.textCredits.text = "Credits: ${course.creditHours}"
        holder.textSchedule.text = "Schedule: ${course.schedule}"

        holder.itemView.setOnClickListener {
            onCourseClick(course)
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(course)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(course)
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    fun updateList(newList: List<Course>) {
        courseList = newList
        notifyDataSetChanged()
    }
}