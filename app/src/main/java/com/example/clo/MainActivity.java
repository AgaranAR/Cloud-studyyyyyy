package com.example.clo;

import android.content.Context;
import android.content.Intent;  // Import this for navigation
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.clo.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Switch switcher;
    boolean nightMODE;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private ActivityMainBinding binding;

    private List<String> courseList;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private EditText searchBox;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button addsubdone = findViewById(R.id.addsubdone);
        databaseRef = FirebaseDatabase.getInstance().getReference("category");
        EditText add = findViewById(R.id.add);

        switcher = findViewById(R.id.switcher);
        searchBox = findViewById(R.id.search_box);

        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night", false);

        if (nightMODE) {
            switcher.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        switcher.setOnClickListener(view -> {
            if (nightMODE) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", true);
            }
            editor.apply();
            nightMODE = !nightMODE;
            updateUIElements();
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        adapter = new CourseAdapter(courseList);
        recyclerView.setAdapter(adapter);

        loadCourses();

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

        FloatingActionButton fabAddCourse = findViewById(R.id.fabAddCourse);
        fabAddCourse.setOnClickListener(view -> {
            add.setVisibility(View.VISIBLE);
            addsubdone.setVisibility(View.VISIBLE);
            addsubdone.setOnClickListener(v -> {
                String categoryname = add.getText().toString().trim();
                String categorynameupper = categoryname.toUpperCase();
                if (!categoryname.isEmpty() && !courseList.contains(categorynameupper)) {
                    addCategory(categorynameupper);
                    add.setText("");
                    add.setVisibility(View.GONE);
                    addsubdone.setVisibility(View.GONE);
                } else if (categoryname.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (courseList.contains(categorynameupper)) {
                    Toast.makeText(MainActivity.this, "Course already exists", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Button to navigate to UploadFileActivity
        Button uploadFileButton = findViewById(R.id.uploadFileButton); // Assuming you added this button in your layout
        uploadFileButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UploadFileActivity.class);
            startActivity(intent);
        });

        updateUIElements();
    }

    private void loadCourses() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courseList.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();
                    if (categoryName != null) {
                        courseList.add(categoryName);
                        Log.d("CategoryName", "Retrieved: " + categoryName);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error getting documents: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIElements() {
        if (nightMODE) {
            searchBox.setTextColor(getResources().getColor(R.color.white));
        } else {
            searchBox.setTextColor(getResources().getColor(R.color.black));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void addCategory(String categoryName) {
        Map<String, Object> categoryStructure = new HashMap<>();
        categoryStructure.put("Books", new ArrayList<>()); // Initialize as empty lists
        categoryStructure.put("StudyMaterials", new ArrayList<>());
        categoryStructure.put("Links", new ArrayList<>());
        categoryStructure.put("QuestionPapers", new ArrayList<>());

        databaseRef.child(categoryName).setValue(categoryStructure)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Submitted successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Submission failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
