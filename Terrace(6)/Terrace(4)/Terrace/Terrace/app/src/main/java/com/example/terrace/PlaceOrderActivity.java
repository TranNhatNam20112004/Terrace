package com.example.terrace;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.terrace.Adapter.OrderSummaryAdapter;
import com.example.terrace.View.MainActivity;
import com.example.terrace.databinding.ActivityPlaceOrderBinding;
import com.example.terrace.model.cart;
import com.example.terrace.model.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.Intent;

public class PlaceOrderActivity extends AppCompatActivity {
    private ActivityPlaceOrderBinding binding;
    private ArrayList<cart> arr_orderSum;
    private OrderSummaryAdapter orderSumAdapter;
    private RecyclerView rvOrderSum;
    private FirebaseFirestore db;
    private String name;
    private float total = 0;
    private TextView txtTotalPrice;
    private Spinner spinnerPromoCode;
    private ArrayList<String> promoCodesList;
    private Map<String, Float> promoCodeDiscountMap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle b = getIntent().getExtras();
        if (b != null) {
            name = b.getString("name");
        }
        addControls();
    }

    private void addControls() {
        rvOrderSum = findViewById(R.id.rvOrderSummary);
        rvOrderSum.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        db = FirebaseFirestore.getInstance();
        spinnerPromoCode = findViewById(R.id.spinnerPromoCode);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);

        binding.btnbackOrder.setOnClickListener(v -> finish());
        binding.btnOrder.setOnClickListener(v -> placeOrder());

        arr_orderSum = new ArrayList<>();
        loadData();
        orderSumAdapter = new OrderSummaryAdapter(this, arr_orderSum);
        rvOrderSum.setAdapter(orderSumAdapter);

        txtTotalPrice.setText(String.valueOf(total));
        loadPromoCodes();

        // Gọi applyDiscount khi người dùng thay đổi lựa chọn mã giảm giá
        spinnerPromoCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyDiscount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần làm gì khi không có mã giảm giá được chọn
            }
        });
    }

    private void loadData() {
        db.collection("cart")
                .whereEqualTo("user", name)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        return;
                    }
                    arr_orderSum.clear();
                    total = 0;
                    for (QueryDocumentSnapshot document : snapshots) {
                        cart cart = document.toObject(cart.class);
                        arr_orderSum.add(cart);
                        total += cart.getPrice();
                    }
                    orderSumAdapter.notifyDataSetChanged();
                    txtTotalPrice.setText(String.valueOf(total));
                });
    }

    private void loadPromoCodes() {
        promoCodesList = new ArrayList<>();
        promoCodeDiscountMap = new HashMap<>();
        promoCodesList.add("Không có giảm giá");

        db.collection("promotion").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String promoName = document.getString("name");
                            Float discount = document.getDouble("discount").floatValue();
                            promoCodesList.add(promoName);
                            promoCodeDiscountMap.put(promoName, discount);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, promoCodesList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPromoCode.setAdapter(adapter);

                        // Gọi applyDiscount sau khi load mã giảm giá xong
                        applyDiscount();
                    } else {
                        Toast.makeText(this, "Lỗi khi lấy mã giảm giá", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyDiscount() {
        String selectedPromoCode = spinnerPromoCode.getSelectedItem() != null ? spinnerPromoCode.getSelectedItem().toString() : "";
        float discountPercentage = promoCodeDiscountMap.containsKey(selectedPromoCode) ? promoCodeDiscountMap.get(selectedPromoCode) : 0;

        float discountAmount = total * (discountPercentage / 100);
        float totalAmount = total - discountAmount;

        txtTotalPrice.setText(String.valueOf(totalAmount));
    }

    private void placeOrder() {
        String nameNH = binding.edtTenNH.getText().toString().trim();
        String phoneNH = binding.edtPhoneNH.getText().toString().trim();
        String addrNH = binding.edtAddrNH.getText().toString().trim();
        String orderNote = binding.edtNoteNH.getText().toString().trim();

        if (nameNH.isEmpty() || phoneNH.isEmpty() || addrNH.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNH.length() != 10) {
            Toast.makeText(this, "Số điện thoại không đúng định dạng", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("user").whereEqualTo("account", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot docSnap = task.getResult().getDocuments().get(0);
                        User u = docSnap.toObject(User.class);
                        String userId = u.getUserId();

                        String selectedPromoCode = spinnerPromoCode.getSelectedItem() != null ? spinnerPromoCode.getSelectedItem().toString() : "";
                        float discountPercentage = promoCodeDiscountMap.containsKey(selectedPromoCode) ? promoCodeDiscountMap.get(selectedPromoCode) : 0;

                        float discountAmount = total * (discountPercentage / 100);
                        float totalAmount = total - discountAmount;

                        Map<String, Object> order = new HashMap<>();
                        order.put("orderId", "");
                        order.put("orderdate", Timestamp.now());
                        order.put("paymethod", "Thanh toán khi nhận hàng");
                        order.put("phone", phoneNH);
                        order.put("address", addrNH);
                        order.put("status", "Chưa giải quyết");
                        order.put("totalamount", totalAmount);
                        order.put("userId", userId);
                        order.put("username", nameNH);
                        order.put("orderNote", orderNote);

                        db.collection("orders").add(order)
                                .addOnSuccessListener(documentReference -> {
                                    documentReference.update("orderId", documentReference.getId());
                                    Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();

                                    clearCartAndGoHome(); // Gọi hàm xóa giỏ hàng và trở về trang chủ
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    // Hàm xóa giỏ hàng và chuyển về trang chủ
    private void clearCartAndGoHome() {
        db.collection("cart")
                .whereEqualTo("user", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("cart").document(document.getId()).delete();
                        }
                        Toast.makeText(this, "Giỏ hàng đã được làm trống", Toast.LENGTH_SHORT).show();

                        // Chuyển về trang chủ
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi khi làm trống giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
