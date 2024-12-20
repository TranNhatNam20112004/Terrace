package com.example.terrace.View;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.terrace.Adapter.OrderAdapter;
import com.example.terrace.R;
import com.example.terrace.databinding.ActivityOrderListBinding;
import com.example.terrace.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    private ActivityOrderListBinding binding;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        binding = ActivityOrderListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, true);  // Truyền true vào để chỉ định đây là trang admin
        binding.rcOrderList.setLayoutManager(new LinearLayoutManager(this));
        binding.rcOrderList.setAdapter(orderAdapter);

        loadData();

        binding.btnBackor.setOnClickListener(view -> finish());
    }

    private void loadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.w("Firestore", "Listen failed.", error);
                        return;
                    }
                    orderList.clear();
                    for (QueryDocumentSnapshot document : snapshots) {
                        Order order = document.toObject(Order.class);
                        orderList.add(order);
                    }
                    orderAdapter.notifyDataSetChanged();
                });
    }
}
