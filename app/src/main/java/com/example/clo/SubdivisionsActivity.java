package com.example.clo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SubdivisionsActivity extends AppCompatActivity {

    private RecyclerView subdivisionsRecyclerView;
    private TextView categoryTitle;
    private DatabaseReference databaseReference;
    private SubdivisionAdapter adapter;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subdivisions);

        // Initialize views
        subdivisionsRecyclerView = findViewById(R.id.contentRecyclerView);
        categoryTitle = findViewById(R.id.contentTitle);

        // Setup RecyclerView
        subdivisionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubdivisionAdapter();
        subdivisionsRecyclerView.setAdapter(adapter);

        // Setup Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("category");

        // Get category name from intent
        categoryName = getIntent().getStringExtra("categoryName");
        if (categoryName != null) {
            categoryTitle.setText(categoryName);
            fetchSubdivisionsFromFirebase(categoryName);
        }
    }

    private void fetchSubdivisionsFromFirebase(String categoryName) {
        databaseReference.child(categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> subdivisions = new ArrayList<>();

                // Only get the top-level keys (Books, QP, links, Study materials)
                for (DataSnapshot subdivisionSnapshot : dataSnapshot.getChildren()) {
                    subdivisions.add(subdivisionSnapshot.getKey());
                }

                adapter.setSubdivisions(subdivisions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
    }

    // RecyclerView Adapter
    private class SubdivisionAdapter extends RecyclerView.Adapter<SubdivisionAdapter.ViewHolder> {
        private List<String> subdivisions = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_subdivision, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String subdivisionName = subdivisions.get(position);
            holder.bind(subdivisionName);
        }

        @Override
        public int getItemCount() {
            return subdivisions.size();
        }

        void setSubdivisions(List<String> subdivisions) {
            this.subdivisions = subdivisions;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;
            Chip statusChip;

            ViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.subdivisionNameTextView);
                statusChip = itemView.findViewById(R.id.statusChip);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String subdivisionName = subdivisions.get(position);
                        navigateToSubdivisionContent(subdivisionName);
                    }
                });
            }

            void bind(String subdivisionName) {
                if (nameTextView != null) {
                    nameTextView.setText(subdivisionName);
                }

                if (statusChip != null) {
                    statusChip.setVisibility(View.GONE); // Hide the status chip as it's not needed
                }
            }
        }
    }

    private void navigateToSubdivisionContent(String subdivisionName) {
        Intent intent = new Intent(this, SubdivisionContentActivity.class);
        intent.putExtra("categoryName", categoryName);
        intent.putExtra("subdivisionName", subdivisionName);
        startActivity(intent);
    }
}