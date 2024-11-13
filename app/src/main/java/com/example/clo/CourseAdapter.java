package com.example.clo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.MyCourseViewHolder> {
    private Context context;
    private List<String> courseList;
    private OnCourseClickListener courseClickListener;

    public interface OnCourseClickListener {
        void onCourseClick(String courseName, int position);
    }

    public CourseAdapter(Context context, List<String> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    public void setOnCourseClickListener(OnCourseClickListener listener) {
        this.courseClickListener = listener;
    }

    @NonNull
    @Override
    public MyCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_item_main, parent, false);
        return new MyCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCourseViewHolder holder, int position) {
        String courseName = courseList.get(position);
        holder.courseName.setText(courseName);

        String firstLetter = courseName.substring(0, 1).toUpperCase();
        holder.courseIcon.setText(firstLetter);

        int[] colors = context.getResources().getIntArray(R.array.course_colors);
        int colorIndex = position % colors.length;
        holder.courseIcon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colors[colorIndex]));

        holder.courseCard.setOnClickListener(v -> {
            Intent intent = new Intent(context, SubdivisionsActivity.class);
            intent.putExtra("categoryName", courseName);
            context.startActivity(intent);
            if (courseClickListener != null) {
                courseClickListener.onCourseClick(courseName, position);
            }
        });
        Log.d("CourseAdapter", "Binding course: " + courseName);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class MyCourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, courseIcon;
        CardView courseCard;

        public MyCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseNameTextView);
            courseIcon = itemView.findViewById(R.id.courseIconText);
            courseCard = itemView.findViewById(R.id.courseCard);
        }
    }
}