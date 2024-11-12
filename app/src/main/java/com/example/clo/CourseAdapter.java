package com.example.clo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clo.SubdivisionsActivity;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<String> courseList;

    public CourseAdapter(Context context, List<String> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (course_item.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        String category = courseList.get(position);
        holder.categoryName.setText(category);  // Set the category name in the TextView

        // Set a click listener for each item in the RecyclerView
        holder.itemView.setOnClickListener(v -> {
            // Create an intent to navigate to the SubdivisionsActivity
            Intent intent = new Intent(context, SubdivisionsActivity.class);
            intent.putExtra("categoryName", category);  // Pass the category name to the next activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();  // Return the size of the course list
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CourseViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.courseNameTextView);  // Ensure the TextView is correctly initialized with the ID from the layout
        }
    }
}
