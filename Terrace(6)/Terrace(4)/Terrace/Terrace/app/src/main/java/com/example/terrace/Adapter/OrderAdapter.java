package com.example.terrace.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.terrace.R;
import com.example.terrace.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private FirebaseFirestore db;
    private boolean isAdmin;  // Thêm biến này để xác định trang admin hay user

    // Sửa constructor để nhận tham số isAdmin
    public OrderAdapter(List<Order> orderList, boolean isAdmin) {
        this.orderList = orderList;
        this.db = FirebaseFirestore.getInstance();
        this.isAdmin = isAdmin;  // Lưu giá trị isAdmin
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

        // Nếu là admin, mới cho phép thay đổi trạng thái
        if (isAdmin) {
            holder.tvStatus.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.status_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    String newStatus = item.getTitle().toString();
                    order.setStatus(newStatus);  // Cập nhật trạng thái trong model
                    db.collection("orders").document(order.getOrderId()) // Cập nhật trạng thái trong Firestore
                            .update("status", newStatus);
                    holder.tvStatus.setText(newStatus);  // Cập nhật giao diện
                    return true;
                });
                popupMenu.show();
            });
        } else {
            // Nếu là user, không cho phép thay đổi trạng thái
            holder.tvStatus.setClickable(false);  // Vô hiệu hóa click
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvAddress, tvOrderDate, tvPhone, tvPayMethod, tvStatus;

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
