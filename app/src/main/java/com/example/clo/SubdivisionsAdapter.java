package com.example.clo;
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




import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubdivisionsAdapter extends RecyclerView.Adapter<SubdivisionsAdapter.SubdivisionViewHolder> {

    private Context context;
    private List<String> subdivisionList;

    public SubdivisionsAdapter(Context context, List<String> subdivisionList) {
        this.context = context;
        this.subdivisionList = subdivisionList;
    }

    @NonNull
    @Override
    public SubdivisionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subdivision_item, parent, false);  // item_subdivision.xml should be defined
        return new SubdivisionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubdivisionViewHolder holder, int position) {
        String subdivisionName = subdivisionList.get(position);
        holder.subdivisionName.setText(subdivisionName);
    }

    @Override
    public int getItemCount() {
        return subdivisionList.size();
    }

    public static class SubdivisionViewHolder extends RecyclerView.ViewHolder {
        TextView subdivisionName;

        public SubdivisionViewHolder(View itemView) {
            super(itemView);
            subdivisionName = itemView.findViewById(R.id.subdivisionNameTextView);  // Assuming item_subdivision has this TextView
        }
    }
}
