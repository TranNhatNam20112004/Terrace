package com.example.terrace.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.terrace.ActivityCart;
import com.example.terrace.Adapter.SanPhamAdapter;
import com.example.terrace.Interface.icDrinkClick;
import com.example.terrace.R;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.cart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerViewSP;
    SanPhamAdapter sanPhamAdapter;
    ArrayList<Drinks> arr_Drinks; // Danh sách sản phẩm hiện tại
    ArrayList<Drinks> arr_DrinksFull; // Danh sách sản phẩm đầy đủ
    ImageButton btnLogout, btnCart;
    TextView txtName;
    SearchView searchView; // Khai báo SearchView
    float totalPrice = 0; // Giá trị tổng cho giỏ hàng
    String name;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        addControls();
        loadData();

        // Điều chỉnh padding khi có system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Xử lý sự kiện khi chọn một mục
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Toast.makeText(MainActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_gift) {
                Toast.makeText(MainActivity.this, "Gift selected", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_list) {
                Toast.makeText(MainActivity.this, "List selected", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Sự kiện logout
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        });

        // Gán username
        txtName = findViewById(R.id.txtName);
        Intent i = getIntent();
        name = i.getStringExtra("name");
        txtName.setText(name);

        // Gán sự kiện cho giỏ hàng
        btnCart = findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> {
            Intent i1 = new Intent(MainActivity.this, ActivityCart.class);
            startActivity(i1);
        });

        // Gán sự kiện cho SearchView
        searchView = findViewById(R.id.Search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Khi xóa tìm kiếm, cập nhật lại danh sách sản phẩm từ arr_DrinksFull
                    sanPhamAdapter.updateList(arr_DrinksFull);
                } else {
                    filter(newText); // Gọi hàm lọc khi người dùng nhập
                }
                return true;
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
                    // Cập nhật danh sách đầy đủ
                    arr_DrinksFull.clear();
                    arr_DrinksFull.addAll(arr_Drinks);

                    // Notify adapter that data has changed
                    sanPhamAdapter.notifyDataSetChanged();
                });
    }

    private void addControls() {
        recyclerViewSP = findViewById(R.id.recyclerSanPham);
        arr_Drinks = new ArrayList<>();
        arr_DrinksFull = new ArrayList<>(); // Khởi tạo danh sách đầy đủ
        sanPhamAdapter = new SanPhamAdapter(this, arr_Drinks, drinks -> {
            AddProduct(drinks);
        });
        recyclerViewSP.setAdapter(sanPhamAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewSP.setLayoutManager(staggeredGridLayoutManager);
    }

    private void AddProduct(Drinks drinks) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String cartItemId = db.collection("cart").document().getId();  // Firestore will generate a unique ID
        // Khởi tạo đối tượng cart với ID vừa tạo
        cart cartItem = new cart( drinks.getName(),cartItemId, drinks.getImage(),name, drinks.getPrice(), 1.0f);
        // Thêm sản phẩm vào Firestore với ID đã chỉ định
        db.collection("cart")
                .document(cartItemId)  // Set document ID
                .set(cartItem)  // Use set() to assign the ID explicitly
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(MainActivity.this, "Sản phẩm đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Thêm sản phẩm vào giỏ hàng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // Hàm lọc sản phẩm theo tên
    private void filter(String text) {
        ArrayList<Drinks> filteredDrinks = new ArrayList<>(); // Danh sách đã lọc

        for (Drinks item : arr_DrinksFull) { // Thay đổi từ arr_Drinks sang arr_DrinksFull
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredDrinks.add(item);
            }
        }

        // Cập nhật adapter với danh sách đã lọc
        sanPhamAdapter.updateList(filteredDrinks);
        if (filteredDrinks.isEmpty()) {
            Toast.makeText(MainActivity.this, "Không tìm thấy sản phẩm nào", Toast.LENGTH_SHORT).show();
        }
    }
}
