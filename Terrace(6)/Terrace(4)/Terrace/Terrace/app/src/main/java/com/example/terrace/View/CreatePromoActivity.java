package com.example.terrace.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.terrace.R;
import com.example.terrace.model.Promotion;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreatePromoActivity extends AppCompatActivity {

    EditText edtName, edtDiscount, edtStartDate, edtEndDate, edtQuantity;
    Button btnAdd, btn_back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_promo);

        edtName = findViewById(R.id.edtName);
        edtDiscount = findViewById(R.id.edtDiscount);
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnAdd = findViewById(R.id.btn_Add);
        btn_back = findViewById(R.id.btn_back);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Lấy dữ liệu từ EditText
                    String name = edtName.getText().toString();
                    int discount = Integer.parseInt(edtDiscount.getText().toString());
                    int quantity = Integer.parseInt(edtQuantity.getText().toString());

                    // Chuyển đổi ngày tháng từ String sang Timestamp
                    Timestamp startDay = convertStringToTimestamp(edtStartDate.getText().toString());
                    Timestamp endDay = convertStringToTimestamp(edtEndDate.getText().toString());

                    // Kiểm tra và tạo khuyến mãi
                    if (!name.isEmpty() && startDay != null && endDay != null) {
                        createPromo(name, discount, startDay, endDay, quantity);
                    } else {
                        Toast.makeText(CreatePromoActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(CreatePromoActivity.this, "Vui lòng nhập giá trị hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createPromo(String name, int discount, Timestamp startDate, Timestamp endDate, int quantity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tạo đối tượng Promotion
        Promotion promo = new Promotion(name, discount, startDate, endDate, quantity);

        // Thêm vào Firestore
        db.collection("promotion")
                .add(promo)  // Sử dụng phương thức add() để tự động tạo ID
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Khuyến mãi mới đã được tạo với ID: " + documentReference.getId());
                    Toast.makeText(CreatePromoActivity.this, "Khuyến mãi đã được thêm thành công!", Toast.LENGTH_SHORT).show();
                    finish();  // Đóng Activity sau khi thêm khuyến mãi
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Tạo khuyến mãi thất bại.", e);
                    Toast.makeText(CreatePromoActivity.this, "Tạo khuyến mãi thất bại!", Toast.LENGTH_SHORT).show();
                });
    }

    // Phương thức chuyển đổi String thành Timestamp
    private Timestamp convertStringToTimestamp(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateString);
            return new Timestamp(date);
        } catch (ParseException e) {
            Log.w("CreatePromoActivity", "Chuyển đổi ngày thất bại: " + e.getMessage());
            return null;
        }
    }
}
