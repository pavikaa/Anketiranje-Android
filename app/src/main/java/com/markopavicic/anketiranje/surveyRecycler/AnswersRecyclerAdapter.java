package com.markopavicic.anketiranje.surveyRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.markopavicic.anketiranje.R;

import java.util.ArrayList;
import java.util.List;

public class AnswersRecyclerAdapter extends RecyclerView.Adapter<AnswersViewHolder> {
    private final List<String> answerList = new ArrayList<>();
    private final AnswersClickListener clickListener;

    public AnswersRecyclerAdapter(AnswersClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public AnswersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cellView = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_row_layout, parent, false);
        return new AnswersViewHolder(cellView, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswersViewHolder holder, int position) {
        holder.setAnswer(answerList.get(position));
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public void addData(List<String> answerList) {
        this.answerList.clear();
        this.answerList.addAll(answerList);
        notifyDataSetChanged();
    }
}
