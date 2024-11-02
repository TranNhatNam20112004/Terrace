package com.example.terrace;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.terrace.View.MainActivity;
import com.example.terrace.databinding.ActivityAccountInforBinding;
import com.example.terrace.databinding.ActivityEditProductBinding;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.Order;
import com.example.terrace.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AccountInforActivity extends AppCompatActivity {

    private ActivityAccountInforBinding binding;
    private FirebaseFirestore db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_infor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityAccountInforBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBackInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String account = getIntent().getStringExtra("name");
        if(account != null){
            db = FirebaseFirestore.getInstance();
            db.collection("user").whereEqualTo("account", account)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful() && !task.getResult().isEmpty()){
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