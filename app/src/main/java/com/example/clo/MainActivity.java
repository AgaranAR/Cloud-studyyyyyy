package com.example.clo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.clo.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    boolean nightMODE;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private ActivityMainBinding binding;

    // Modify courseList to hold Course objects
    private List<Course> courseList;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private EditText searchBox;

    private DatabaseReference databaseRef; // Reference to the Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button addsubdone = findViewById(R.id.addsubdone);
        // Initialize Firebase Realtime Database
        databaseRef = FirebaseDatabase.getInstance().getReference("category");
        EditText add = findViewById(R.id.add);

        // Initialize UI components after setContentView
        searchBox = findViewById(R.id.search_box);

        // Setup SharedPreferences to store theme preference

        // Set an onClickListener for the switch

        // Update UI elements based on the new theme
        updateUIElements();

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        adapter = new CourseAdapter(this ,courseList);
        recyclerView.setAdapter(adapter);

        // Load courses from Realtime Database
        loadCourses();

        // Initialize search box with a TextWatcher
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        Button btnAllCourses = findViewById(R.id.btn_all_courses);
        btnAllCourses.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SubjectsActivity.class);
            startActivity(intent);
        });

        // FAB action for adding a course
        FloatingActionButton fabAddCourse = findViewById(R.id.fabAddCourse);
        fabAddCourse.setOnClickListener(view -> {
            // Show the EditText for input
            add.setVisibility(View.VISIBLE);
            addsubdone.setVisibility(View.VISIBLE);
            // Set a listener for when the user presses Enter
            addsubdone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String categoryname = add.getText().toString().trim(); // Trim any extra whitespace
                    String categorynameupper = categoryname.toUpperCase();
                    if (!categoryname.isEmpty() && !courseList.contains(new Course(categorynameupper))) { // Check if the category name is not empty
                        addCategory(categorynameupper); // Call the method to add the category
                        add.setText(""); // Clear the input field after adding
                        add.setVisibility(View.GONE); // Optionally hide the input field
                        addsubdone.setVisibility(View.GONE);
                    } else if(categoryname.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                    else     if (courseList.contains(new Course(categorynameupper))){
                        Toast.makeText(MainActivity.this, "Course already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        // Update UI elements based on initial theme
        updateUIElements();
    }

    private void loadCourses() {
        // Assuming the mockUserId is already available
        String mockUserId = "mockUserId"; // Replace with actual logic to get the user ID

        // Database reference pointing to the user's selectedSubjects
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(mockUserId).child("selectedSubjects");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courseList.clear(); // Clear the existing list
                for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                    String subject = subjectSnapshot.getValue(String.class); // Assuming subject is a string
                    if (subject != null) {
                        courseList.add(new Course(subject)); // Add to the course list
                        Log.d("SubjectName", "Retrieved: " + subject); // Log the subject name
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error getting data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateUIElements() {
        // Update EditText colors based on night mode
        if (nightMODE) {
            searchBox.setTextColor(getResources().getColor(R.color.white));
        } else {
            searchBox.setTextColor(getResources().getColor(R.color.black));
        }
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
