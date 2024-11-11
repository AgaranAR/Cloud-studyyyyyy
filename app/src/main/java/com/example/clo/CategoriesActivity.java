package com.example.clo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CategoriesActivity extends AppCompatActivity {
    private String subjectName;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity);

        // Get subject name from intent
        subjectName = getIntent().getStringExtra("SUBJECT_NAME");
        if (subjectName == null) {
            Toast.makeText(this, "Error: Subject name not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        setupToolbar();

        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("category").child(subjectName);

        // Setup category cards
        setupCategoryCards();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(subjectName);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void setupCategoryCards() {
        MaterialCardView booksCard = findViewById(R.id.booksCard);
        MaterialCardView studyMaterialsCard = findViewById(R.id.studyMaterialsCard);
        MaterialCardView linksCard = findViewById(R.id.linksCard);
        MaterialCardView qpCard = findViewById(R.id.qpCard);

        if (booksCard != null) {
            booksCard.setOnClickListener(v -> openCategory("Books"));
        }
        if (studyMaterialsCard != null) {
            studyMaterialsCard.setOnClickListener(v -> openCategory("Study materials"));
        }
        if (linksCard != null) {
            linksCard.setOnClickListener(v -> openCategory("links"));
        }
        if (qpCard != null) {
            qpCard.setOnClickListener(v -> openCategory("qp"));
        }
    }

    private void openCategory(String category) {
        if (databaseRef == null) {
            Toast.makeText(this, "Database reference not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseRef.child(category).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String value = task.getResult().getValue(String.class);
                if ("pending".equals(value)) {
                    Toast.makeText(this, "No content available yet", Toast.LENGTH_SHORT).show();
                } else {
                    navigateToContentActivity(category);
                }
            } else {
                String errorMessage = task.getException() != null ?
                        task.getException().getMessage() : "Unknown error";
                Toast.makeText(this, "Error loading content: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToContentActivity(String category) {
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("SUBJECT_NAME", subjectName);
        intent.putExtra("CATEGORY", category);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}