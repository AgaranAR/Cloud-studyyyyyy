package com.example.clo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private Context context;
    private List<String> subjects;
    private List<String> selectedSubjects;

    public SubjectAdapter(Context context, List<String> subjects, List<String> selectedSubjects) {
        this.context = context;
        this.subjects = subjects;
        this.selectedSubjects = selectedSubjects;
    }

    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout for each item
        View view = LayoutInflater.from(context).inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {
        String subject = subjects.get(position);
        holder.checkBox.setText(subject);
        holder.checkBox.setChecked(selectedSubjects.contains(subject));
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public List<String> getSelectedSubjects() {
        List<String> selectedSubjectsList = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            if (selectedSubjects.contains(subjects.get(i))) {
                selectedSubjectsList.add(subjects.get(i));
            }
        }
        return selectedSubjectsList;
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            // Find the CheckBox from the custom layout
            checkBox = itemView.findViewById(R.id.checkbox);

            // Set the listener for checking/unchecking
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String subject = checkBox.getText().toString();
                if (isChecked) {
                    selectedSubjects.add(subject);
                } else {
                    selectedSubjects.remove(subject);
                }
            });
        }
    }
}
