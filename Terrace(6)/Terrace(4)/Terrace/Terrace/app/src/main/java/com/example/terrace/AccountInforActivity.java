package com.example.terrace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.terrace.View.NewPassword;
import com.example.terrace.View.OrderListUserActivity;
import com.example.terrace.databinding.ActivityAccountInforBinding;
import com.example.terrace.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AccountInforActivity extends AppCompatActivity {

    private ActivityAccountInforBinding binding;
    private FirebaseFirestore db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountInforBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập nút quay lại
        binding.btnBackInformation.setOnClickListener(v -> finish());

        // Thiết lập sự kiện OnClick cho nút "Đơn hàng của tôi"
        binding.btnDonHangCuaToi.setOnClickListener(v -> {
            if (user != null) {
                Intent intent = new Intent(AccountInforActivity.this, OrderListUserActivity.class);
                intent.putExtra("username", user.getAccount()); // Truyền username vào Intent
                startActivity(intent);
            }
        });

        // Thiết lập sự kiện OnClick cho nút "Quên mật khẩu"
        binding.btnForgot.setOnClickListener(v -> {
            Intent intent = new Intent(AccountInforActivity.this, NewPassword.class);
            startActivity(intent); // Chuyển qua trang NewPasswordActivity
        });

        String account = getIntent().getStringExtra("name");
        if (account != null) {
            db = FirebaseFirestore.getInstance();
            db.collection("user").whereEqualTo("account", account)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                user = documentSnapshot.toObject(User.class);
                                binding.txtNameKH.setText(user.getAccount());
                                binding.txtGmailKH.setText(user.getMail());
                                binding.txtPhoneKH.setText(user.getPhone());
                            }
                        }
                    });
        }
    }
}
