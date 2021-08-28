package com.markopavicic.anketiranje.ui.groups.groupsRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.markopavicic.anketiranje.R;

import java.util.ArrayList;
import java.util.List;

public class GroupsRecyclerAdapter extends RecyclerView.Adapter<GroupsViewHolder> {
    private final List<String> groupNames = new ArrayList<>();
    private final GroupsClickListener clickListener;

    public GroupsRecyclerAdapter(GroupsClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cellView = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_recycler_row_layout, parent, false);
        return new GroupsViewHolder(cellView, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        holder.setGroupName(groupNames.get(position));
    }

    @Override
    public int getItemCount() {
        return groupNames.size();
    }

    public void addData(List<String> groupNames) {
        this.groupNames.clear();
        this.groupNames.addAll(groupNames);
        notifyDataSetChanged();
    }
}
