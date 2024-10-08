package com.example.terrace.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.terrace.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    ImageButton btnBack; // Khai báo nút Back
    EditText edtEmail; // Khai báo EditText để nhập email
    Button btnReset; // Khai báo nút Reset
    FirebaseAuth auth; // Khai báo FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        // Áp dụng edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Khởi tạo các thành phần UI
        btnBack = findViewById(R.id.btnBack);
        edtEmail = findViewById(R.id.edtEmail);
        btnReset = findViewById(R.id.btnReset);

        // Xử lý sự kiện nhấn nút btnBack
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Kết thúc ForgetPasswordActivity
        });

        // Xử lý sự kiện nhấn nút btnReset
        btnReset.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(ForgetPasswordActivity.this, "Vui lòng nhập địa chỉ email", Toast.LENGTH_SHORT).show();
                return;
            }
            sendResetEmail(email);
        });
    }

    private void sendResetEmail(String email) {
        // Kiểm tra nếu người dùng đã nhập email hay chưa
        if (email.isEmpty()) {
            Toast.makeText(ForgetPasswordActivity.this, "Vui lòng nhập địa chỉ email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi email khôi phục mật khẩu qua Firebase Authentication
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Thông báo email đã được gửi thành công
                        Toast.makeText(ForgetPasswordActivity.this, "Email khôi phục mật khẩu đã được gửi đến " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        // Nếu có lỗi xảy ra, in ra lỗi
                        Log.e("ResetEmailError", task.getException().getMessage());
                        Toast.makeText(ForgetPasswordActivity.this, "Có lỗi xảy ra: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}