package com.example.terrace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.terrace.databinding.ActivityAddProductBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {
    private ActivityAddProductBinding binding;


    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng chờ");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //xu ly nhap chuot , bat dau up phan loai
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategoryFirebase();
            }
        });
    }
    private void addCategoryFirebase() {
        //hien thi
        progressDialog.setMessage("Thêm sản phẩm...");
        progressDialog.show();
        //Thiet lap vao db
        String TenSP = binding.edtTenSP.getText().toString().trim();
        String GiaSP = binding.edtGiaSP.getText().toString().trim();
        int g = Integer.parseInt(GiaSP);
        String MoTa = binding.edtMoTaSP.getText().toString().trim();
        String HinhAnh = binding.edtLinkSP.getText().toString().trim();
        // Setup data to save to database
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("MaSP", "");
        hashMap.put("detail", MoTa);
        hashMap.put("image", HinhAnh);
        hashMap.put("name", TenSP);
        hashMap.put("price", g);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("drinks")
                .add(hashMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentReference.update("MaSP",documentReference.getId());
                        progressDialog.dismiss();
                        Toast.makeText(AddProductActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddProductActivity.this, AdminPageActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
}