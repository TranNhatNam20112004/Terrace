package com.example.terrace.Interface;

import com.example.terrace.model.Drinks;

public interface icDrinkClick {
    // Phương thức khi click vào một sản phẩm Drinks
    void onDrinkClick(Drinks drinks);
    void onItemClick(int position);
}