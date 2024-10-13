package com.example.terrace.View;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class NewPassword extends AppCompatActivity {

    ImageButton btnBack;
    EditText edtEmail, edtNewPass, edtReenter;
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_password);

        // Find views
        btnBack = findViewById(R.id.btnBack);
        edtEmail = findViewById(R.id.edtEmail);
        edtNewPass = findViewById(R.id.edtNewPass);
        edtReenter = findViewById(R.id.edtReenter);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Set click listener for btnBack
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(NewPassword.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Set click listener for btnConfirm
        btnConfirm.setOnClickListener(view -> {
            String email = edtEmail.getText().toString().trim();
            String newPass = edtNewPass.getText().toString().trim();
            String reenterPass = edtReenter.getText().toString().trim();

            // Validate input
            if (email.isEmpty()) {
                edtEmail.setError("Vui lòng nhập email");
                return;
            }
            if (newPass.isEmpty()) {
                edtNewPass.setError("Vui lòng nhập mật khẩu mới");
                return;
            }
            if (reenterPass.isEmpty()) {
                edtReenter.setError("Vui lòng nhập lại mật khẩu mới");
                return;
            }

            if (!newPass.equals(reenterPass)) {
                Toast.makeText(this, "Mật khẩu xác nhận không trùng khớp", Toast.LENGTH_SHORT).show();
                return;
            }


            // Update password in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user")
                    .whereEqualTo("mail", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            String docId = task.getResult().getDocuments().get(0).getId();
                            db.collection("user").document(docId)
                                    .update("pass", newPass)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        // Quay lại LoginActivity sau khi cập nhật thành công
                                        Intent intent = new Intent(NewPassword.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Lỗi khi cập nhật mật khẩu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(this, "Không tìm thấy tài khoản với email này", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi tìm kiếm tài khoản: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
