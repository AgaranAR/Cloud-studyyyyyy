package com.example.clo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SubdivisionsActivity extends AppCompatActivity {

    private LinearLayout subdivisionsContainer;
    private TextView subdivisionDetailTextView; // TextView to show subdivision details
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subdivisions);

        subdivisionsContainer = findViewById(R.id.subdivisionsContainer);
        subdivisionDetailTextView = findViewById(R.id.subdivisionDetailTextView); // Initialize detail TextView

        databaseReference = FirebaseDatabase.getInstance().getReference("category");

        String categoryName = getIntent().getStringExtra("categoryName");

        fetchSubdivisionsFromFirebase(categoryName);
    }

    private void fetchSubdivisionsFromFirebase(String categoryName) {
        databaseReference.child(categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subdivisionsContainer.removeAllViews();

                for (DataSnapshot subdivisionSnapshot : dataSnapshot.getChildren()) {
                    String subdivisionName = subdivisionSnapshot.getKey();

                    Button button = new Button(SubdivisionsActivity.this);
                    button.setText(subdivisionName);
                    button.setTextSize(18);
                    button.setPadding(8, 8, 8, 8);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showSubdivisionDetail(subdivisionSnapshot);
                        }
                    });

                    subdivisionsContainer.addView(button);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
    }

    private void showSubdivisionDetail(DataSnapshot subdivisionSnapshot) {
        StringBuilder detailText = new StringBuilder();

        // Assuming each subdivision has key-value pairs, like "Books": "pending"
        for (DataSnapshot detailSnapshot : subdivisionSnapshot.getChildren()) {
            String key = detailSnapshot.getKey();
            String value = detailSnapshot.getValue(String.class);
            detailText.append(key).append(": ").append(value).append("\n");
        }

        subdivisionDetailTextView.setText(detailText.toString()); // Display details in the TextView
    }
}
