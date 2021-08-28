package com.markopavicic.anketiranje.ui.groups;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.markopavicic.anketiranje.R;
import com.markopavicic.anketiranje.ui.groups.groupsRecycler.GroupsClickListener;
import com.markopavicic.anketiranje.ui.groups.groupsRecycler.GroupsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment implements GroupsClickListener {


    private RecyclerView groupsRecycler;
    private GroupsRecyclerAdapter groupsRecyclerAdapter;
    private List<String> userGroups;
    private List<String> userGroupCodes;
    private Button btnJoinGroup;
    private EditText etGroupCode;
    private Boolean check, checkIfUserAlreadyBelongsToGroup;
    private String groupName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnJoinGroup = view.findViewById(R.id.btnEnterGroup);
        etGroupCode = view.findViewById(R.id.etGroup);
        setupRecycler();
        setupRecyclerData();
        btnJoinGroup.setOnClickListener(v -> {
            check = false;
            checkIfUserAlreadyBelongsToGroup = false;
            String groupCode = etGroupCode.getText().toString().trim();
            if (groupCode.equals("")) {
                etGroupCode.setError(getString(R.string.errorEmptyFields));
                etGroupCode.requestFocus();
            } else {
                String pushId = FirebaseDatabase.getInstance().getReference("korisnici").push().getKey();
                FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot creatorsSnapshot : snapshot.child("grupe").getChildren()) {
                            for (DataSnapshot groupSnapshot : creatorsSnapshot.getChildren()) {
                                if (groupSnapshot.child("kod").getValue().toString().equals(groupCode)) {
                                    check = true;
                                    groupName = groupSnapshot.child("ime").getValue().toString();
                                }
                            }
                        }
                        if (check) {
                            for (int i = 0; i < userGroupCodes.size(); i++) {
                                if (groupCode.equals(userGroupCodes.get(i)))
                                    checkIfUserAlreadyBelongsToGroup = true;
                            }
                        } else {
                            Snackbar.make(getView(), "Nepostojeća grupa.", Snackbar.LENGTH_LONG).show();
                        }
                        if (checkIfUserAlreadyBelongsToGroup) {
                            Snackbar.make(getView(), "Već ste član te grupe.", Snackbar.LENGTH_LONG).show();
                        } else if (check) {
                            snapshot.getRef().child("korisnici").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pushId).child("ime").setValue(groupName).addOnCompleteListener(task -> {
                            });
                            snapshot.getRef().child("korisnici").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pushId).child("kod").setValue(groupCode).addOnCompleteListener(task -> {
                            });
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Uspješno ste se pridružili grupi", Snackbar.LENGTH_LONG).show();
                        }
                        etGroupCode.getText().clear();
                        setupRecyclerData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void setupRecycler() {
        groupsRecycler = getActivity().findViewById(R.id.groupsRecyclerView);
        groupsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupsRecyclerAdapter = new GroupsRecyclerAdapter(this);
        groupsRecycler.setAdapter(groupsRecyclerAdapter);
    }

    private void setupRecyclerData() {
        FirebaseDatabase.getInstance().getReference("korisnici").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userGroups = new ArrayList<>();
                        userGroupCodes = new ArrayList<>();
                        for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                            String groupName = groupSnapshot.child("ime").getValue().toString();
                            String groupCode = groupSnapshot.child("kod").getValue().toString();
                            if (!groupName.equals("") && !groupCode.equals("")) {
                                userGroups.add(groupSnapshot.child("ime").getValue().toString());
                                userGroupCodes.add(groupSnapshot.child("kod").getValue().toString());
                            }
                        }
                        groupsRecyclerAdapter.addData(userGroups);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void GroupClick(int position) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setTitle("Uklanjanje grupe");
        alertBuilder.setMessage("Želite li ukloniti ovu grupu?");
        alertBuilder.setPositiveButton("Da", (dialog, which) -> FirebaseDatabase.getInstance().getReference("korisnici").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    if (userGroupCodes.get(position).equals(groupSnapshot.child("kod").getValue().toString())) {
                        groupSnapshot.getRef().removeValue();
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Grupa uspješno uklonjena.", Snackbar.LENGTH_LONG).show();
                        setupRecyclerData();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        }));
        alertBuilder.setNegativeButton("Ne", (dialog, which) -> {
        });
        alertBuilder.show();
    }
}