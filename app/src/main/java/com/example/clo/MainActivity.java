package com.example.clo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clo.databinding.ActivityMainBinding;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize UI components after setContentView
        switcher = findViewById(R.id.switcher);
        searchBox = findViewById(R.id.search_box); // Initialize EditText here

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
        // Add sample courses
        courseList.add("Mathematics");
        courseList.add("Physics");
        courseList.add("Chemistry");
        courseList.add("Biology");
        courseList.add("Computer Science");
        adapter = new CourseAdapter(courseList);
        recyclerView.setAdapter(adapter);

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

        // Update UI elements based on initial theme
        updateUIElements();
    }

    private void updateUIElements() {
        // Update EditText text color based on night mode
        if (nightMODE) {
            searchBox.setTextColor(getResources().getColor(R.color.white)); // Text color for night mode
        } else {
            searchBox.setTextColor(getResources().getColor(R.color.black)); // Text color for bright mode
        }

        // No changes to the background color of the search box
        // You can also update RecyclerView item colors here if needed
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
