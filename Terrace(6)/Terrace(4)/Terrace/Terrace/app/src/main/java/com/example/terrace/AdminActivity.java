package com.example.terrace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.terrace.View.LoginActivity;
import com.example.terrace.View.OrderListActivity;
import com.example.terrace.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Set nội dung của activity từ file XML layout
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Đặt padding tương ứng với hệ thống insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khi nhấn vào btnDrink, chuyển đến AdminPageActivity
        binding.btnDrink.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, AdminPageActivity.class);
            startActivity(intent);
        });

        // Khi nhấn vào btnPromotion, chuyển đến PromoActivity
        binding.btnPromotion.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, PromoActivity.class);
            startActivity(intent);
        });

        // Khi nhấn vào btnLogout, chuyển đến LoginActivity
        binding.btnLogout.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Khi nhấn vào btnOrderList, chuyển đến OrderListActivity
        binding.btnOrderList.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, OrderListActivity.class);
            startActivity(intent);
        });
    }
}
