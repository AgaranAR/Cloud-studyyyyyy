package com.example.clo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<String> selectedCourses; // Specify type for selectedCourses
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the selected courses from Firebase
        retrieveSelectedCoursesFromFirebase();

        FloatingActionButton fabMyCourses = findViewById(R.id.fab_my_courses);
        fabMyCourses.setOnClickListener(v -> navigateToMyCourses());

        bottomNavigationView = findViewById(R.id.bottom_nav);

        // Set up Bottom Navigation View listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    // Start MainActivity (or another home activity if needed)
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_all_courses) {
                    // Start AllCoursesActivity (your existing page for courses)
                    startActivity(new Intent(MainActivity.this, MyCourses.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_upload) {
                    // Start UploadActivity (your existing page for uploading)
                    startActivity(new Intent(MainActivity.this, upload_file.class));
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

    private void navigateToMyCourses() {
        Intent intent = new Intent(MainActivity.this, MyCourses.class);
        startActivity(intent);
    }

    private void retrieveSelectedCoursesFromFirebase() {
        String userId = "mockUserId"; // Replace with actual user ID
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("selectedSubjects");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedCourses = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String course = snapshot.getValue(String.class);
                    selectedCourses.add(course);
                    Log.d("MainActivity", "Retrieved course: " + course);
                }

                // Confirm the size of selectedCourses list
                Log.d("MainActivity", "Total courses retrieved: " + selectedCourses.size());

                // Set the adapter after data is retrieved
                courseAdapter = new CourseAdapter(MainActivity.this, selectedCourses);
                recyclerView.setAdapter(courseAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load selected courses", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
