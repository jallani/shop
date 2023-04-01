package com.zamili.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Mobileaccessories extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<usermodel> list;
    private Adapterforuser adapterforuser;
    FirebaseAuth firebaseAuth;
    private AdView madview;
    private EditText searchview;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobileaccessories);
        searchview = findViewById(R.id.search);

        recyclerView= findViewById(R.id.MA);
        firebaseAuth = FirebaseAuth.getInstance();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        madview = findViewById(R.id.mbadView);
        AdRequest adRequest = new AdRequest.Builder().build();
        madview.loadAd(adRequest);

        searchview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterforuser.getFilter().filter(charSequence);

                } catch (Exception e)
                {

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        displayitem();

        loadfilterProduct("Mobile accessories");
    }


    private void loadfilterProduct(String selected) {


        list = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("6xB3yDexPIenHXvo7SM5OoG1kJr1").child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot ds: snapshot.getChildren())
                        {
                            String Categroy = ""+ds.child("Prophonenumber").getValue();
                            if(selected.equals(Categroy)){
                                usermodel md = ds.getValue(usermodel.class);
                                list.add(md);
                            }
                        }
//
                        adapterforuser = new Adapterforuser(Mobileaccessories.this,list);
                        recyclerView.setAdapter(adapterforuser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException();
                    }
                });

    }

    private void displayitem() {
        list = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("6xB3yDexPIenHXvo7SM5OoG1kJr1").child("Products").child("Mobile accessories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //arraylist.clear();
                Log.d("mDataqw", "onDataChange: " + snapshot.getChildren());
                list.clear();


                for(DataSnapshot ds: snapshot.getChildren())
                {
                    usermodel md = ds.getValue(usermodel.class);
                    list.add(md);
                    Log.d("mDataas", "onDataChange: " + ds.getValue());
                }
//
                adapterforuser = new Adapterforuser(Mobileaccessories.this,list);
                GridLayoutManager layoutManager=new GridLayoutManager(Mobileaccessories.this,2);

                // at last set adapter to recycler view.
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapterforuser);
                Log.d("mDatazx", "onDataChange: " + adapterforuser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

    }

    public void backbutton(View view) {
        super.onBackPressed();
        this.finish();
    }
}