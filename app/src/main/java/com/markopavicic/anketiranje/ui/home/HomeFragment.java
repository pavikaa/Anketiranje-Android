package com.markopavicic.anketiranje.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.markopavicic.anketiranje.R;
import com.markopavicic.anketiranje.SurveyPrepActivity;
import com.markopavicic.anketiranje.ui.home.surveyRecycler.SurveyClickListener;
import com.markopavicic.anketiranje.ui.home.surveyRecycler.SurveyRecyclerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements SurveyClickListener {

    private RecyclerView surveyRecycler;
    private SurveyRecyclerAdapter surveyRecyclerAdapter;
    private List<String> userGroups, userGroupNames;
    private ArrayList<String> solvedSurveysArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loadData();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecycler();
        setupRecyclerData();
    }

    private void setupRecycler() {
        surveyRecycler = getActivity().findViewById(R.id.surveyRecyclerView);
        surveyRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        surveyRecyclerAdapter = new SurveyRecyclerAdapter(this);
        surveyRecycler.setAdapter(surveyRecyclerAdapter);
    }

    private void setupRecyclerData() {
        userGroups = new ArrayList<>();
        userGroupNames = new ArrayList<>();
        List<String> surveyNames = new ArrayList<>();
        List<String> surveyGroups = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("korisnici").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                            userGroups.add(groupSnapshot.child("kod").getValue().toString());
                            userGroupNames.add(groupSnapshot.child("ime").getValue().toString());

                        }
                        FirebaseDatabase.getInstance().getReference("ankete")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot surveysSnapshot : snapshot.getChildren()) {

                                            for (DataSnapshot surveyGroupsSnapshot : surveysSnapshot.child("odabraneGrupe").getChildren()) {
                                                for (int i = 0; i < userGroups.size(); i++) {
                                                    if (surveyGroupsSnapshot.getValue().toString().equals(userGroups.get(i))) {
                                                        Boolean check;
                                                        check = true;
                                                        for (int j = 0; j < solvedSurveysArrayList.size(); j++) {
                                                            if (surveysSnapshot.getKey().equals(solvedSurveysArrayList.get(j))) {
                                                                check = false;
                                                            }
                                                        }
                                                        if (check) {
                                                            surveyNames.add("Anketa: " + surveysSnapshot.child("kod").getValue().toString());
                                                            surveyGroups.add("Grupa: " + userGroupNames.get(i));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        surveyRecyclerAdapter.addData(surveyNames, surveyGroups);
                                        if (surveyNames.size() == 0) {
                                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Nemate niti jednu anketu u svojim grupama", Snackbar.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void surveyClick(String id) {
        Intent i = new Intent(getContext(), SurveyPrepActivity.class);
        i.putExtra("surveyKey", id);
        i.putExtra("loggedIn", true);
        startActivity(i);
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("surveys", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        solvedSurveysArrayList = gson.fromJson(json, type);
        if (solvedSurveysArrayList == null) {
            solvedSurveysArrayList = new ArrayList<>();
            solvedSurveysArrayList.add("");
        }
    }
}