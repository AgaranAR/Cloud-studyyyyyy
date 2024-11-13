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
    private List<String> courseList;
    private List<String> filteredCourseList;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private EditText searchBox;
    private DatabaseReference databaseRef;
    private static final String TAG = "MainActivity";
    private static final String MOCK_USER_ID = "mockUserId";
    private List<String> subjects;
    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        subjects = new ArrayList<>();
        saveButton = findViewById(R.id.saveButton);

        initializeFirebase();
        initializeViews();
        setupRecyclerView();
        loadCourses();
        setupSearchListener();
//        setupFabListener();

        saveButton.setOnClickListener(view -> {
            List<String> selectedSubjects = CourseAdapter.getSelectedSubjects();
            if (!selectedSubjects.isEmpty()) {
                saveSelectedSubjectsToFirebase(selectedSubjects);
            } else {
                Toast.makeText(MainActivity.this, "No subjects selected", Toast.LENGTH_SHORT).show();
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
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        filteredCourseList = new ArrayList<>();
        adapter = new CourseAdapter(this, filteredCourseList, CourseAdapter.getSelectedSubjects());
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
                Toast.makeText(MainActivity.this,
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
                    Toast.makeText(MainActivity.this,
                            "Course added successfully!",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding course: " + e.getMessage());
                    Toast.makeText(MainActivity.this,
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
                        Toast.makeText(MainActivity.this, "Subjects saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error saving subjects", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
