package com.example.terrace.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.terrace.EditProductActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.terrace.Interface.icDeleteDrinkClick;
import com.example.terrace.R;
import com.example.terrace.model.Drinks;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Drinks> arr_Product;
    private ProductOnClickListener productOnClickListener;
    private icDeleteDrinkClick icDeleteDrinkClick;

    public ProductAdapter(Context context, ArrayList<Drinks> arr_Product, ProductOnClickListener productOnClickListener, icDeleteDrinkClick icDeleteDrinkClick) {
        this.context = context;
        this.arr_Product = arr_Product;
        this.productOnClickListener = productOnClickListener;
        this.icDeleteDrinkClick = icDeleteDrinkClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.productitems, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drinks sp = arr_Product.get(position);

        // Sử dụng Glide để tải ảnh
        Glide.with(context).load(sp.getImage()).into(holder.ivHinhSP);
        holder.txtTenSP.setText(sp.getName());
        holder.txtGiaSP.setText(String.valueOf(sp.getPrice()));

        // Xử lý sự kiện khi click vào item
        holder.itemView.setOnClickListener(v -> productOnClickListener.onClickAtItem(position));

        // Xử lý sự kiện khi click vào nút xóa
        holder.btnDelete.setOnClickListener(v -> icDeleteDrinkClick.onDrinkClick(sp));
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditProductActivity.class);
                i.putExtra("drink_name",sp.getName());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arr_Product.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhSP;
        TextView txtTenSP, txtGiaSP;
        Button btnEdit;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhSP = itemView.findViewById(R.id.ivHinhSP);
            txtTenSP = itemView.findViewById(R.id.txtTenSP);
            txtGiaSP = itemView.findViewById(R.id.txtGiaSP);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btn_Edt);
        }
    }

    // Phương thức lọc danh sách
    public void filterList(ArrayList<Drinks> filteredList) {
        this.arr_Product = filteredList;
        notifyDataSetChanged();
    }

    public interface ProductOnClickListener {
        void onClickAtItem(int position);
    }
}
