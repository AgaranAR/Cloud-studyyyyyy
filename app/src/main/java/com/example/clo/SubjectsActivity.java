package com.example.clo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SubjectsActivity extends AppCompatActivity {

    private Button saveButton;
    private RecyclerView subjectsRecyclerView;
    private SubjectAdapter subjectAdapter;
    private List<String> subjects; // List of subjects to display
    private DatabaseReference database;

    // Replace with actual user ID or use Firebase Authentication to get the current user ID
    private static final String MOCK_USER_ID = "mockUserId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        saveButton = findViewById(R.id.saveButton);
        subjectsRecyclerView = findViewById(R.id.subjectsRecyclerView);

        // Initialize the lists
        subjects = new ArrayList<>();

        // Initialize Firebase database reference for categories
        database = FirebaseDatabase.getInstance().getReference("category");

        // Set up the RecyclerView
        subjectAdapter = new SubjectAdapter(this, subjects, new ArrayList<>());
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectsRecyclerView.setAdapter(subjectAdapter);

        // Load subjects from Firebase
        loadSubjects();
        Toolbar toolbar = findViewById(R.id.toolbar);

     // Update with your layout name

                // Find the back arrow ImageView and set a click listener
                ImageView backArrow = findViewById(R.id.backArrow);
                backArrow.setOnClickListener(v -> onBackPressed());


        // Save selected subjects to Firebase
        saveButton.setOnClickListener(view -> {
            List<String> selectedSubjects = subjectAdapter.getSelectedSubjects(); // Retrieve selected subjects
            if (!selectedSubjects.isEmpty()) {
                saveSelectedSubjectsToFirebase(selectedSubjects);
            } else {
                Toast.makeText(SubjectsActivity.this, "No subjects selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSubjects() {
        // Load categories (the list of available subjects)
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the current list before adding new subjects
                subjects.clear();

                // Loop through each category and add to subjects list
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey(); // Get the category name
                    if (categoryName != null) {
                        subjects.add(categoryName);
                    }
                }

                // Load existing selected subjects for the current user
                loadSelectedSubjectsFromFirebase();

                // Notify the adapter that the data has changed
                subjectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SubjectsActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSelectedSubjectsFromFirebase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(MOCK_USER_ID).child("selectedSubjects");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> selectedSubjects = new ArrayList<>();

                // If there are already selected subjects, load them
                if (dataSnapshot.exists()) {
                    for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                        String selectedSubject = subjectSnapshot.getValue(String.class);
                        if (selectedSubject != null) {
                            selectedSubjects.add(selectedSubject);
                        }
                    }
                }

                // Merge the selected subjects with the new categories
                for (String selectedSubject : selectedSubjects) {
                    if (!subjects.contains(selectedSubject)) {
                        subjects.add(selectedSubject);
                    }
                }

                // Notify the adapter to update the view
                subjectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SubjectsActivity.this, "Failed to load selected subjects", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Save selected subjects under users/mockUserId/selectedSubjects
    private void saveSelectedSubjectsToFirebase(List<String> selectedSubjects) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(MOCK_USER_ID).child("selectedSubjects");

        // Get the current selected subjects from Firebase
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> existingSubjects = new ArrayList<>();

                // If there are already selected subjects, load them
                if (dataSnapshot.exists()) {
                    for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                        String existingSubject = subjectSnapshot.getValue(String.class);
                        if (existingSubject != null) {
                            existingSubjects.add(existingSubject);
                        }
                    }
                }

                // Add only new selected subjects that are not already present
                for (String subject : selectedSubjects) {
                    if (!existingSubjects.contains(subject)) {
                        existingSubjects.add(subject);
                    }
                }

                // Save the updated list of subjects back to Firebase
                userRef.setValue(existingSubjects)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(SubjectsActivity.this, "Subjects saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SubjectsActivity.this, "Error saving subjects", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SubjectsActivity.this, "Failed to load existing subjects", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
