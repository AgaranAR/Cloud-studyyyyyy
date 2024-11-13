package com.example.clo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SubdivisionContentActivity extends AppCompatActivity {

    private RecyclerView contentRecyclerView;
    private TextView contentTitle;
    private DatabaseReference databaseReference;
    private ContentAdapter adapter;
    private String categoryName;
    private String subdivisionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subdivision_content);

        // Initialize views
        contentRecyclerView = findViewById(R.id.contentRecyclerView);
        contentTitle = findViewById(R.id.contentTitleTextView);

        // Setup RecyclerView
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContentAdapter();
        contentRecyclerView.setAdapter(adapter);

        // Get data from intent
        categoryName = getIntent().getStringExtra("categoryName");
        subdivisionName = getIntent().getStringExtra("subdivisionName");

        if (categoryName != null && subdivisionName != null) {
            contentTitle.setText(subdivisionName);

            // Setup Firebase reference
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("category")
                    .child(categoryName)
                    .child(subdivisionName);

            fetchContentFromFirebase();
        }
    }

    private void fetchContentFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ContentItem> contentItems = new ArrayList<>();

                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue() instanceof String) {
                        String value = dataSnapshot.getValue(String.class);
                        contentItems.add(new ContentItem("Status", value, ContentType.TEXT));
                    } else {
                        for (DataSnapshot contentSnapshot : dataSnapshot.getChildren()) {
                            String key = contentSnapshot.getKey();
                            Object value = contentSnapshot.getValue();

                            if (value instanceof String) {
                                String valueString = (String) value;
                                if (valueString.contains("firebasestorage")) {
                                    contentItems.add(new ContentItem(key, valueString, getContentTypeFromKey(key)));
                                } else {
                                    contentItems.add(new ContentItem(key, valueString, ContentType.TEXT));
                                }
                            } else if (contentSnapshot.hasChild("imageUrl")) {
                                String imageUrl = contentSnapshot.child("imageUrl").getValue(String.class);
                                if (imageUrl != null) {
                                    contentItems.add(new ContentItem(key, imageUrl, ContentType.IMAGE));
                                }
                            } else if (contentSnapshot.hasChildren()) {
                                for (DataSnapshot fileSnapshot : contentSnapshot.getChildren()) {
                                    String fileKey = fileSnapshot.getKey();
                                    String fileUrl = fileSnapshot.getValue(String.class);
                                    if (fileUrl != null) {
                                        contentItems.add(new ContentItem(fileKey, fileUrl, getContentTypeFromKey(fileKey)));
                                    }
                                }
                            }
                        }
                    }
                }
                adapter.setContentItems(contentItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log the error for debugging
                Log.e("SubdivisionContentActivity", "Firebase error: " + databaseError.getMessage());
            }
        });
    }

    private ContentType getContentTypeFromKey(String key) {
        key = key.toLowerCase();
        if (key.contains("pdf")) {
            return ContentType.PDF;
        } else if (key.contains("image") || key.contains("img")) {
            return ContentType.IMAGE;
        } else if (key.contains("url") || key.contains("link")) {
            return ContentType.LINK;
        }
        return ContentType.TEXT;
    }

    // Content types enum
    private enum ContentType {
        PDF,
        IMAGE,
        LINK,
        TEXT
    }

    // Content item class
    private static class ContentItem {
        String title;
        String content;
        ContentType type;

        ContentItem(String title, String content, ContentType type) {
            this.title = title;
            this.content = content;
            this.type = type;
        }
    }

    // RecyclerView Adapter
    private class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
        private List<ContentItem> contentItems = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ContentItem item = contentItems.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return contentItems.size();
        }

        void setContentItems(List<ContentItem> contentItems) {
            this.contentItems = contentItems;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            TextView contentTextView;
            ImageView contentImageView;
            ImageView contentTypeIcon;
            MaterialCardView cardView;

            ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.contentTitleTextView);
                contentTextView = itemView.findViewById(R.id.contentTextView);
                contentImageView = itemView.findViewById(R.id.contentImageView);
                contentTypeIcon = itemView.findViewById(R.id.contentTypeIcon);
                cardView = itemView.findViewById(R.id.contentCardView);
            }

            void bind(ContentItem item) {
                titleTextView.setText(item.title);

                // Reset visibility
                contentTextView.setVisibility(View.GONE);
                contentImageView.setVisibility(View.GONE);

                // Set appropriate icon based on content type
                switch (item.type) {
                    case PDF:
                        contentTextView.setVisibility(View.VISIBLE);
                        contentTextView.setText("Click to open PDF");
                        break;
                    case IMAGE:
                        contentImageView.setVisibility(View.VISIBLE);
                        // Load image (use an image loading library like Glide if available)
                        // Glide.with(itemView.getContext()).load(item.content).into(contentImageView);
                        break;
                    case LINK:
                        contentTextView.setVisibility(View.VISIBLE);
                        contentTextView.setText("Click to open link");
                        break;
                    case TEXT:
                        contentTextView.setVisibility(View.VISIBLE);
                        contentTextView.setText(item.content);
                        break;
                }

                // Handle item clicks
                cardView.setOnClickListener(v -> {
                    if (item.type != ContentType.TEXT) {
                        openContent(item);
                    }
                });
            }

            private void openContent(ContentItem item) {
                if (item.type == ContentType.PDF) {
                    openPdf(item.content);
                } else if (item.type == ContentType.IMAGE) {
                    openImage(item.content);
                } else if (item.type == ContentType.LINK) {
                    openLink(item.content);
                }
            }

            private void openPdf(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                try {
                    itemView.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(itemView.getContext(), "No application to open PDF", Toast.LENGTH_SHORT).show();
                }
            }

            private void openImage(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                itemView.getContext().startActivity(intent);
            }

            private void openLink(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                itemView.getContext().startActivity(intent);
            }
        }
    }
}
