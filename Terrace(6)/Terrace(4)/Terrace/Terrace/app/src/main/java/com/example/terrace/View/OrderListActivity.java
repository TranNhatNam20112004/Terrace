package com.example.terrace.View;

import android.os.Bundle;
import android.util.Log;
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

import com.example.terrace.Adapter.OrderAdapter;
import com.example.terrace.R;
import com.example.terrace.databinding.ActivityOrderListBinding;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.Order;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    private ActivityOrderListBinding binding;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private RecyclerView recyclerView;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_list);

        binding = ActivityOrderListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnBack = findViewById(R.id.btn_backor);

        // Xử lý khi nhấn nút quay lại
        btnBack.setOnClickListener(view -> finish());  // Đóng activity hiện tại và quay về trang trước

        recyclerView = findViewById(R.id.rcOrderList);
        orderList = new ArrayList<>();
        loadData();
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);
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
                    // Duyệt qua từng tài liệu trong collection "orders"
                    for (QueryDocumentSnapshot document : snapshots) {
                        Order order = document.toObject(Order.class);
                        orderList.add(order);
                        Log.d("Firestore", "Order retrieved: " + order.toString()); // Log thông tin order
                    }
                    orderAdapter.notifyDataSetChanged();
                    Log.d("Firestore", "Total orders retrieved: " + orderList.size()); // Log số lượng order
                });
    }
}
