package com.university.coursemanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerCourses: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var searchView: SearchView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var courseAdapter: CourseAdapter

    private val allCourses = ArrayList<Course>()
    private var currentSearchText = ""

    private val databaseRef by lazy {
        FirebaseDatabase.getInstance()
            .getReference("courses")
            .child(StudentNodeHelper.getStudentNode(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerCourses = findViewById(R.id.recyclerCourses)
        emptyView = findViewById(R.id.emptyView)
        searchView = findViewById(R.id.searchView)
        fabAdd = findViewById(R.id.fabAdd)

        courseAdapter = CourseAdapter(
            emptyList(),
            onCourseClick = { course ->
                val intent = Intent(this, CourseDetailActivity::class.java)
                intent.putExtra("course", course)
                startActivity(intent)
            },
            onEditClick = { course ->
                val intent = Intent(this, EditCourseActivity::class.java)
                intent.putExtra("course", course)
                startActivity(intent)
            },
            onDeleteClick = { course ->
                confirmDelete(course)
            }
        )

        recyclerCourses.layoutManager = LinearLayoutManager(this)
        recyclerCourses.adapter = courseAdapter

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddCourseActivity::class.java))
        }

        setupSearch()
        loadCoursesRealtime()
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentSearchText = query.orEmpty()
                filterCourses()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchText = newText.orEmpty()
                filterCourses()
                return true
            }
        })
    }

    private fun loadCoursesRealtime() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allCourses.clear()

                for (courseSnapshot in snapshot.children) {
                    val course = courseSnapshot.getValue(Course::class.java)
                    if (course != null) {
                        if (course.id.isEmpty()) {
                            course.id = courseSnapshot.key.orEmpty()
                        }
                        allCourses.add(course)
                    }
                }

                filterCourses()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filterCourses() {
        val filteredList = if (currentSearchText.isBlank()) {
            allCourses
        } else {
            allCourses.filter {
                it.courseName.contains(currentSearchText, ignoreCase = true) ||
                        it.courseCode.contains(currentSearchText, ignoreCase = true)
            }
        }

        courseAdapter.updateList(filteredList)

        if (filteredList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerCourses.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerCourses.visibility = View.VISIBLE
        }
    }

    private fun confirmDelete(course: Course) {
        AlertDialog.Builder(this)
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete ${course.courseName}?")
            .setPositiveButton("Delete") { _, _ ->
                databaseRef.child(course.id).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}