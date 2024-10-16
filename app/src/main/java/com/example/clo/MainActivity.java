package com.example.clo;

import android.content.Context;
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
    Switch switcher;
    boolean nightMODE;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
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

        Button addsubdone = findViewById(R.id.addsubdone);
        // Iniv tialize Firebase Realtime Database
        databaseRef = FirebaseDatabase.getInstance().getReference("category");
        EditText add = findViewById(R.id.add);

        // Initialize UI components after setContentView
        switcher = findViewById(R.id.switcher);
        searchBox = findViewById(R.id.search_box);

        // Setup SharedPreferences to store theme preference
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night", false); // Default is false

        // Apply night mode based on the preference
        if (nightMODE) {
            switcher.setChecked(true); // Set switch checked if night mode is enabled
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Set an onClickListener for the switch
        switcher.setOnClickListener(view -> {
            if (nightMODE) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", false); // Save preference for no night mode
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", true); // Save preference for night mode
            }
            editor.apply(); // Apply the changes
            nightMODE = !nightMODE; // Toggle the night mode flag

            // Update UI elements based on the new theme
            updateUIElements();
        });

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
                    // Show the EditText for input
                    add.setVisibility(View.VISIBLE);
                    addsubdone.setVisibility(View.VISIBLE);
                    // Set a listener for when the user presses Enter
                    addsubdone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String categoryname = add.getText().toString().trim(); // Trim any extra whitespace
                            String categorynameupper = categoryname.toUpperCase();
                            if (!categoryname.isEmpty() && !courseList.contains(categorynameupper)) { // Check if the category name is not empty
                                addCategory(categorynameupper); // Call the method to add the category
                                add.setText(""); // Clear the input field after adding
                                add.setVisibility(View.GONE); // Optionally hide the input field
                                addsubdone.setVisibility(View.GONE);
                            } else if(categoryname.isEmpty()) {
                                Toast.makeText(MainActivity.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                            }
                            else     if (courseList.contains(categorynameupper)){
                                Toast.makeText(MainActivity.this, "Course already exists", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                });
        // Update UI elements based on initial theme
        updateUIElements();
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

    private void updateUIElements() {
        // Update EditText colors based on night mode
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
