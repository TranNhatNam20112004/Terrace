package com.example.terrace.View;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class UpdatePromoActivity extends AppCompatActivity {

    EditText edtName, edtDiscount, edtStartDate, edtEndDate, edtQuantity;
    Button btnUpdate, btn_back;
    FirebaseFirestore db;
    String promoId;  // ID của khuyến mãi cần cập nhật

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_promo);

        // Lấy ID khuyến mãi từ Intent
        Intent i = getIntent();
        promoId = i.getStringExtra("id");

        edtName = findViewById(R.id.edtName);
        edtDiscount = findViewById(R.id.edtDiscount);
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnUpdate = findViewById(R.id.btn_Update);
        btn_back = findViewById(R.id.btn_back);

        db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu khuyến mãi hiện tại từ Firestore
        loadPromoData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
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

                    // Kiểm tra và cập nhật khuyến mãi
                    if (!name.isEmpty() && startDay != null && endDay != null) {
                        updatePromo(name, discount, startDay, endDay, quantity);
                    } else {
                        Toast.makeText(UpdatePromoActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(UpdatePromoActivity.this, "Vui lòng nhập giá trị hợp lệ!", Toast.LENGTH_SHORT).show();
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

    // Hàm lấy dữ liệu khuyến mãi hiện tại từ Firestore
    private void loadPromoData() {
        if (promoId != null) {
            db.collection("promotion").document(promoId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Promotion promo = documentSnapshot.toObject(Promotion.class);
                            if (promo != null) {
                                edtName.setText(promo.getName());
                                edtDiscount.setText(String.valueOf(promo.getDiscount()));
                                edtStartDate.setText(convertTimestampToString(promo.getStart()));
                                edtEndDate.setText(convertTimestampToString(promo.getEnd()));
                                edtQuantity.setText(String.valueOf(promo.getQuantity()));
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.w("UpdatePromoActivity", "Lỗi khi tải dữ liệu khuyến mãi: ", e));
        }
    }

    // Hàm cập nhật khuyến mãi
    private void updatePromo(String name, int discount, Timestamp startDate, Timestamp endDate, int quantity) {
        if (promoId != null) {
            // Tạo đối tượng Promotion
            Promotion promo = new Promotion(name, discount, startDate, endDate, quantity);

            // Cập nhật vào Firestore
            db.collection("promotion").document(promoId)
                    .set(promo)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Khuyến mãi đã được cập nhật với ID: " + promoId);
                        Toast.makeText(UpdatePromoActivity.this, "Khuyến mãi đã được cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();  // Đóng Activity sau khi cập nhật khuyến mãi
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Cập nhật khuyến mãi thất bại.", e);
                        Toast.makeText(UpdatePromoActivity.this, "Cập nhật khuyến mãi thất bại!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Phương thức chuyển đổi String thành Timestamp
    private Timestamp convertStringToTimestamp(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateString);
            return new Timestamp(date);
        } catch (ParseException e) {
            Log.w("UpdatePromoActivity", "Chuyển đổi ngày thất bại: " + e.getMessage());
            return null;
        }
    }

    // Phương thức chuyển đổi Timestamp thành String
    private String convertTimestampToString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = timestamp.toDate();
        return dateFormat.format(date);
    }
}
