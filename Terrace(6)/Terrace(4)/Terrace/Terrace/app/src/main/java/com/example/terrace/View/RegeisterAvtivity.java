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

import com.example.terrace.model.User;
import com.example.terrace.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegeisterAvtivity extends AppCompatActivity {
    CardView cardLog;
    Button btnRes;
    ImageButton btnBack;
    EditText edtEmail, edtPass, edtAccount, edtPhone;
    String account, pass, email, phone;

    private FirebaseAuth auth; // Khai báo FirebaseAuth
    private FirebaseFirestore db; // Khai báo Firestore

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Khởi tạo FirebaseAuth và Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btnBack);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        edtAccount = findViewById(R.id.edtAccount);
        edtPhone = findViewById(R.id.edtPhone);

        cardLog = findViewById(R.id.cardLog);
        cardLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegeisterAvtivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btnRes = findViewById(R.id.btnRegister);
        btnRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void Register() {
        email = edtEmail.getText().toString();
        account = edtAccount.getText().toString();
        pass = edtPass.getText().toString();
        phone = edtPhone.getText().toString();

        // Kiểm tra các trường
        if (email.isEmpty() || account.isEmpty() || pass.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo tài khoản trên Firebase Authentication
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Nếu tạo tài khoản thành công, lưu thông tin vào Firestore
                        User user = new User(account, pass, email, "customer", phone);
                        saveUserToFirestore(user);
                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserToFirestore(User user) {
        db.collection("user")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentReference.update("userId", documentReference.getId());
                        Toast.makeText(RegeisterAvtivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegeisterAvtivity.this, MainActivity.class);
                        i.putExtra("name", user.getUserId());
                        startActivity(i);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
