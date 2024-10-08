package com.example.terrace;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.terrace.Adapter.CartAdapter;
import com.example.terrace.Adapter.ProductAdapter;
import com.example.terrace.Interface.icCartClick;
import com.example.terrace.View.LoginActivity;
import com.example.terrace.View.MainActivity;
import com.example.terrace.databinding.ActivityAddProductBinding;
import com.example.terrace.databinding.ActivityAdminPageBinding;
import com.example.terrace.databinding.ActivityPlaceOrderBinding;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.cart;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PlaceOrderActivity extends AppCompatActivity {
    private ActivityPlaceOrderBinding binding;
    private ArrayList<cart> arr_Cart;
    private CartAdapter cartAdapter;
    private RecyclerView rvCart;
    float total;
    TextView txtTotalPrice;
    private FirebaseFirestore db;

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

        addControls();
    }

    private void addControls() {
        rvCart = findViewById(R.id.rvCart);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rvCart.setLayoutManager(gridLayoutManager);
        db = FirebaseFirestore.getInstance();

        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openDialog(Gravity.CENTER);
                Intent i = new Intent(PlaceOrderActivity.this, MainActivity.class);
                startActivity(i);

            }
        });

        arr_Cart = new ArrayList<>();
        loadData();
        cartAdapter = new CartAdapter(this, arr_Cart, new icCartClick() {
            @Override
            public void onCartClick(cart cart) {

            }
        });
        rvCart.setAdapter(cartAdapter);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        Intent i = getIntent();
        total = i.getFloatExtra("total", 0);
        txtTotalPrice.setText(String.valueOf(total));


    }

    private void loadData() {
        // Listen for real-time updates in the 'drinks' collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cart")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.w("Firestore", "Listen failed.", error);
                        return;
                    }

                    // Clear the list to avoid duplicates
                    arr_Cart.clear();

                    // Duyệt qua từng tài liệu (product) trong collection "drinks"
                    for (QueryDocumentSnapshot document : snapshots) {
                        // Chuyển đổi tài liệu thành đối tượng Drinks
                        cart cart = document.toObject(cart.class);
                        arr_Cart.add(cart);
                    }

                    // Notify adapter that data has changed
                    cartAdapter.notifyDataSetChanged();
                });
    }
        /*private void openDialog(int gravity){
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_order);

            Window window = dialog.getWindow();
            if (window == null){
                return;
            }
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //Xác định vị tri dialog
            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            windowAttributes.gravity = gravity;
            window.setAttributes(windowAttributes);

            if (Gravity.BOTTOM == gravity){
                dialog.setCancelable(true);
            }else dialog.setCancelable(false);

            Button btn_backHome = findViewById(R.id.btn_backHome);
            btn_backHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(PlaceOrderActivity.this, MainActivity.class);
                    startActivity(i);
                }
            });
            dialog.show();
        }*/
}