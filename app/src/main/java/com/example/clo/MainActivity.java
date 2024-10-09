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

        // Set the toolbar as the app bar for the activity
        setSupportActionBar(binding.toolbar);

        // Initialize the Switch
        switcher = findViewById(R.id.switcher);  // Ensure this matches the ID in your XML layout

        // Setup SharedPreferences to store theme preference
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night", false); // Default is false

        // Apply night mode based on the preference
        if (nightMODE) {
            switcher.setChecked(true); // Set switch checked if night mode is enabled
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Set an onClickListener for the switch
        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
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

        // Initialize search box
        searchBox = findViewById(R.id.search_box);
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
        fabAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Course functionality to be implemented", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
