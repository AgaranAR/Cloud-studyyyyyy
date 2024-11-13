package com.example.clo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private final Context context;
    private OnCourseClickListener courseClickListener;
    private static List<String> selectedSubjects;
    private static List<String> courseList;

    public interface OnCourseClickListener {
        void onCourseClick(String courseName, int position);
        void onCourseOptionsClick(String courseName, int position, View anchor);
    }

    public CourseAdapter(Context context, List<String> courseList, List<String> selectedSubjects) {
        this.context = context;
        this.courseList = courseList;
        this.selectedSubjects = selectedSubjects != null ? selectedSubjects : new ArrayList<>();
    }

    public void setOnCourseClickListener(OnCourseClickListener listener) {
        this.courseClickListener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        String courseName = courseList.get(position);
        holder.courseName.setText(courseName);
        holder.checkBox.setChecked(selectedSubjects.contains(courseName));

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

        holder.optionsButton.setOnClickListener(v -> {
            if (courseClickListener != null) {
                courseClickListener.onCourseOptionsClick(courseName, position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static List<String> getSelectedSubjects() {
        return selectedSubjects;
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, courseIcon;
        CheckBox checkBox;
        ImageView optionsButton;
        CardView courseCard;
        LinearLayout courseLayout;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseNameTextView);
            courseIcon = itemView.findViewById(R.id.courseIconText);
            optionsButton = itemView.findViewById(R.id.courseOptionsButton);
            courseCard = itemView.findViewById(R.id.courseCard);
            checkBox = itemView.findViewById(R.id.checkbox);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String courseName = courseList.get(getAdapterPosition());
                if (isChecked) {
                    if (!selectedSubjects.contains(courseName)) {
                        selectedSubjects.add(courseName);
                    }
                } else {
                    selectedSubjects.remove(courseName);
                }
            });
        }
    }
}