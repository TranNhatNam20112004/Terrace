package com.example.terrace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.terrace.Adapter.ProductAdapter;
import com.example.terrace.Interface.icDeleteDrinkClick;
import com.example.terrace.databinding.ActivityAdminPageBinding;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.Product;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminPageActivity extends AppCompatActivity implements ProductAdapter.ProductOnClickListener {
    private ActivityAdminPageBinding binding;
    //firebase auth
    private ArrayList<Drinks> arr_Drinks;
    private ProductAdapter adapterProduct;
    private RecyclerView recyclerViewSP;
    private FirebaseFirestore db;
    Button btnPromo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityAdminPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnThemSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPageActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });
        addControls();

        btnPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(AdminPageActivity.this, PromoActivity.class);
                startActivity(i);
            }
        });
    }
    private void filter(String text) {
        ArrayList<Drinks> filteredList = new ArrayList<>();
        for (Drinks item : arr_Drinks) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapterProduct.filterList(filteredList);
    }
    private void addControls() {
        recyclerViewSP = findViewById(R.id.rvSP);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewSP.setLayoutManager(gridLayoutManager);
        btnPromo =findViewById(R.id.btnPromo);
        db = FirebaseFirestore.getInstance();
        arr_Drinks = new ArrayList<>();

        loadData();
        adapterProduct = new ProductAdapter(this, arr_Drinks, this, new icDeleteDrinkClick() {
            @Override
            public void onDrinkClick(Drinks drinks) {
                deleteProduct(drinks);
            }
        });
        recyclerViewSP.setAdapter(adapterProduct);

    }
    private void deleteProduct(Drinks drinks) {
        // Get the Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the "drinks" collection to find the document where the name matches
        db.collection("drinks")
                .whereEqualTo("name", drinks.getName())  // Match the product by name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Delete the document
                            db.collection("drinks").document(document.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Product deleted successfully");
                                        Toast.makeText(this, "Sản phẩm đã được xóa", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error deleting document", e);
                                        Toast.makeText(this, "Xóa sản phẩm thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Log.d("Firestore", "No matching documents found");
                        Toast.makeText(this, "Không tìm thấy sản phẩm để xóa", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadData() {
        // Listen for real-time updates in the 'drinks' collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("drinks")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.w("Firestore", "Listen failed.", error);
                        return;
                    }
                    // Clear the list to avoid duplicates
                    arr_Drinks.clear();
                    // Duyệt qua từng tài liệu (product) trong collection "drinks"
                    for (QueryDocumentSnapshot document : snapshots) {
                        // Chuyển đổi tài liệu thành đối tượng Drinks
                        Drinks drinks = document.toObject(Drinks.class);
                        arr_Drinks.add(drinks);
                    }
                    // Notify adapter that data has changed
                    adapterProduct.notifyDataSetChanged();
                });
    }


    @Override
    public void onClickAtItem(int position) {
        Intent i = new Intent(AdminPageActivity.this, ProductDetailActivity.class);
        i.putExtra("name", arr_Drinks.get(position).getName());
        i.putExtra("image", arr_Drinks.get(position).getImage());
        i.putExtra("detail", arr_Drinks.get(position).getDetail());
        i.putExtra("price", arr_Drinks.get(position).getPrice());
        startActivity(i);
    }
}