package com.example.terrace.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.terrace.R;
import com.example.terrace.model.Branch;

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {

    private List<Branch> branchList;

    // Constructor
    public BranchAdapter(List<Branch> branchList) {
        this.branchList = branchList;
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.branch_items, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        Branch branch = branchList.get(position);
        holder.txtMaCN.setText(branch.getId());
        holder.txtDC.setText(branch.getAddress());
    }

    @Override
    public int getItemCount() {
        return branchList.size();
    }

    // ViewHolder cho RecyclerView
    public static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView txtMaCN, txtDC;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMaCN = itemView.findViewById(R.id.txtMaCN);
            txtDC = itemView.findViewById(R.id.txtDC);
        }
    }
}
