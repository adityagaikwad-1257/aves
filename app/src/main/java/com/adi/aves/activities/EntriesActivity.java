package com.adi.aves.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.adi.aves.activities.adapters.EntriesAdapter;
import com.adi.aves.databinding.ActivityEntriesBinding;
import com.adi.aves.models.PRResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EntriesActivity extends AppCompatActivity {

    ActivityEntriesBinding binding;
    EntriesAdapter entriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

        loadEntries();

    }

    private void loadEntries() {

        FirebaseDatabase.getInstance().getReference()
                .child("Entries")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<PRResponse> entryList = new ArrayList<>();

                        for (DataSnapshot entry: snapshot.getChildren()){
                            entryList.add(entry.getValue(PRResponse.class));
                            entryList.add(entry.getValue(PRResponse.class));
                        }

                        entriesAdapter.submitList(entryList);
                        Log.d("aditya", "onDataChange: " + entryList.size() + "\n" + entryList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initViews() {
        entriesAdapter = new EntriesAdapter();
        binding.entryRv.setLayoutManager(new LinearLayoutManager(this));
        binding.entryRv.setAdapter(entriesAdapter);
    }

}