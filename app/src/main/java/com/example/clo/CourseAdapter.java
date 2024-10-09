package com.example.clo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> implements Filterable {

    private List<String> courseList;
    private List<String> courseListFull; // Full list for filtering
    private boolean nightMode; // To track the current mode

    public CourseAdapter(List<String> courseList) {
        this.courseList = courseList;
        this.courseListFull = new ArrayList<>(courseList); // Create a full list copy
        this.nightMode = nightMode; // Set initial mode
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        String currentCourse = courseList.get(position);
        holder.courseTextView.setText(currentCourse);

        // Set text and background color based on night mode
        if (nightMode) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.background_night));
            holder.courseTextView.setTextColor(holder.itemView.getResources().getColor(R.color.white));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.background_bright));
            holder.courseTextView.setTextColor(holder.itemView.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<String> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(courseListFull); // No filter applied
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (String course : courseListFull) {
                        if (course.toLowerCase().contains(filterPattern)) {
                            filteredList.add(course);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                courseList.clear();
                courseList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    // Method to update the night mode status
    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
        notifyDataSetChanged(); // Refresh the view
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView courseTextView;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
