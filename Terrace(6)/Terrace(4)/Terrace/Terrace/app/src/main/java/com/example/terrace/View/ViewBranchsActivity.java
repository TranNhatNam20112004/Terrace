package com.example.terrace.View;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.terrace.Adapter.BranchAdapter;
import com.example.terrace.R;
import com.example.terrace.databinding.ActivityAddProductBinding;
import com.example.terrace.databinding.ActivityViewBranchsBinding;
import com.example.terrace.model.Branch;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewBranchsActivity extends AppCompatActivity {
    private ActivityViewBranchsBinding binding;
    private FirebaseFirestore db;
    private BranchAdapter branchAdapter;
    private List<Branch> branchList;
    private RecyclerView recyclerViewBranches;
    Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_branchs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityViewBranchsBinding.inflate(getLayoutInflater());
        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btn_backbr);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {finish();}
        });


        recyclerViewBranches = findViewById(R.id.rcBranch);
        branchList = new ArrayList<>();
        branchAdapter = new BranchAdapter(branchList);
        recyclerViewBranches.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBranches.setAdapter(branchAdapter);

        fetchBranchData();
    }

    private void fetchBranchData() {
        db.collection("cfshop")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getString("id");
                        String address = document.getString("address");
                        Branch branch = new Branch(id, address);
                        branchList.add(branch);
                    }
                    branchAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lấy dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}