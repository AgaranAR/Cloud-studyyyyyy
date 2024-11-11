package com.example.clo;  // Update this to match your package name

import android.content.Context;
import android.util.Log;
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
import java.util.Locale;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;

    public CourseAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameTextView; // Declare the TextView

        public CourseViewHolder(View itemView) {
            super(itemView);

            // Initialize the TextView
            courseNameTextView = itemView.findViewById(R.id.course_names); // Ensure this ID exists in item_course.xml
        }

        public void bind(Course course) {
            // Ensure courseNameTextView is not null before calling setText
            if (courseNameTextView != null) {
                courseNameTextView.setText(course.getName());
            } else {
                Log.e("CourseViewHolder", "courseNameTextView is null");
            }
        }
    }
}

