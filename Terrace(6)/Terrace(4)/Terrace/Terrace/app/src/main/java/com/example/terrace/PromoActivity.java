package com.example.terrace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.terrace.Adapter.PromoAdapter;
import com.example.terrace.Interface.icPromoClick;
import com.example.terrace.Interface.icUpdatePromoClick;
import com.example.terrace.Interface.icUsePromoClick;
import com.example.terrace.View.CreatePromoActivity;
import com.example.terrace.View.UpdatePromoActivity;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.Promotion;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PromoActivity extends AppCompatActivity {

    RecyclerView rvPromo;
    ArrayList<Promotion> arrPro;
    PromoAdapter promoAdapter;
    Button btnAdd, btnStop, btnBack; // Thêm btnBack vào đây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_promo);
        InitUI();

        arrPro = new ArrayList<>();
        loadData();

        promoAdapter = new PromoAdapter(this, arrPro, new icPromoClick() {
            @Override
            public void onClick(Promotion promo)
            {
                deletePromo(promo);
            }
        }, new icUsePromoClick() {
            @Override
            public void onClick(Promotion promo) {
                //applyPromo(promo);
            }
        }, new icUpdatePromoClick() {
            @Override
            public void onClick(Promotion promo) {
                Intent i = new Intent(PromoActivity.this, UpdatePromoActivity.class);
                i.putExtra("id", promo.getId());
                startActivity(i);
            }
        });

        rvPromo.setAdapter(promoAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvPromo.setLayoutManager(staggeredGridLayoutManager);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PromoActivity.this, CreatePromoActivity.class);
                startActivity(i);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopApplyingPromo();
            }
        });
        // Thêm sự kiện click cho btnBack
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Kết thúc activity hiện tại để quay về AdminPageActivity
            }
        });
    }

    private void InitUI() {
        rvPromo = findViewById(R.id.rvPromo);
        btnAdd = findViewById(R.id.btnAdd);
        btnStop = findViewById(R.id.btnStop);
        btnBack = findViewById(R.id.btnBack); // Khai báo btnBack
    }

    private void deletePromo(Promotion promo) {
        Log.d("PromoActivity", "Đang xóa khuyến mãi: " + promo.getName());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Xóa document với ID của khuyến mãi
        db.collection("promotion").document(promo.getId()) // Sử dụng ID của document
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Khuyến mãi đã được xóa thành công: " + promo.getName());
                    Toast.makeText(PromoActivity.this, "Xóa khuyến mãi thành công: " + promo.getName(), Toast.LENGTH_SHORT).show();
                    arrPro.remove(promo);
                    promoAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Xóa khuyến mãi thất bại: " + e.getMessage(), e);
                    Toast.makeText(PromoActivity.this, "Xóa khuyến mãi thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void applyPromo(Promotion promo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("drinks").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Drinks drink = document.toObject(Drinks.class);
                            float oldPrice = drink.getPrice();
                            drink.setOriginalPrice(oldPrice);
                            float newPrice = oldPrice - ((oldPrice * promo.getDiscount()) / 100);

                            drink.setPrice(newPrice);

                            db.collection("drinks").document(document.getId())
                                    .set(drink)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Giá của sản phẩm đã được cập nhật thành công cho: " + drink.getName());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Cập nhật giá sản phẩm thất bại cho: " + drink.getName(), e);
                                    });
                        }
                        Toast.makeText(PromoActivity.this, "Áp dụng khuyến mãi thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("Firestore", "Lấy danh sách sản phẩm thất bại.", task.getException());
                    }
                });
    }

    private void stopApplyingPromo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("drinks").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Drinks drink = document.toObject(Drinks.class);
                            float originalPrice = drink.getOriginalPrice();
                            drink.setPrice(originalPrice);

                            db.collection("drinks").document(document.getId())
                                    .set(drink)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Giá của sản phẩm đã được khôi phục thành công cho: " + drink.getName());
                                        Toast.makeText(PromoActivity.this, "Giá của sản phẩm đã được khôi phục thành công", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Khôi phục giá sản phẩm thất bại cho: " + drink.getName(), e);
                                    });
                        }
                    } else {
                        Log.w("Firestore", "Lấy danh sách sản phẩm thất bại.", task.getException());
                    }
                });
    }

    private void loadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("promotion")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.w("Firestore", "Listen failed.", error);
                        return;
                    }

                    arrPro.clear();
                    for (QueryDocumentSnapshot document : snapshots) {
                        Promotion promo = document.toObject(Promotion.class);
                        promo.setId(document.getId()); // Lưu ID của document
                        arrPro.add(promo);
                    }
                    promoAdapter.notifyDataSetChanged();
                });
    }
}