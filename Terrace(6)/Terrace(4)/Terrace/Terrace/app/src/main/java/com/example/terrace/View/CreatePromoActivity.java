package com.example.terrace.View;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreatePromoActivity extends AppCompatActivity {

    EditText edtName, edtDiscount, edtStartDate, edtEndDate, edtQuantity;
    Button btnAdd, btn_back;
    Calendar calendarStartDate, calendarEndDate;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_promo);

        // Ánh xạ các view
        edtName = findViewById(R.id.edtName);
        edtDiscount = findViewById(R.id.edtDiscount);
        edtStartDate = findViewById(R.id.edtStartDate);
        edtEndDate = findViewById(R.id.edtEndDate);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnAdd = findViewById(R.id.btn_Add);
        btn_back = findViewById(R.id.btn_back);

        calendarStartDate = Calendar.getInstance();
        calendarEndDate = Calendar.getInstance();

        edtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(calendarStartDate, edtStartDate);
            }
        });

        edtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(calendarEndDate, edtEndDate);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Lấy dữ liệu từ EditText
                    String name = edtName.getText().toString();
                    int discount = Integer.parseInt(edtDiscount.getText().toString());
                    int quantity = Integer.parseInt(edtQuantity.getText().toString());

                    // Kiểm tra % giảm giá nằm trong khoảng 0 đến 100
                    if (discount < 0 || discount > 100) {
                        Toast.makeText(CreatePromoActivity.this, "Giá trị giảm giá phải nằm trong khoảng từ 0% đến 100%", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Kiểm tra số lượng nằm trong khoảng 1 đến 1000
                    if (quantity < 1 || quantity > 1000) {
                        Toast.makeText(CreatePromoActivity.this, "Số lượng phải nằm trong khoảng từ 1 đến 1000", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Chuyển đổi ngày tháng từ String sang Timestamp
                    Timestamp startDay = convertStringToTimestamp(edtStartDate.getText().toString());
                    Timestamp endDay = convertStringToTimestamp(edtEndDate.getText().toString());

                    // Kiểm tra ngày bắt đầu và ngày kết thúc
                    if (startDay != null && endDay != null && startDay.toDate().after(endDay.toDate())) {
                        Toast.makeText(CreatePromoActivity.this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!", Toast.LENGTH_SHORT).show();
                        return;
                    }

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

    private void showDatePickerDialog(Calendar calendar, EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        editText.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void createPromo(String name, int discount, Timestamp startDate, Timestamp endDate, int quantity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tạo ID tự động
        String promoId = db.collection("promotion").document().getId();

        // Tạo đối tượng Promotion với ID mới
        Promotion promo = new Promotion(promoId, name, discount, startDate, endDate, quantity);

        // Thêm vào Firestore
        db.collection("promotion")
                .document(promoId)
                .set(promo)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Khuyến mãi mới đã được tạo với ID: " + promoId);
                    Toast.makeText(CreatePromoActivity.this, "Khuyến mãi đã được thêm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
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
