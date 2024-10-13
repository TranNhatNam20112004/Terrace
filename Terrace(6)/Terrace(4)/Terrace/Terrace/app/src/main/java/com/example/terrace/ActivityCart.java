package com.example.terrace;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.terrace.View.MainActivity;
import com.example.terrace.model.cart;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class ActivityCart extends AppCompatActivity {

    RecyclerView rvCart;
    ArrayList<cart> arr_Cart;
    CartAdapter cartAdapter;
    TextView txtTotalPrice;
    float total = 0;
    Button btnBack, btnCheckout;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        Intent i = getIntent();
        arr_Cart = new ArrayList<>();
        loadData();
        rvCart = findViewById(R.id.rvCart);
        btnBack = findViewById(R.id.btn_back3); // Ánh xạ nút Back

        cartAdapter = new CartAdapter(this, arr_Cart, new icCartClick() {
            @Override
            public void onCartClick(cart cart) {
               //deleteCart(cart.getId());
            }
        });
        rvCart.setAdapter(cartAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvCart.setLayoutManager(staggeredGridLayoutManager);

        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtTotalPrice.setText(String.valueOf(total));

        // Xử lý sự kiện khi nhấn nút Back
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityCart.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityCart.this, PlaceOrderActivity.class);
                startActivity(intent);
            }
        });
    }

    private void deleteCart(String cartId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cart").document(cartId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ActivityCart.this, "Sản phẩm đã được xoa khoi giỏ hàng", Toast.LENGTH_SHORT).show();
                    loadData();  // Tải lại dữ liệu sau khi xóa
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ActivityCart.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void loadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cart")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        return;
                    }
                    arr_Cart.clear();
                    total = 0;  // Reset total to 0
                    for (QueryDocumentSnapshot document : snapshots) {
                        cart cart = document.toObject(cart.class);
                        arr_Cart.add(cart);
                        total += cart.getPrice();  // Add cart item's price
                    }
                    cartAdapter.notifyDataSetChanged();
                    txtTotalPrice.setText(String.valueOf(total));  // Update total price
                });
    }

}
