package com.example.terrace.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.terrace.R;
import com.example.terrace.model.Order;
import com.google.api.Context;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_items, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvUser.setText(order.getUsername());
        holder.tvAddress.setText(order.getAddress());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(order.getOrderDate().toDate());
        holder.tvOrderDate.setText(formattedDate);

        holder.tvPhone.setText(order.getPhone());
        holder.tvPayMethod.setText(order.getPaymethod());
        holder.tvStatus.setText(order.getStatus());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvUser, tvAddress, tvOrderDate, tvPhone, tvPayMethod, tvStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvPayMethod = itemView.findViewById(R.id.tvPayMethod);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
