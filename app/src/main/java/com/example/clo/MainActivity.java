package com.example.clo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clo.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        // Initialize Firebase Realtime Database
        databaseRef = FirebaseDatabase.getInstance().getReference("category");

        // Initialize UI components after setContentView
        searchBox = findViewById(R.id.search_box);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        adapter = new CourseAdapter(courseList);
        recyclerView.setAdapter(adapter);

        // Load courses from Realtime Database
        loadCourses();

        // Initialize search box with a TextWatcher
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // FAB action for adding a course
        FloatingActionButton fabAddCourse = findViewById(R.id.fabAddCourse);
        fabAddCourse.setOnClickListener(view -> {
            Snackbar.make(view, "Add Course functionality to be implemented", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
