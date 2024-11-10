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

import com.example.terrace.AdminActivity;
import com.example.terrace.View.MainActivity;
import com.example.terrace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    Button btnLog, btnForgot;
    ImageButton btnBack;
    EditText edtEmail, edtPass;

    private FirebaseAuth auth; // FirebaseAuth

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Tìm các view
        btnBack = findViewById(R.id.btnBack);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);

        btnLog = findViewById(R.id.btnLogin);
        btnLog.setEnabled(false); // Vô hiệu hóa nút đăng nhập lúc đầu

        // TextWatcher để kiểm tra dữ liệu nhập vào
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInput(); // Kiểm tra đầu vào khi thay đổi
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        edtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInput(); // Kiểm tra đầu vào khi thay đổi
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Sự kiện cho nút Đăng ký
        findViewById(R.id.cardRes).setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegeisterAvtivity.class);
            startActivity(i);
        });

        // Sự kiện cho nút Đăng nhập
        btnLog.setOnClickListener(view -> authenticateUser());

        // Sự kiện cho nút Quay lại
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

        // Chỉ kích hoạt nút đăng nhập nếu đầu vào hợp lệ
        btnLog.setEnabled(isInputValid);
    }

    private void authenticateUser() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        // Kiểm tra nếu email hoặc mật khẩu để trống
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email và mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đăng nhập với Firebase Authentication
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sau khi đăng nhập thành công, kiểm tra vai trò người dùng trong Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("user")
                                .whereEqualTo("mail", email)
                                .get()
                                .addOnCompleteListener(queryTask -> {
                                    if (queryTask.isSuccessful() && !queryTask.getResult().isEmpty()) {
                                        DocumentSnapshot document = queryTask.getResult().getDocuments().get(0);
                                        String role = document.getString("role"); // Lấy vai trò người dùng

                                        // Chuyển hướng dựa trên vai trò
                                        if ("customer".equals(role)) {
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.putExtra("name", document.getString("account"));
                                            startActivity(intent);
                                            finish();
                                        } else if ("admin".equals(role)) {
                                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(this, "Không tìm thấy tài khoản trong Firestore", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi đăng nhập: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
