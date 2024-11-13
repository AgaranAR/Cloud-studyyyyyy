package com.example.clo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class SubdivisionsAdapter extends RecyclerView.Adapter<SubdivisionsAdapter.SubdivisionViewHolder> {

    private Context context;
    private List<SubdivisionItem> subdivisionList;

    // Constructor
    public SubdivisionsAdapter(Context context, List<SubdivisionItem> subdivisionList) {
        this.context = context;
        this.subdivisionList = subdivisionList;
    }

    @NonNull
    @Override
    public SubdivisionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subdivision, parent, false);
        return new SubdivisionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubdivisionViewHolder holder, int position) {
        SubdivisionItem item = subdivisionList.get(position);

        // Check for null in item to avoid NullPointerException
        if (item != null && item.name != null) {
            holder.subdivisionName.setText(item.name);
        }

        // Handle item click to start DisplayFileActivity
        holder.itemView.setOnClickListener(v -> {
            String fileUrl = item.details != null ? item.details.get("fileUrl") : null;
            String fileType = "pdf"; // Assuming file type is always "pdf"

            if (fileUrl != null) { // Check if fileUrl exists before starting activity
                Intent intent = new Intent(context, DisplayFileActivity.class);
                intent.putExtra("fileUrl", fileUrl);
                intent.putExtra("fileType", fileType);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subdivisionList != null ? subdivisionList.size() : 0;
    }

    // ViewHolder class
    public static class SubdivisionViewHolder extends RecyclerView.ViewHolder {
        TextView subdivisionName;

        public SubdivisionViewHolder(View itemView) {
            super(itemView);
            // Ensure the TextView ID matches the one in item_subdivision.xml
            subdivisionName = itemView.findViewById(R.id.subdivisionNameTextView);
        }
    }
}

// SubdivisionItem class (example)

