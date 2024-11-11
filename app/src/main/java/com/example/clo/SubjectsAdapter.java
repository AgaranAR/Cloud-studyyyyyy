package com.example.clo;

// SubjectsAdapter.java
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder> {

    private List<Subject> subjects;
    private Context context;

    public SubjectsAdapter(List<Subject> subjects, SubjectsActivity subjectsActivity) {
        this.subjects = subjects;
        this.context = context;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_card_item, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.subjectNameTextView.setText(subject.getName());
        holder.favoriteCheckBox.setChecked(subject.isFavorite());

        // Set click listener on item to navigate to CategoriesActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoriesActivity.class);
            intent.putExtra("subjectName", subject.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectNameTextView;
        CheckBox favoriteCheckBox;

        @SuppressLint("WrongViewCast")
        SubjectViewHolder(View itemView) {
            super(itemView);
            subjectNameTextView = itemView.findViewById(R.id.subjectNameText);
            favoriteCheckBox = itemView.findViewById(R.id.favButton);
        }
    }
}
