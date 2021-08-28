package com.markopavicic.anketiranje.surveyRecycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.markopavicic.anketiranje.R;

public class AnswersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final AnswersClickListener clickListener;
    private final TextView tvAnswer;

    public AnswersViewHolder(@NonNull View itemView, AnswersClickListener clickListener) {
        super(itemView);
        this.clickListener = clickListener;
        tvAnswer = itemView.findViewById(R.id.tvSurveyAnswer);
        itemView.setOnClickListener(this);
    }

    public void setAnswer(String answer) {
        tvAnswer.setText(answer);
    }

    @Override
    public void onClick(View v) {
        clickListener.AnswerClick(getAdapterPosition());
    }
}
