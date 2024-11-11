package com.example.clo;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity {
    private String subjectName;
    private String category;
    private DatabaseReference databaseRef;
    private RecyclerView recyclerView;
    private ContentAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // Get intent extras
        subjectName = getIntent().getStringExtra("SUBJECT_NAME");
        category = getIntent().getStringExtra("CATEGORY");

        if (subjectName == null || category == null) {
            Toast.makeText(this, "Error: Missing required data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        setupToolbar();

        // Initialize views
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter
        adapter = new ContentAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("category")
                .child(subjectName)
                .child(category);

        // Load content
        loadContent();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(category);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadContent() {
        showLoading(true);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ContentItem> contentList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String title = itemSnapshot.getKey();
                    String url = itemSnapshot.getValue(String.class);
                    if (title != null && url != null && !url.equals("pending")) {
                        contentList.add(new ContentItem(title, url));
                    }
                }
                adapter.setContentList(contentList);
                showLoading(false);

                if (contentList.isEmpty()) {
                    Toast.makeText(ContentActivity.this,
                            "No content available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                Toast.makeText(ContentActivity.this,
                        "Error loading content: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}