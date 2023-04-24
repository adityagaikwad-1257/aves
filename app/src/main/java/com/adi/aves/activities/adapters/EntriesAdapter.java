package com.adi.aves.activities.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.aves.databinding.EntryViewholderBinding;
import com.adi.aves.models.PRResponse;

public class EntriesAdapter extends ListAdapter<PRResponse, EntriesAdapter.EntryViewHolder> {

    private static final DiffUtil.ItemCallback<PRResponse> DIFF_UTIL = new DiffUtil.ItemCallback<PRResponse>() {
        @Override
        public boolean areItemsTheSame(@NonNull PRResponse oldItem, @NonNull PRResponse newItem) {
            return oldItem.getEntry_id().equals(newItem.getEntry_id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PRResponse oldItem, @NonNull PRResponse newItem) {
            return true;
        }
    };

    public EntriesAdapter(){
        super(DIFF_UTIL);
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EntryViewHolder(EntryViewholderBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        PRResponse prResponse = getItem(position);
        Log.d("aditya", "onBindViewHolder: ");
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder{
        EntryViewholderBinding binding;
        public EntryViewHolder(EntryViewholderBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
