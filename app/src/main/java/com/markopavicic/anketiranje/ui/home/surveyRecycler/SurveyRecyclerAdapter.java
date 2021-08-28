package com.markopavicic.anketiranje.ui.home.surveyRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.markopavicic.anketiranje.R;

import java.util.ArrayList;
import java.util.List;

public class SurveyRecyclerAdapter extends RecyclerView.Adapter<SurveyViewHolder> {
    private final List<String> surveyNames = new ArrayList<>();
    private final List<String> surveyGroups = new ArrayList<>();
    private final SurveyClickListener clickListener;

    public SurveyRecyclerAdapter(SurveyClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public SurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cellView = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_recycler_row_layout, parent, false);
        return new SurveyViewHolder(cellView, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyViewHolder holder, int position) {
        holder.setSurveyName(surveyNames.get(position), surveyGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return surveyNames.size();
    }

    public void addData(List<String> surveyNames, List<String> surveyGroups) {
        this.surveyNames.clear();
        this.surveyNames.addAll(surveyNames);
        this.surveyGroups.clear();
        this.surveyGroups.addAll(surveyGroups);
        notifyDataSetChanged();
    }
}
