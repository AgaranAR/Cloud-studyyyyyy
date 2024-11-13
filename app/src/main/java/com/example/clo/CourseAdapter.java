package com.example.clo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;

    // Constructor
    public CourseAdapter(Context context, List<Course> courseList) {
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
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String userId = (user != null) ? user.getUid() : "mockUserId";  // Use userId or "mockUserId" if user is null
                    deleteCourse(userId, course);  // Call deleteCourse with userId and course as arguments
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

    // Method to delete course by name from Firebase and update local list
    private void deleteCourse(String userId, Course course) {
        String courseName = course.getName();

        // Reference to the user's selectedSubjects node in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("selectedSubjects");

        // Find and remove the course from Firebase
        userRef.orderByValue().equalTo(courseName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();  // Remove course from Firebase
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("CourseAdapter", "Error removing course: " + databaseError.getMessage());
            }
        });

        // Remove the course from the local list and update RecyclerView
        courseList.remove(course);
        notifyDataSetChanged();
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
