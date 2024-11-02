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
import com.example.terrace.Interface.icUpdateCartClick;
import com.example.terrace.View.LoginActivity;
import com.example.terrace.View.MainActivity;
import com.example.terrace.databinding.ActivityAddProductBinding;
import com.example.terrace.databinding.ActivityAdminPageBinding;
import com.example.terrace.databinding.ActivityPlaceOrderBinding;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.cart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity {
    private ActivityPlaceOrderBinding binding;
    private ArrayList<cart> arr_Cart;
    private CartAdapter cartAdapter;
    private RecyclerView rvCart;
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

        name = getIntent().getStringExtra("name");
        addControls();
    }

    private void addControls() {
        rvCart = findViewById(R.id.rvCart);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rvCart.setLayoutManager(gridLayoutManager);
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
                //openDialog(Gravity.CENTER);
                placeOrder();
            }
        });

        arr_Cart = new ArrayList<>();
        loadData();
        cartAdapter = new CartAdapter(this, arr_Cart, new icCartClick() {
            @Override
            public void onCartClick(cart cart) {

            }
        }, new icUpdateCartClick() {
            @Override
            public void onCartClick(cart cart) {

            }
        });
        rvCart.setAdapter(cartAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvCart.setLayoutManager(staggeredGridLayoutManager);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtTotalPrice.setText(String.valueOf(total));
    }

    private void loadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cart")
                .whereEqualTo("user", name)
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

    String userId ="";
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
        order.put("userId", "");
        order.put("username", nameNH);
        order.put("orderNote", orderNote);


        // Thêm tài liệu vào Firestore
        db.collection("orders").add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentReference.update("orderId",documentReference.getId());
                        Toast.makeText(PlaceOrderActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlaceOrderActivity.this, "Đặt hàng thất bại" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Intent intent = new Intent(PlaceOrderActivity.this, MainActivity.class);
        startActivity(intent);
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