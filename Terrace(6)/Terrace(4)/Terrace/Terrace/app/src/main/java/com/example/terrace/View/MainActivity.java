package com.example.terrace.View;

import static com.example.terrace.Adapter.SanPhamAdapter.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.terrace.AccountInforActivity;
import com.example.terrace.ActivityCart;
import com.example.terrace.Adapter.SanPhamAdapter;
import com.example.terrace.Interface.icDrinkClick;
import com.example.terrace.ProductDetailActivity;
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
        // Gán username
        txtName = findViewById(R.id.txtName);
        Intent i = getIntent();
        name = i.getStringExtra("name");
        txtName.setText(name);
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
            } else if (itemId == R.id.nav_store) {
                //Toast.makeText(MainActivity.this, "Store selected", Toast.LENGTH_SHORT).show();
                Intent i2 = new Intent(MainActivity.this, ViewBranchsActivity.class);
                startActivity(i2);
                return true;
            } else if (itemId == R.id.nav_list) {
                //Toast.makeText(MainActivity.this, "List selected", Toast.LENGTH_SHORT).show();
                Intent i2 = new Intent(MainActivity.this, AccountInforActivity.class);
                i2.putExtra("name",name);
                startActivity(i2);
                return true;
            }
            return false;
        });

        // Sự kiện logout
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(view -> {
            Intent i2 = new Intent(MainActivity.this, LoginActivity.class);

            startActivity(i2);
        });



        // Gán sự kiện cho giỏ hàng
        btnCart = findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> {
            Intent i1 = new Intent(MainActivity.this, ActivityCart.class);
            i1.putExtra("name", name);
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
        sanPhamAdapter = new SanPhamAdapter(this, arr_Drinks, new icDrinkClick() {
            @Override
            public void onDrinkClick(Drinks drinks) {
                AddProduct(drinks);
            }

            @Override
            public void onItemClick(int position) {
                Intent i = new Intent(MainActivity.this, ProductDetailActivity.class);
                i.putExtra("name", arr_Drinks.get(position).getName());
                i.putExtra("image", arr_Drinks.get(position).getImage());
                i.putExtra("detail", arr_Drinks.get(position).getDetail());
                i.putExtra("price", arr_Drinks.get(position).getPrice());
                startActivity(i);
            }

    });
        recyclerViewSP.setAdapter(sanPhamAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewSP.setLayoutManager(staggeredGridLayoutManager);
    }

    private void AddProduct(Drinks drinks) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng của người dùng chưa
        db.collection("cart")
                .whereEqualTo("name", drinks.getName())
                .whereEqualTo("user", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Nếu sản phẩm đã tồn tại, lấy document và cập nhật quantity
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cart existingCartItem = document.toObject(cart.class);
                            float newQuantity = existingCartItem.getQuantity() + 1;
                            existingCartItem.setQuantity(newQuantity);

                            // Tính lại giá dựa trên số lượng mới
                            existingCartItem.setPrice(drinks.getPrice() * newQuantity);

                            // Cập nhật lại sản phẩm trong Firestore
                            db.collection("cart")
                                    .document(document.getId())
                                    .set(existingCartItem)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(MainActivity.this, "Đã cập nhật số lượng sản phẩm trong giỏ hàng", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(MainActivity.this, "Cập nhật sản phẩm thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        // Nếu sản phẩm chưa tồn tại, thêm mới vào giỏ hàng
                        String cartItemId = db.collection("cart").document().getId();
                        cart cartItem = new cart(drinks.getName(), cartItemId, drinks.getImage(), name, drinks.getPrice(), 1);

                        db.collection("cart")
                                .document(cartItemId)
                                .set(cartItem)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(MainActivity.this, "Sản phẩm đã được thêm vào giỏ hàng", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(MainActivity.this, "Thêm sản phẩm vào giỏ hàng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
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
