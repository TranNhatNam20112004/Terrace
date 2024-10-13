package com.example.terrace.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.terrace.AdminPageActivity;
import com.example.terrace.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    CardView btnRes;
    Button btnLog, btnForgot;
    ImageButton btnBack;
    EditText edtEmail, edtPass;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Find views
        btnBack = findViewById(R.id.btnBack);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        btnForgot = findViewById(R.id.btnForgot); // Nút Forgot Password

        // Disable login button initially
        btnLog = findViewById(R.id.btnLogin);
        btnLog.setEnabled(false); // Disable the button at start

        // TextWatcher để kiểm tra dữ liệu nhập vào
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInput(); // Validate input on text change
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        edtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInput(); // Validate input on text change
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Sự kiện cho nút Forgot Password
        btnForgot.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, NewPassword.class);
            startActivity(intent); // Chuyển qua ForgetPasswordActivity
        });

        // Sự kiện cho nút Sign Up
        btnRes = findViewById(R.id.cardRes);
        btnRes.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegeisterAvtivity.class);
            startActivity(i);
        });

        // Sự kiện cho nút Login
        btnLog.setOnClickListener(view -> {
            CheckUser();
        });

        // Sự kiện cho nút Back
        btnBack.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, Introduce.class);
            startActivity(i);
        });
    }

    private void validateInput() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        // Kiểm tra nếu email có đuôi @gmail.com và cả email và password không được rỗng
        boolean isEmailValid = email.endsWith("@gmail.com");
        boolean isInputValid = !email.isEmpty() && !pass.isEmpty() && isEmailValid;

        // Chỉ kích hoạt nút login nếu đầu vào hợp lệ
        btnLog.setEnabled(isInputValid);
    }

    private void CheckUser() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        // Kiểm tra nếu email hoặc password để trống
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email và mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra nếu email không có đuôi @gmail.com
        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(this, "Email phải có đuôi @gmail.com", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore for matching account and password
        db.collection("user")
                .whereEqualTo("mail", email)
                .whereEqualTo("pass", pass)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String role = document.getString("role"); // Lấy vai trò người dùng

                        // Chuyển hướng đến MainActivity hoặc AdminPageActivity dựa trên vai trò
                        if ("customer".equals(role)) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("name", document.getString("account"));
                            startActivity(intent);
                            finish();
                        } else if ("admin".equals(role)) {
                            Intent intent = new Intent(LoginActivity.this, AdminPageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Email hoặc mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lấy dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
