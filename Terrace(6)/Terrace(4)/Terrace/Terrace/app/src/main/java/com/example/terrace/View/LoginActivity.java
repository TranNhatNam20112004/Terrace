package com.example.terrace.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

        // Sự kiện cho nút Forgot Password
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent); // Chuyển qua ForgetPasswordActivity
            }
        });

        // Sự kiện cho nút Sign Up
        btnRes = findViewById(R.id.cardRes);
        btnRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegeisterAvtivity.class);
                startActivity(i);
            }
        });

        // Sự kiện cho nút Login
        btnLog = findViewById(R.id.btnLogin);
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckUser();
            }
        });

        // Sự kiện cho nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, Introduce.class);
                startActivity(i);
            }
        });
    }

    private void CheckUser() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        // Check if email or password is empty
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
