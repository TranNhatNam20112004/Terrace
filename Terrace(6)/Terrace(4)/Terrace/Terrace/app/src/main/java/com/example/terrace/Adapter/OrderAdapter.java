package com.example.terrace.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.terrace.R;
import com.example.terrace.model.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private FirebaseFirestore db;
    private boolean isAdmin;

    public OrderAdapter(List<Order> orderList, boolean isAdmin) {
        this.orderList = orderList;
        this.db = FirebaseFirestore.getInstance();
        this.isAdmin = isAdmin;
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

        if (isAdmin) {
            holder.tvStatus.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.status_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    String newStatus = item.getTitle().toString();
                    order.setStatus(newStatus);
                    db.collection("orders").document(order.getOrderId())
                            .update("status", newStatus);
                    holder.tvStatus.setText(newStatus);
                    return true;
                });
                popupMenu.show();
            });

            holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ("Đã hủy".equals(order.getStatus())) {
                        Toast.makeText(view.getContext(), "Đơn hàng đã bị hủy", Toast.LENGTH_SHORT).show();
                    } else {
                        db.collection("orders").document(order.getOrderId())
                                .update("status", "Đang giao")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.tvStatus.setText("Đang giao");
                                        Toast.makeText(view.getContext(), "Đơn hàng đang được giao", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "Cập nhật trạng thái thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            });
        } else {
            holder.tvStatus.setClickable(false);
            holder.btn_accept.setVisibility(View.GONE);
        }

        holder.btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("Đang giao".equals(order.getStatus())) {
                    Toast.makeText(view.getContext(), "Đơn hàng đang được giao, không thể hủy", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("orders").document(order.getOrderId())
                            .update("status", "Đã hủy")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    holder.tvStatus.setText("Đã hủy");
                                    Toast.makeText(view.getContext(), "Đơn hàng đã bị hủy", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), "Cập nhật trạng thái thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvAddress, tvOrderDate, tvPhone, tvPayMethod, tvStatus;
        Button btn_accept, btn_cancle;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvPayMethod = itemView.findViewById(R.id.tvPayMethod);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_cancle = itemView.findViewById(R.id.btn_cancle);
        }
    }
}
