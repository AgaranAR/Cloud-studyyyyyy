package com.example.clo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;

    // Constructor
    public CourseAdapter(Context context ,List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseName.setText(course.getName());

        // Handle the 3 dots menu for each course
        holder.menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.course_menu);  // Inflate the menu
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete) {
                    // Handle delete action here
                    // This is where you would call a method to delete the course
                    deleteCourse(position);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    // Method to delete course (you can update this to perform the deletion from the database)
    private void deleteCourse(int position) {
        courseList.remove(position);
        notifyItemRemoved(position);  // Notify RecyclerView that an item was removed
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {

        TextView courseName;
        View menuButton;

        public CourseViewHolder(View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            menuButton = itemView.findViewById(R.id.menu_button);  // Assuming you have a 3-dot button
        }
    }
}
