package com.zamili.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.squareup.picasso.Picasso;

public class viewimg extends AppCompatActivity {
    ImageView imgv;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewimg);

        imgv= findViewById(R.id.viewimg);
        back = findViewById(R.id.backact);
        Intent intent=getIntent();
        String imgurl = intent.getStringExtra("ProImage");
        Picasso.get().load(imgurl).placeholder(R.drawable.ic_baseline_shopping_cart_checkout_24).into(imgv);
        Zoomy.Builder builder = new Zoomy.Builder(this)
                .target(imgv)
                .animateZooming(false)
                .enableImmersiveMode(false)
                .tapListener(new TapListener() {
                    @Override
                    public void onTap(View v) {
                        Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                    }
                });
        builder.register();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}