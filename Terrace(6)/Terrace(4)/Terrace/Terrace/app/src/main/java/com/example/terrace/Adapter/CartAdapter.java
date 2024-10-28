package com.example.terrace.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.terrace.Interface.icCartClick;
import com.example.terrace.Interface.icUpdateCartClick;
import com.example.terrace.R;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.cart;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>  {
    //khai bao bien
    Activity context;
    ArrayList<cart> arr_Cart;
    icCartClick icCartClickm;
    icUpdateCartClick icUpdateCartClickm;

    int quan = 1;

    public CartAdapter(Activity context, ArrayList<cart> arr_Cart,
                       icCartClick icCartClickm,
                       icUpdateCartClick icUpdateCartClickm){
        this.context=context;
        this.arr_Cart = arr_Cart;
        this.icCartClickm = icCartClickm;
        this.icUpdateCartClickm = icUpdateCartClickm;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View viewSanPham=layoutInflater.inflate(R.layout.cart_item,parent, false);
        ViewHolder viewHolderSP=new ViewHolder(viewSanPham);
        return  viewHolderSP;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        cart sp= arr_Cart.get(position);
        float currentQuantity = sp.getQuantity(); // Lấy số lượng hiện tại từ đối tượng Product
        float price = sp.getPrice() / currentQuantity; // Tính giá đơn vị của sản phẩm
        Glide.with(context)
                .load(sp.getImage())
                .into(holder.imgProduct);
        holder.txtProductName.setText(sp.getName());

        holder.txtProductPrice.setText(String.valueOf(sp.getPrice()));

        holder.btnRemoveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icCartClickm.onCartClick(sp);
            }
        });
        holder.tvQuantity.setText(String.valueOf((int)sp.getQuantity()));

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.setQuantity(sp.getQuantity() + 1);
                holder.tvQuantity.setText(String.valueOf((int)sp.getQuantity()));
                float newPrice = price * sp.getQuantity();
                sp.setPrice(newPrice);
                holder.txtProductPrice.setText(String.valueOf(newPrice));
                icUpdateCartClickm.onCartClick(sp);
                notifyItemChanged(position); // Thông báo RecyclerView cập nhật item
            }
        });

        holder.btnLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sp.getQuantity() > 1) {
                    sp.setQuantity(sp.getQuantity() - 1);
                    holder.tvQuantity.setText(String.valueOf((int)sp.getQuantity()));
                    float newPrice = price * sp.getQuantity();
                    sp.setPrice(newPrice);
                    holder.txtProductPrice.setText(String.valueOf(newPrice));
                    icUpdateCartClickm.onCartClick(sp);
                    notifyItemChanged(position); // Thông báo RecyclerView cập nhật item
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return arr_Cart.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgProduct;
        TextView txtProductName, txtProductPrice, tvQuantity;
        ImageButton btnRemoveProduct;
        Button btnLess, btnAdd;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct=itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice=itemView.findViewById(R.id.txtProductPrice);
            btnRemoveProduct=itemView.findViewById(R.id.btnRemoveProduct);
            tvQuantity=itemView.findViewById(R.id.tvQuantity);
            btnLess = itemView.findViewById(R.id.btnLess);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}
