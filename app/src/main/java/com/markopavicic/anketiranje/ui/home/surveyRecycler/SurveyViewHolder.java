package com.markopavicic.anketiranje.ui.home.surveyRecycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.markopavicic.anketiranje.R;

public class SurveyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final SurveyClickListener clickListener;
    private final TextView tvSurveyName, tvSurveyGroup;

    public SurveyViewHolder(@NonNull View itemView, SurveyClickListener clickListener) {
        super(itemView);
        this.clickListener = clickListener;
        tvSurveyName = itemView.findViewById(R.id.tvSurveyName);
        tvSurveyGroup = itemView.findViewById(R.id.tvGroupName);
        itemView.setOnClickListener(this);
    }

    public void setSurveyName(String surveyName, String surveyGroupName) {
        tvSurveyName.setText(surveyName);
        tvSurveyGroup.setText(surveyGroupName);
    }

    @Override
    public void onClick(View v) {
        clickListener.surveyClick(tvSurveyName.getText().toString().substring(tvSurveyName.getText().toString().length() - 6));
    }
}
