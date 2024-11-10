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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewPassword extends AppCompatActivity {

    ImageButton btnBack;
    EditText edtPassCurrent, edtNewPass, edtReenter;
    Button btnConfirm;
    FirebaseUser user;
    FirebaseFirestore db;

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

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(NewPassword.this, AccountInforActivity.class);
            startActivity(intent);
            finish();
        });

        btnConfirm.setOnClickListener(view -> {
            String currentPass = edtPassCurrent.getText().toString().trim();
            String newPass = edtNewPass.getText().toString().trim();
            String reenterPass = edtReenter.getText().toString().trim();

            // Kiểm tra các trường nhập liệu
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

            // Xác thực lại người dùng với mật khẩu hiện tại
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);
            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        // Nếu xác thực thành công, cập nhật mật khẩu mới
                        user.updatePassword(newPass)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(NewPassword.this, AccountInforActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi cập nhật mật khẩu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                    });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
