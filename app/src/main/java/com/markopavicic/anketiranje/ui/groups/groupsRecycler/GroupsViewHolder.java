package com.markopavicic.anketiranje.ui.groups.groupsRecycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.markopavicic.anketiranje.R;

public class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final GroupsClickListener clickListener;
    private final TextView tvGroupName;

    public GroupsViewHolder(@NonNull View itemView, GroupsClickListener clickListener) {
        super(itemView);
        this.clickListener = clickListener;
        tvGroupName = itemView.findViewById(R.id.tvGroupRecyclerName);
        itemView.setOnClickListener(this);
    }

    public void setGroupName(String groupName) {
        tvGroupName.setText(groupName);
    }

    @Override
    public void onClick(View v) {
        clickListener.GroupClick(getAdapterPosition());
    }
}
