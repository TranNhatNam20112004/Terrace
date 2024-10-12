package com.example.terrace.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.terrace.EditProductActivity;
import com.example.terrace.R;
import com.example.terrace.model.Drinks;
import com.example.terrace.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Drinks> arr_Product;
    ProductOnClickListener productOnClickListener;

    FirebaseFirestore db;

    public ProductAdapter(Context context, ArrayList<Drinks> arr_Product, ProductOnClickListener productOnClickListener){
        this.context = context;
        this.arr_Product = arr_Product;
        this.productOnClickListener = productOnClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View viewSanPham = layoutInflater.inflate(R.layout.productitems,parent,false);
        ViewHolder viewHolderSP = new ViewHolder(viewSanPham);
        return viewHolderSP;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        Drinks sp =  arr_Product.get(position);

        Picasso.get().load(sp.getImage()).into(holder.ivHinhSP);
        String g = String.valueOf(sp.getPrice());
        holder.txtGiaSP.setText(g);
        holder.txtTenSP.setText(sp.getName());
        holder.itemView.setOnClickListener(v -> productOnClickListener.onClickAtItem(position));
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivHinhSP;
        TextView txtTenSP, txtGiaSP;
        Button btnEdit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhSP = itemView.findViewById(R.id.ivHinhSP);
            txtTenSP = itemView.findViewById(R.id.txtTenSP);
            txtGiaSP = itemView.findViewById(R.id.txtGiaSP);
            btnEdit = itemView.findViewById(R.id.btn_Edt);
        }
    }
    public void filterList(ArrayList<Drinks> filteredList) {
        this.arr_Product = filteredList;
        notifyDataSetChanged();
    }
    public interface ProductOnClickListener {
        void onClickAtItem(int position);
    }
}
