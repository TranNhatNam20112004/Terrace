package com.example.terrace.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.terrace.AccountInforActivity;
import com.example.terrace.Adapter.OrderAdapter;
import com.example.terrace.databinding.ActivityOrderListUserBinding;
import com.example.terrace.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderListUserActivity extends AppCompatActivity {
    private ActivityOrderListUserBinding binding;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderListUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        username = getIntent().getStringExtra("username");

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, false); // Truyền false vào để chỉ định đây là trang người dùng
        binding.rcOrderList.setLayoutManager(new LinearLayoutManager(this));
        binding.rcOrderList.setAdapter(orderAdapter);

        loadData();

        // Xử lý sự kiện khi nhấn vào nút back
        binding.btnBack1.setOnClickListener(v -> {
            Intent intent = new Intent(OrderListUserActivity.this, AccountInforActivity.class);
            startActivity(intent);
            finish(); // Kết thúc Activity hiện tại
        });
    }

    private void loadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders")
                .whereEqualTo("username", username)
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
