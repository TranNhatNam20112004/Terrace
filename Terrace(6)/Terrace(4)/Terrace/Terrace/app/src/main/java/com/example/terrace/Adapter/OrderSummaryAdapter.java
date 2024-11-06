package com.example.terrace.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.terrace.R;
import com.example.terrace.model.cart;

import java.util.ArrayList;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {

    Activity context;
    ArrayList<cart> arr_OrderSum;

    public OrderSummaryAdapter(Activity context, ArrayList<cart> arr_OrderSum) {
        this.context = context;
        this.arr_OrderSum = arr_OrderSum;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.ordersummary_item, parent,false);
        ViewHolder viewHolderOrder = new ViewHolder(view);
        return viewHolderOrder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        cart os = arr_OrderSum.get(position);
        float currentQuantity = os.getQuantity(); // Lấy số lượng hiện tại từ đối tượng Product
        float price = os.getPrice() / currentQuantity; // Tính giá đơn vị của sản phẩm
        Glide.with(context)
                .load(os.getImage())
                .into(holder.imgOS);
        holder.txtNameOS.setText(os.getName());
        holder.txtPriceOS.setText(String.valueOf(os.getPrice()));
        holder.txtSL.setText(String.valueOf((int)os.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return arr_OrderSum.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNameOS, txtPriceOS, txtSL;
        ImageView imgOS;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameOS = itemView.findViewById(R.id.txtNameOS);
            txtPriceOS = itemView.findViewById(R.id.txtPriceOS);
            imgOS = itemView.findViewById(R.id.imgOS);
            txtSL = itemView.findViewById(R.id.txtSlOS);
        }
    }
}
