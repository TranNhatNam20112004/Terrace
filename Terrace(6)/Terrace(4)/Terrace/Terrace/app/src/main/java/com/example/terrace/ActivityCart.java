package com.example.terrace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.terrace.Adapter.CartAdapter;
import com.example.terrace.Interface.icCartClick;
import com.example.terrace.Interface.icUpdateCartClick;
import com.example.terrace.View.MainActivity;
import com.example.terrace.model.cart;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Locale;

public class ActivityCart extends AppCompatActivity {

    RecyclerView rvCart;
    ArrayList<cart> arr_Cart;
    CartAdapter cartAdapter;
    TextView txtTotalPrice;
    float total = 0;
    Button btnBack, btnCheckout;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        Intent i = getIntent();
        name = i.getStringExtra("name");

        arr_Cart = new ArrayList<>();

        rvCart = findViewById(R.id.rvCart);
        btnBack = findViewById(R.id.btn_back3);
        btnCheckout = findViewById(R.id.btnCheckout);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);

        cartAdapter = new CartAdapter(this, arr_Cart, new icCartClick() {
            @Override
            public void onCartClick(cart cart) {
                deleteCart(cart.getId());
            }
        }, new icUpdateCartClick() {
            @Override
            public void onCartClick(cart cart) {
                updateCart(cart);
            }
        });

        rvCart.setAdapter(cartAdapter);
        rvCart.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        loadData();

        btnBack.setOnClickListener(v -> finish()); // Chỉ quay lại mà không tạo Intent mới

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityCart.this, PlaceOrderActivity.class);
            startActivity(intent);
        });
    }

    private void deleteCart(String cartId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cart").document(cartId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ActivityCart.this, "Sản phẩm đã được xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                    loadData();  // Tải lại dữ liệu sau khi xóa
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ActivityCart.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cart")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(ActivityCart.this, "Lỗi khi tải giỏ hàng", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    arr_Cart.clear();
                    total = 0;
                    for (QueryDocumentSnapshot document : snapshots) {
                        cart cart = document.toObject(cart.class);
                        if(cart.getUser().equals(name)){
                            arr_Cart.add(cart);
                            total += cart.getPrice();
                        }
                    }
                    cartAdapter.notifyDataSetChanged();

                    // Định dạng tổng giá với 2 chữ số thập phân
                    String formattedTotal = String.format(Locale.getDefault(), "%.2f", total);
                    txtTotalPrice.setText(formattedTotal);
                });
    }
    public void updateCart(cart cart) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Tìm kiếm sản phẩm theo tên và số bàn
        db.collection("cart")
                .whereEqualTo("name", cart.getName())
                .whereEqualTo("user", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("cart")
                                    .document(document.getId())
                                    .set(cart)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Product updated successfully!");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error updating product", e);
                                    });
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }
}
