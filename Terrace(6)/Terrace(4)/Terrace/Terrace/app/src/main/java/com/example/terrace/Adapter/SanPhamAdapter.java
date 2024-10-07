package com.example.terrace.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.terrace.Interface.icDrinkClick;
import com.example.terrace.R;
import com.example.terrace.model.Drinks;

import java.util.ArrayList;

public class SanPhamAdapter extends RecyclerView.Adapter<SanPhamAdapter.ViewHolder> {
    private Activity context;
    private ArrayList<Drinks> arr_Drinks;
    private ArrayList<Drinks> arr_DrinksFull; // Danh sách đầy đủ để tìm kiếm
    private icDrinkClick icDrinkClickm;

    public SanPhamAdapter(Activity context, ArrayList<Drinks> arr_Drinks, icDrinkClick icDrinkClickm) {
        this.context = context;
        this.arr_Drinks = arr_Drinks;
        this.arr_DrinksFull = new ArrayList<>(arr_Drinks); // Sao chép danh sách đầy đủ
        this.icDrinkClickm = icDrinkClickm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View viewSanPham = layoutInflater.inflate(R.layout.items_sp, parent, false);
        return new ViewHolder(viewSanPham);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drinks sp = arr_Drinks.get(position);

        Glide.with(context)
                .load(sp.getImage())
                .into(holder.iv_1);
        holder.txtSanPham.setText(sp.getName());
        holder.txtGia.setText(String.valueOf(sp.getPrice()));

        holder.btnAdd.setOnClickListener(v -> icDrinkClickm.onDrinkClick(sp));
    }

    @Override
    public int getItemCount() {
        return arr_Drinks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton iv_1, btnAdd;
        TextView txtSanPham, txtGia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_1 = itemView.findViewById(R.id.iv_1);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            txtSanPham = itemView.findViewById(R.id.txtSanPham);
            txtGia = itemView.findViewById(R.id.txtGia);
        }
    }

    // Cập nhật danh sách sản phẩm
    public void updateList(ArrayList<Drinks> list) {
        arr_Drinks.clear();
        arr_Drinks.addAll(list);
        notifyDataSetChanged();
    }

    // Xóa sản phẩm từ danh sách tìm kiếm
    public void removeItem(Drinks drinks) {
        arr_Drinks.remove(drinks);
        notifyDataSetChanged();
    }

    // Cập nhật danh sách đầy đủ
    public void updateFullList(ArrayList<Drinks> fullList) {
        arr_DrinksFull.clear();
        arr_DrinksFull.addAll(fullList);
    }
}
