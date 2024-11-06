package com.example.terrace;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.terrace.Adapter.CartAdapter;
import com.example.terrace.Adapter.OrderSummaryAdapter;
import com.example.terrace.Adapter.ProductAdapter;
import com.example.terrace.Interface.icCartClick;
import com.example.terrace.Interface.icUpdateCartClick;
import com.example.terrace.View.LoginActivity;
import com.example.terrace.View.MainActivity;
import com.example.terrace.databinding.ActivityAddProductBinding;
import com.example.terrace.databinding.ActivityAdminPageBinding;
import com.example.terrace.databinding.ActivityPlaceOrderBinding;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.User;
import com.example.terrace.model.cart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity {
    private ActivityPlaceOrderBinding binding;
    private ArrayList<cart> arr_orderSum;
    private OrderSummaryAdapter orderSumAdapter;
    private RecyclerView rvOrderSum;
    float total = 0;
    TextView txtTotalPrice;
    private FirebaseFirestore db;
    private String name;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_place_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle b = getIntent().getExtras();
        if (b != null){
            name = b.getString("name");
        }
        addControls();
    }

    private void addControls() {
        rvOrderSum = findViewById(R.id.rvOrderSummary);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rvOrderSum.setLayoutManager(gridLayoutManager);
        db = FirebaseFirestore.getInstance();

        binding.btnbackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });

        arr_orderSum = new ArrayList<>();
        loadData();
        orderSumAdapter = new OrderSummaryAdapter(this, arr_orderSum);
        rvOrderSum.setAdapter(orderSumAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvOrderSum.setLayoutManager(staggeredGridLayoutManager);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtTotalPrice.setText(String.valueOf(total));
    }

    private void loadData() {
        db = FirebaseFirestore.getInstance();
        db.collection("cart")
                .whereEqualTo("user", name)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        return;
                    }
                    arr_orderSum.clear();
                    total = 0;  // Reset total to 0
                    for (QueryDocumentSnapshot document : snapshots) {
                        cart cart = document.toObject(cart.class);
                        arr_orderSum.add(cart);
                        total += cart.getPrice();  // Add cart item's price
                    }
                    orderSumAdapter.notifyDataSetChanged();
                    txtTotalPrice.setText(String.valueOf(total));  // Update total price
                });
    }

    String userId = "", drinkId = "";

    private void placeOrder() {
        // Lấy thông tin từ các EditText
        String nameNH = binding.edtTenNH.getText().toString().trim();
        String phoneNH = binding.edtPhoneNH.getText().toString().trim();
        String addrNH = binding.edtAddrNH.getText().toString().trim();
        String orderNote = binding.edtNoteNH.getText().toString().trim();

        // Kiểm tra dữ liệu nhập
        if (nameNH.isEmpty() || phoneNH.isEmpty() || addrNH.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNH.length() != 10) {
            Toast.makeText(this, "Số điện thoại không đúng định dạng", Toast.LENGTH_SHORT).show();
            return;
        }

        //lấy userId
        db.collection("user").whereEqualTo("account", name)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot docSnap = task.getResult().getDocuments().get(0);
                            User u = docSnap.toObject(User.class);
                            userId = u.getUserId();
                        }
                    }
                });

        // Lấy tổng tiền từ TextView
        String totalPrice = binding.txtTotalPrice.getText().toString().trim();
        float totalamount;
        try {
            totalamount = Float.parseFloat(totalPrice);
        } catch (NumberFormatException e) {
            totalamount = 0;
        }

        // Lấy phương thức thanh toán
        String paymentMethod = "Thanh toán khi nhận hàng";

        // Tạo Order object
        Map<String, Object> order = new HashMap<>();
        order.put("orderId", "");
        order.put("orderdate", Timestamp.now());
        order.put("paymethod", paymentMethod);
        order.put("phone", phoneNH);
        order.put("address", addrNH);
        order.put("status", "Chưa giải quyết");
        order.put("totalamount", totalamount);
        order.put("userId", userId);
        order.put("username", nameNH);
        order.put("orderNote", orderNote);

        // Thêm vào orders
        db.collection("orders").add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentReference.update("orderId", documentReference.getId());
                        Toast.makeText(PlaceOrderActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();

                        //thêm vào orderdetail
                        db.collection("cart")
                                .whereEqualTo("user", name)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        Map<String, Object> productList = new HashMap<>();
                                        int count = 1;

                                        for (DocumentSnapshot docSnap : task.getResult()) {
                                            String nameC = docSnap.getString("name");
                                            int quantityC = docSnap.getLong("quantity").intValue();
                                            float priceC = docSnap.getLong("price").floatValue();

                                            // Tạo một bản đồ con cho từng sản phẩm
                                            Map<String, Object> productDetails = new HashMap<>();
                                            productDetails.put("dname", nameC);
                                            productDetails.put("quantity", quantityC);
                                            productDetails.put("price", priceC);

                                            // Thêm bản đồ con vào danh sách sản phẩm với khóa duy nhất (ví dụ: SP1, SP2)
                                            productList.put("Pro" + count, productDetails);
                                            count++;
                                        }

                                        // Thêm chi tiết đơn hàng vào collection orderdetail
                                        Map<String, Object> orderDetail = new HashMap<>();
                                        orderDetail.put("orderId", documentReference.getId()); // ID đơn hàng
                                        orderDetail.put("ProductList", productList); // Danh sách sản phẩm

                                        db.collection("orderdetail").add(orderDetail)
                                                .addOnSuccessListener(docRefOT -> {
                                                    // Cập nhật ID cho orderdetail
                                                    docRefOT.update("id", docRefOT.getId())
                                                            .addOnSuccessListener(aVoid -> Log.d("Success", "Đã thêm chi tiết đơn hàng thành công"))
                                                            .addOnFailureListener(e -> Log.d("Error", "Cập nhật ID thất bại: " + e.getMessage()));
                                                })
                                                .addOnFailureListener(e -> Log.d("Error", "Thêm chi tiết đơn hàng thất bại: " + e.getMessage()));
                                    } else {
                                        Log.d("Error", "Không tìm thấy sản phẩm trong giỏ hàng hoặc lỗi: " + task.getException());
                                    }
                                });

                        // Xóa sản phẩm trong giỏ hàng
                        db.collection("cart").whereEqualTo("user", name)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                document.getReference().delete();
                                            }
                                        }
                                    }
                                });
                        Intent intent = new Intent(PlaceOrderActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlaceOrderActivity.this, "Đặt hàng thất bại" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
