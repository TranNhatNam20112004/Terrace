package com.example.terrace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.terrace.databinding.ActivityEditProductBinding;
import com.example.terrace.model.Drinks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    private ActivityEditProductBinding binding;
    private FirebaseFirestore db;
    private Drinks drink;
    Button btnEdit, btnBackEdt;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnBackEdt = findViewById(R.id.btn_backEdt);
        btnEdit = findViewById(R.id.btnEdit);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("drink")) {
            drink = (Drinks) intent.getSerializableExtra("drink");

            // Hiển thị thông tin hiện tại của đồ uống lên các trường nhập liệu
            binding.edtTenSP.setText(drink.getName());
            binding.edtHinhSP.setText(drink.getImage());
            binding.edtMoTa.setText(drink.getDetail());
            binding.edtGiaSP.setText(String.valueOf(drink.getPrice()));
        }

        // Xử lý sự kiện nhấn nút edit
        binding.btnEdit.setOnClickListener(v -> saveProductToFirebase());
    }

    // Hàm lưu sản phẩm lên Firestore
    private void saveProductToFirebase() {
        String name = binding.edtTenSP.getText().toString();
        String image = binding.edtHinhSP.getText().toString();
        String detail = binding.edtMoTa.getText().toString();
        String price = binding.edtGiaSP.getText().toString();

        // Kiểm tra tính hợp lệ của dữ liệu đầu vào
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(image) || TextUtils.isEmpty(detail) || TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật lại đối tượng drink với dữ liệu mới
        drink.setName(name);
        drink.setImage(image);
        drink.setDetail(detail);
        drink.setPrice(Float.parseFloat(price));

        // Tạo một Map để chứa dữ liệu của sản phẩm
        Map<String, Object> drinkData = new HashMap<>();
        drinkData.put("name", drink.getName());
        drinkData.put("image", drink.getImage());
        drinkData.put("detail", drink.getDetail());
        drinkData.put("price", drink.getPrice());

        // Cập nhật dữ liệu lên Firestore
        db.collection("drinks").document(drink.getName()) // Bạn có thể sử dụng ID duy nhất khác cho mỗi sản phẩm
                .set(drinkData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProductActivity.this, "Sản phẩm đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                    // Trả về dữ liệu sau khi lưu thành công
                })
                .addOnFailureListener(e -> Toast.makeText(EditProductActivity.this, "Lỗi khi cập nhật sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
