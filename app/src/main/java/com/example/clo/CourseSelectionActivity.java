package com.example.clo;


import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class CourseSelectionActivity extends AppCompatActivity {

    private Button saveButton;
    private LinearLayout courseListContainer;
    private List<CheckBox> courseCheckBoxes = new ArrayList<>();
    private List<String> selectedCourses = new ArrayList<>();
    private List<String> allCourses; // List of available courses
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_selection);

        saveButton = findViewById(R.id.saveButton);
        courseListContainer = findViewById(R.id.courseListContainer);

        // Initialize the course list (this could be from Firebase or a local list)
        allCourses = new ArrayList<>();

        // Add more courses as needed

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().getReference();

        // Dynamically create CheckBoxes for each course
        for (String course : allCourses) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(course);
            courseListContainer.addView(checkBox);
            courseCheckBoxes.add(checkBox);
        }

        // Retrieve the selected courses from Firebase
        retrieveSelectedCoursesFromFirebase();

        // Save button to save selected courses to Firebase
        saveButton.setOnClickListener(view -> {
            selectedCourses.clear();
            for (CheckBox checkBox : courseCheckBoxes) {
                if (checkBox.isChecked()) {
                    selectedCourses.add(checkBox.getText().toString());
                }
            }

            // Save selected courses to Firebase
            if (!selectedCourses.isEmpty()) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                database.child("users").child(userId).child("selectedCourses").setValue(selectedCourses)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(CourseSelectionActivity.this, "Courses saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CourseSelectionActivity.this, "Error saving courses", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(CourseSelectionActivity.this, "No course selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Retrieve selected courses from Firebase and update CheckBoxes
    private void retrieveSelectedCoursesFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.child("users").child(userId).child("selectedCourses")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Get the selected courses from Firebase
                        List<String> selectedCoursesFromFirebase = (List<String>) task.getResult().getValue();
                        if (selectedCoursesFromFirebase != null) {
                            // Update CheckBoxes based on Firebase data
                            for (int i = 0; i < courseCheckBoxes.size(); i++) {
                                CheckBox checkBox = courseCheckBoxes.get(i);
                                String courseName = checkBox.getText().toString();
                                if (selectedCoursesFromFirebase.contains(courseName)) {
                                    checkBox.setChecked(true);
                                } else {
                                    checkBox.setChecked(false);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(CourseSelectionActivity.this, "Failed to load selected courses", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
