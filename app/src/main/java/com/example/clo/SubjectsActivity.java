package com.example.clo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;  // Added for @NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;// Added for List

public class SubjectsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SubjectsAdapter adapter;
    private DatabaseReference databaseRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_activity);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("category");

        // Initialize SharedPreferences for favorites
        sharedPreferences = getSharedPreferences("Favorites", MODE_PRIVATE);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.subjectsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Pass empty list in constructor
        recyclerView.setAdapter(adapter);

        // Load subjects
        loadSubjects();
        // SubjectsActivity.java
        adapter = new SubjectsAdapter(new ArrayList<>(), this);

    }

    private void loadSubjects() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Subject> subjects = new ArrayList<>();
                for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                    String subjectName = subjectSnapshot.getKey();
                    boolean isFavorite = sharedPreferences.getBoolean(subjectName, false);
                    subjects.add(new Subject(subjectName, isFavorite));
                }
//                adapter.setSubjects(subjects);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SubjectsActivity.this,
                        "Error loading subjects: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}