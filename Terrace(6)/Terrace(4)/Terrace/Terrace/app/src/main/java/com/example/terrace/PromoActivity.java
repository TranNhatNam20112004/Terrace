package com.example.terrace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.terrace.Adapter.PromoAdapter;
import com.example.terrace.Interface.icPromoClick;
import com.example.terrace.Interface.icUsePromoClick;
import com.example.terrace.View.CreatePromoActivity;
import com.example.terrace.View.MainActivity;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.Promotion;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Locale;
public class PromoActivity extends AppCompatActivity {

    RecyclerView rvPromo;
    ArrayList<Promotion> arrPro;
    PromoAdapter promoAdapter;
    Button btnAdd, btnStop;
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
          public void onClick(Promotion promo) {
              deletePromo(promo);
          }
      }, new icUsePromoClick() {
          @Override
          public void onClick(Promotion promo) {
              applyPromo(promo);
          }
      });
        rvPromo.setAdapter(promoAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager=
                new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
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
    }
    private void InitUI(){
        rvPromo = findViewById(R.id.rvPromo);
        btnAdd   = findViewById(R.id.btnAddPromo);
        btnStop  = findViewById(R.id.btnStop);
    }
    private void deletePromo(Promotion promo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("promotion").document(promo.getName())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Khuyến mãi đã được xóa thành công: " + promo.getName());
                    Toast.makeText(PromoActivity.this, "Xoa khuyen mai thanh cong: ", Toast.LENGTH_SHORT).show();

                    arrPro.remove(promo);
                    promoAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Xóa khuyến mãi thất bại.", e);
                    Toast.makeText(PromoActivity.this, "Xoa khuyen mai thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                });
    }

    private void applyPromo(Promotion promo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("drinks").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Drinks drink = document.toObject(Drinks.class);
                            float oldPrice = drink.getPrice(); // Lấy giá gốc
                            drink.setOriginalPrice(oldPrice);
                            float newPrice = oldPrice - ((oldPrice*promo.getDiscount())/100);
                            // Lưu giá gốc vào drink (giả sử Drink có phương thức setOriginalPrice())

                            drink.setPrice(newPrice); // Cập nhật giá mới

                            // Cập nhật vào Firestore
                            db.collection("drinks").document(document.getId())
                                    .set(drink)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Giá của sản phẩm đã được cập nhật thành công cho: " + drink.getName());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Cập nhật giá sản phẩm thất bại cho: " + drink.getName(), e);
                                    });
                        }
                        // Xóa khuyến mãi sau khi áp dụng
                        Toast.makeText(PromoActivity.this, "Ap dung khuyen mai thanh cong: ", Toast.LENGTH_SHORT).show();
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

                            // Khôi phục giá gốc
                            float originalPrice = drink.getOriginalPrice(); // Giả sử Drink có phương thức getOriginalPrice()
                            drink.setPrice(originalPrice); // Khôi phục giá

                            // Cập nhật vào Firestore
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
                        // Có thể xóa khuyến mãi nếu cần thiết
                        // deletePromo(promo);
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

                        arrPro.add(promo);
                    }

                    // Thông báo cho adapter biết dữ liệu đã thay đổi
                    promoAdapter.notifyDataSetChanged();
                });
    }

}