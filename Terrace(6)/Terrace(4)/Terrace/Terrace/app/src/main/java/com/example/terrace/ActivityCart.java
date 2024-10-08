package com.example.terrace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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
    float total;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        arr_Cart = new ArrayList<>();
        loadData();
        rvCart = findViewById(R.id.rvCart);
        btnBack = findViewById(R.id.btn_back3); // Ánh xạ nút Back

        cartAdapter = new CartAdapter(this, arr_Cart, new icCartClick() {
            @Override
            public void onCartClick(cart cart) {
                // Handle cart click here
            }
        });
        rvCart.setAdapter(cartAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvCart.setLayoutManager(staggeredGridLayoutManager);

        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        Intent i = getIntent();
        total = i.getFloatExtra("total", 0);
        txtTotalPrice.setText(String.valueOf(total));

        // Xử lý sự kiện khi nhấn nút Back
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityCart.this, MainActivity.class);
            startActivity(intent);
            finish();
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
                    for (QueryDocumentSnapshot document : snapshots) {
                        cart cart = document.toObject(cart.class);
                        arr_Cart.add(cart);
                    }
                    cartAdapter.notifyDataSetChanged();
                });
    }
}
