package com.example.terrace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity {
    Button btn_AddToCart, btn_back1;
    //ImageButton btn_Cart;
    TextView txtProName1, txtProName2, txtProDescr, txtProPrice;
    ImageView imgSP;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //btn_AddToCart = findViewById(R.id.btn_AddToCart);
        btn_back1 = findViewById(R.id.btn_back1);
        txtProName1 = findViewById(R.id.txtProName1);
        txtProName2 = findViewById(R.id.txtProName2);
        txtProDescr = findViewById(R.id.txtProDescr);
        txtProPrice = findViewById(R.id.txtProPrice);
        //btn_Cart = findViewById(R.id.btn_Cart);
        imgSP = findViewById(R.id.imgSP);

        btn_back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*btn_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, ActivityCart.class);
                startActivity(intent);
            }
        });*/

        Bundle b = getIntent().getExtras();
        if(b!=null){
            String h = b.getString("image");
            String t = b.getString("name");
            String m = b.getString("detail");
            Picasso.get().load(h).into(imgSP);
            txtProName1.setText(t);
            txtProName2.setText(t);
            txtProDescr.setText(m);
            txtProPrice.setText(String.valueOf(b.getFloat("price")));
        }
    }
}