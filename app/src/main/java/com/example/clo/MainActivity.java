package com.example.clo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clo.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // Sample data for RecyclerView
    private List<String> courseList;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private EditText searchBox;
    private DatabaseReference databaseRef; // Reference to the Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        // Initialize Firebase Realtime Database
        databaseRef = FirebaseDatabase.getInstance().getReference("category");

        // Initialize UI components after setContentView
        searchBox = findViewById(R.id.search_box);
        Button addsubdone = findViewById(R.id.addsubdone);
        EditText add = findViewById(R.id.add);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        adapter = new CourseAdapter(this, courseList);
        recyclerView.setAdapter(adapter);

        // Load courses from Realtime Database
        loadCourses();

        // Initialize search box with a TextWatcher
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Optionally filter the course list if needed
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // FAB action for adding a course
        FloatingActionButton fabAddCourse = findViewById(R.id.fabAddCourse);
        fabAddCourse.setOnClickListener(view -> {
            // Show the EditText for input
            add.setVisibility(View.VISIBLE);
            addsubdone.setVisibility(View.VISIBLE);

            // Set a listener for when the user presses Enter
            addsubdone.setOnClickListener(view1 -> {
                String categoryname = add.getText().toString().trim(); // Trim any extra whitespace
                String categorynameupper = categoryname.toUpperCase();
                if (!categoryname.isEmpty() && !courseList.contains(categorynameupper)) { // Check if the category name is not empty
                    addCategory(categorynameupper); // Call the method to add the category
                    add.setText(""); // Clear the input field after adding
                    add.setVisibility(View.GONE); // Optionally hide the input field
                    addsubdone.setVisibility(View.GONE);
                } else if (categoryname.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (courseList.contains(categorynameupper)) {
                    Toast.makeText(MainActivity.this, "Course already exists", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadCourses() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courseList.clear(); // Clear existing course list
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    // Get the category name
                    String categoryName = categorySnapshot.getKey();
                    if (categoryName != null) {
                        courseList.add(categoryName);
                        Log.d("CategoryName", "Retrieved: " + categoryName); // Log category name
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter of data changes
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error getting documents: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategory(String categoryName) {
        String[] insideCategory = {"qp", "links", "Study materials", "Books"};
        DatabaseReference subjectRef = databaseRef.child(categoryName);

        if (categoryName != null) {
            // List to hold the tasks
            List<Task<Void>> tasks = new ArrayList<>();

            // Loop through each inside category
            for (String s : insideCategory) {
                // Add each setValue task to the list
                Task<Void> task = subjectRef.child(s).setValue("pending");
                tasks.add(task);
            }

            // Add a listener for when all tasks are complete
            Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Submitted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Some submissions failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
