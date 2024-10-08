package com.example.terrace.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.terrace.Interface.icPromoClick;
import com.example.terrace.Interface.icUsePromoClick;
import com.example.terrace.R;
import com.example.terrace.model.Promotion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.ViewHolder> {
    // Khai báo biến
    Activity context;
    ArrayList<Promotion> arrPromo;
    icPromoClick icPromoClickm;
    icUsePromoClick icUsePromoClickm;

    // Constructor
    public PromoAdapter(Activity context, ArrayList<Promotion> arrPromo, icPromoClick icPromoClickm, icUsePromoClick icUsePromoClickm) {
        this.context = context;
        this.arrPromo = arrPromo;
        this.icPromoClickm = icPromoClickm;
        this.icUsePromoClickm = icUsePromoClickm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View viewPromo = layoutInflater.inflate(R.layout.promo_items, parent, false);
        return new ViewHolder(viewPromo);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Promotion promo = arrPromo.get(position);

        // Thiết lập các giá trị cho view
        holder.tvName.setText(promo.getName());
        holder.tvDiscount.setText(String.valueOf(promo.getDiscount()));

        // Xử lý sự kiện xóa và sử dụng mã khuyến mãi
        holder.btnRemove.setOnClickListener(v -> icPromoClickm.onClick(promo));
        holder.btnUse.setOnClickListener(v -> icUsePromoClickm.onClick(promo));

        // Kiểm tra nếu ngày kết thúc không null trước khi định dạng và hiển thị
        if (promo.getEnd() != null) {
            Date endDate = promo.getEnd().toDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formattedEndDate = dateFormat.format(endDate);
            holder.tvEndDay.setText(formattedEndDate);
        } else {
            holder.tvEndDay.setText("Không có ngày kết thúc");
        }
    }

    @Override
    public int getItemCount() {
        return arrPromo.size();
    }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDiscount, tvEndDay;
        ImageButton btnRemove, btnUse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPromoName);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnUse = itemView.findViewById(R.id.btnUsePromo);
            tvEndDay = itemView.findViewById(R.id.tvEndDay);
        }
    }
}
