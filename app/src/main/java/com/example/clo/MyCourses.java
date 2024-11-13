// MyCourses.java
package com.example.clo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clo.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MyCourses extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<String> courseList;
    private List<String> filteredCourseList;
    private RecyclerView recyclerView;
    private MyCourseAdapter adapter;
    private EditText searchBox;
    private DatabaseReference databaseRef;
    private static final String TAG = "MyCourses";
    private static final String MOCK_USER_ID = "mockUserId";
    private List<String> subjects;
    private FloatingActionButton saveButton;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_my_courses);

        subjects = new ArrayList<>();
        saveButton = findViewById(R.id.saveButton2);
        if (saveButton != null) {
            saveButton.setOnClickListener(view -> {
                List<String> selectedSubjects = MyCourseAdapter.getSelectedSubjects();
                if (!selectedSubjects.isEmpty()) {
                    saveSelectedSubjectsToFirebase(selectedSubjects);
                } else {
                    Toast.makeText(MyCourses.this, "No subjects selected", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "saveButton is null, unable to set OnClickListener");
        }

        initializeFirebase();
        initializeViews();
        setupRecyclerView();
        loadCourses();
        setupSearchListener();
//        setupFabListener();

        saveButton.setOnClickListener(view -> {
            List<String> selectedSubjects = MyCourseAdapter.getSelectedSubjects();
            if (!selectedSubjects.isEmpty()) {
                saveSelectedSubjectsToFirebase(selectedSubjects);
            } else {
                Toast.makeText(MyCourses.this, "No subjects selected", Toast.LENGTH_SHORT).show();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setSelectedItemId(R.id.nav_all_courses);

        // Set up Bottom Navigation View listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    // Start MainActivity (or another home activity if needed)
                    startActivity(new Intent(MyCourses.this, MainActivity.class));

                    return true;
                } else if (item.getItemId() == R.id.nav_all_courses) {
                    // Start AllCoursesActivity (your existing page for courses)
                    startActivity(new Intent(MyCourses.this, MyCourses.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_upload) {
                    // Start UploadActivity (your existing page for uploading)
                    startActivity(new Intent(MyCourses.this, upload_file.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_logout) {
                    // Handle logout, e.g., clear session and redirect to login page
                    // logoutUser(); // Uncomment this when needed
                    return true;
                }
                return false;
            }
        });
    }

    private void initializeFirebase() {
        FirebaseApp.initializeApp(this);
        databaseRef = FirebaseDatabase.getInstance().getReference("category");
    }

    private void initializeViews() {
        searchBox = binding.searchBox;
        searchBox.setHint("Search courses...");
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        filteredCourseList = new ArrayList<>();
        adapter = new MyCourseAdapter(this, filteredCourseList, MyCourseAdapter.getSelectedSubjects());
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchListener() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCourses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

//    private void setupFabListener() {
//        FloatingActionButton fabAddCourse = binding.fabAddCourse;
//        EditText add = binding.add;
//        Button addsubdone = binding.addsubdone;
//
//        fabAddCourse.setOnClickListener(view -> {
//            add.setVisibility(View.VISIBLE);
//            addsubdone.setVisibility(View.VISIBLE);
//            add.requestFocus();
//        });
//
//        addsubdone.setOnClickListener(view -> {
//            String categoryname = add.getText().toString().trim();
//            String categorynameupper = categoryname.toUpperCase();
//
//            if (categoryname.isEmpty()) {
//                add.setError("Category name cannot be empty");
//                return;
//            }
//
//            if (courseList.contains(categorynameupper)) {
//                add.setError("Course already exists");
//                return;
//            }
//
//            addCategory(categorynameupper);
//            add.setText("");
//            add.setVisibility(View.GONE);
//            addsubdone.setVisibility(View.GONE);
//        });
//    }

    private void loadCourses() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courseList.clear();
                filteredCourseList.clear();

                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();
                    if (categoryName != null) {
                        courseList.add(categoryName);
                        filteredCourseList.add(categoryName);
                        Log.d(TAG, "Retrieved category: " + categoryName);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                Toast.makeText(MyCourses.this,
                        "Failed to load courses: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategory(String categoryName) {
        String[] insideCategory = {"qp", "links", "Study materials", "Books"};
        DatabaseReference subjectRef = databaseRef.child(categoryName);

        List<Task<Void>> tasks = new ArrayList<>();
        for (String s : insideCategory) {
            Task<Void> task = subjectRef.child(s).setValue("");
            tasks.add(task);
        }

        Tasks.whenAllComplete(tasks)
                .addOnSuccessListener(taskSnapshots -> {
                    Toast.makeText(MyCourses.this,
                            "Course added successfully!",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding course: " + e.getMessage());
                    Toast.makeText(MyCourses.this,
                            "Failed to add course: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void filterCourses(String query) {
        filteredCourseList.clear();
        if (query.isEmpty()) {
            filteredCourseList.addAll(courseList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (String course : courseList) {
                if (course.toLowerCase().contains(lowerCaseQuery)) {
                    filteredCourseList.add(course);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void saveSelectedSubjectsToFirebase(List<String> selectedSubjects) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(MOCK_USER_ID).child("selectedSubjects");

        userRef.setValue(selectedSubjects)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MyCourses.this, "Subjects saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyCourses.this, "Error saving subjects", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}