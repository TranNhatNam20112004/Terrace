package com.example.terrace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.terrace.Adapter.ProductAdapter;
import com.example.terrace.Interface.icDeleteDrinkClick;
import com.example.terrace.View.LoginActivity;
import com.example.terrace.databinding.ActivityAdminPageBinding;
import com.example.terrace.model.Drinks;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminPageActivity extends AppCompatActivity implements ProductAdapter.ProductOnClickListener {
    private ActivityAdminPageBinding binding;
    private ArrayList<Drinks> arr_Drinks;
    private ProductAdapter adapterProduct;
    private FirebaseFirestore db;
    private Button btnPromo;
    private ImageButton btnBack; // Khai báo btnBack
    private ImageButton btnLogout; // Khai báo btnLogout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdminPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupListeners();
        loadData();
    }

    private void initializeViews() {
        db = FirebaseFirestore.getInstance();
        arr_Drinks = new ArrayList<>();

        // Set up RecyclerView
        RecyclerView recyclerViewSP = binding.rvSP;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewSP.setLayoutManager(gridLayoutManager);

        // Initialize ProductAdapter
        adapterProduct = new ProductAdapter(this, arr_Drinks, this, this::deleteProduct);
        recyclerViewSP.setAdapter(adapterProduct);

        // Khởi tạo btnBack và btnLogout
        btnBack = binding.btnBack; // Liên kết với nút btnBack trong layout
        btnLogout = binding.btnLogout; // Liên kết với nút btnLogout trong layout
    }

    private void setupListeners() {
        // Sự kiện nhấn nút "Thêm Sản Phẩm"
        binding.btnThemSP.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPageActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        // Thiết lập sự kiện cho btnBack
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPageActivity.this, AdminActivity.class);
            startActivity(intent);
            finish(); // Kết thúc AdminPageActivity để không quay lại đây khi nhấn nút back của hệ thống
        });

        // Thiết lập sự kiện cho btnLogout
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPageActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Để clear stack
            startActivity(intent);
            finish(); // Kết thúc AdminPageActivity
        });
    }

    private void loadData() {
        db.collection("drinks").addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Log.w("Firestore", "Listen failed.", error);
                return;
            }
            arr_Drinks.clear();
            for (QueryDocumentSnapshot document : snapshots) {
                Drinks drinks = document.toObject(Drinks.class);
                arr_Drinks.add(drinks);
            }
            adapterProduct.notifyDataSetChanged();
        });
    }

    private void deleteProduct(Drinks drinks) {
        db.collection("drinks").whereEqualTo("name", drinks.getName()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    db.collection("drinks").document(document.getId()).delete().addOnSuccessListener(aVoid -> {
                        arr_Drinks.remove(drinks); // Remove from local list
                        adapterProduct.notifyDataSetChanged(); // Notify the adapter
                        Toast.makeText(this, "Sản phẩm đã được xóa", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Log.w("Firestore", "Error deleting document", e);
                        Toast.makeText(this, "Xóa sản phẩm thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                Toast.makeText(this, "Không tìm thấy sản phẩm để xóa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClickAtItem(int position) {
        Intent i = new Intent(AdminPageActivity.this, ProductDetailActivity.class);
        i.putExtra("name", arr_Drinks.get(position).getName());
        i.putExtra("image", arr_Drinks.get(position).getImage());
        i.putExtra("detail", arr_Drinks.get(position).getDetail());
        i.putExtra("price", arr_Drinks.get(position).getPrice());
        startActivity(i);
    }
}
