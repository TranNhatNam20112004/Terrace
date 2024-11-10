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

import com.example.terrace.AccountInforActivity;
import com.example.terrace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewPassword extends AppCompatActivity {

    ImageButton btnBack;
    EditText edtPassCurrent, edtEmail, edtNewPass, edtReenter;
    Button btnConfirm;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_password);

        btnBack = findViewById(R.id.btnBack);
        edtPassCurrent = findViewById(R.id.edtPassCurrent);
        edtNewPass = findViewById(R.id.edtNewPass);
        edtReenter = findViewById(R.id.edtReenter);
        btnConfirm = findViewById(R.id.btnConfirm);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
//        userId = auth.getCurrentUser().getUid();

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(NewPassword.this, AccountInforActivity.class);
            startActivity(intent);
            finish();
        });

        btnConfirm.setOnClickListener(view -> {
            String currentPass = edtPassCurrent.getText().toString().trim();
            String newPass = edtNewPass.getText().toString().trim();
            String reenterPass = edtReenter.getText().toString().trim();

            if (currentPass.isEmpty()) {
                edtPassCurrent.setError("Vui lòng nhập mật khẩu hiện tại");
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

            db.collection("user").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String storedPass = documentSnapshot.getString("pass");
                        if (storedPass != null && storedPass.equals(currentPass)) {
                            db.collection("user").document(userId)
                                    .update("pass", newPass)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(NewPassword.this, AccountInforActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Lỗi khi cập nhật mật khẩu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi truy xuất mật khẩu hiện tại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}