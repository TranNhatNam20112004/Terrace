package com.example.terrace.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.terrace.Interface.icPromoClick;
import com.example.terrace.Interface.icUsePromoClick;
import com.example.terrace.R;
import com.example.terrace.model.Promotion;
import android.text.format.DateFormat; // Thêm import này
import java.text.SimpleDateFormat; // Thêm import này
import java.util.Date;

import java.util.ArrayList;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.ViewHolder>  {
    //khai bao bien
    Activity context;
    ArrayList<Promotion> arrPromo;
    icPromoClick icPromoClickm;
    icUsePromoClick icUsePromoClickm;
    public PromoAdapter(Activity context, ArrayList<Promotion> arrPromo, icPromoClick icPromoClickm, icUsePromoClick icUsePromoClickm){
        this.context=context;
        this.arrPromo = arrPromo;
        this.icPromoClickm = icPromoClickm;
        this.icUsePromoClickm = icUsePromoClickm;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View viewSanPham=layoutInflater.inflate(R.layout.promo_items,parent, false);
        ViewHolder viewHolderSP=new ViewHolder(viewSanPham);
        return  viewHolderSP;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Promotion sp= arrPromo.get(position);


        holder.tvName.setText(sp.getName());

        holder.tvDiscount.setText(String.valueOf(sp.getDiscount()));

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icPromoClickm.onClick(sp);
            }
        });
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icUsePromoClickm.onClick(sp);
            }
        });
        Date endDate = sp.getEnd().toDate();
        // Định dạng ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedEndDate = dateFormat.format(endDate);
        holder.tvEndDay.setText(formattedEndDate);
    }

    @Override
    public int getItemCount() {
        return arrPromo.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvDiscount, tvEndDay;
        ImageButton btnRemove, btnUse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPromoName);
            tvDiscount=itemView.findViewById(R.id.tvDiscount);
            btnRemove=itemView.findViewById(R.id.btnRemove);
            btnRemove=itemView.findViewById(R.id.btnUsePromo);
            tvEndDay=itemView.findViewById(R.id.tvEndDay);
        }
    }
}
