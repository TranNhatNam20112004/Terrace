package com.example.terrace.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.terrace.Interface.icCartClick;
import com.example.terrace.R;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.cart;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>  {
    //khai bao bien
    Activity context;
    ArrayList<cart> arr_Cart;
    icCartClick icCartClickm;

    public CartAdapter(Activity context, ArrayList<cart> arr_Cart,icCartClick icCartClickm){
        this.context=context;
        this.arr_Cart = arr_Cart;
        this.icCartClickm = icCartClickm;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        cart sp= arr_Cart.get(position);

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
        holder.tvQuantity.setText(String.valueOf(sp.getQuantity()));


    }

    @Override
    public int getItemCount() {
        return arr_Cart.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgProduct;
        TextView txtProductName, txtProductPrice, tvQuantity;
        ImageButton btnRemoveProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct=itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice=itemView.findViewById(R.id.txtProductPrice);
            btnRemoveProduct=itemView.findViewById(R.id.btnRemoveProduct);
            tvQuantity=itemView.findViewById(R.id.tvQuantity);
        }
    }
}
