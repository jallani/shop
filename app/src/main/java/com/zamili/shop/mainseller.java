package com.zamili.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class mainseller extends AppCompatActivity {
    private TextView sellername,sellerphone,allshow;
    private EditText searchproducts;
    private ImageButton addproduct,logout,filterbtn;
    private ImageView personimg;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView products,orders;
    private RelativeLayout productUI,orderUI;
    private RecyclerView productrecycleview;
//    private Button itemview;



    private ArrayList<Modelproduct> arraylist;
    private Adapterproductseller adapterproductseller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainseller);


        sellername  = findViewById(R.id.sellername);
        sellerphone = findViewById(R.id.sellerphoneno);
        allshow = findViewById(R.id.showall);
        logout = findViewById(R.id.logoutbutton);
        addproduct = findViewById(R.id.addproduct);
        personimg = findViewById(R.id.personimg);
        searchproducts = findViewById(R.id.searchproducts);
        filterbtn = findViewById(R.id.filterbtn);
        productrecycleview = findViewById(R.id.allproduct);

        products = findViewById(R.id.products);
        orders = findViewById(R.id.orders);
        productUI = findViewById(R.id.productUI);
        orderUI = findViewById(R.id.orderUI);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait.....");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkuser();
        ShowproductUI();
        loadallproduct();

        searchproducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterproductseller.getFilter().filter(charSequence);

                } catch (Exception e)
                {

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mainseller.this,Addproducts.class));
                finish();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makemeoffline();
            }
        });
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowproductUI();
            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShoworderUI();
            }
        });
        filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(mainseller.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productcategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String Selected = Constants.productcategory1[i];
                                allshow.setText(Selected);
                                if(Selected.equals("ALL"))
                                {
                                    loadallproduct();
                                }
                                else
                                {
                                    loadfilterProduct(Selected);
                                }
                            }
                        })
                        .show();
            }
        });



    }

    private void loadfilterProduct(String selected) {


        arraylist = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arraylist.clear();
                        for(DataSnapshot ds: snapshot.getChildren())
                        {
                            String Categroy = ""+ds.child("Prophonenumber").getValue();
                            if(selected.equals(Categroy)){
                                    Modelproduct md = ds.getValue(Modelproduct.class);
                                    arraylist.add(md);
                            }
                        }
//
                        adapterproductseller = new Adapterproductseller(mainseller.this,arraylist);
                           productrecycleview.setAdapter(adapterproductseller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException();
                    }
                });

    }

    private void loadallproduct() {
        arraylist = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //arraylist.clear();
                        Log.d("mData", "onDataChange: " + snapshot.getChildren());
                            arraylist.clear();


                            for(DataSnapshot ds: snapshot.getChildren())
                            {
                                Modelproduct md = ds.getValue(Modelproduct.class);
                                arraylist.add(md);
                                Log.d("mData", "onDataChange: " + ds.getValue());
                            }
//
                      adapterproductseller = new Adapterproductseller(mainseller.this,arraylist);
                        RecyclerView.LayoutManager recyce = new LinearLayoutManager(mainseller.this);
                      productrecycleview.setLayoutManager(recyce);
                      productrecycleview.setAdapter(adapterproductseller);
                        Log.d("mDatars", "onDataChange: " + adapterproductseller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException();
                    }
                });
    }

    private void ShoworderUI() {
    }

    private void ShowproductUI() {
        productUI.setVisibility(View.VISIBLE);
        orderUI.setVisibility(View.GONE);

        products.setTextColor(getResources().getColor(R.color.black));
        products.setBackgroundResource(R.drawable.tabshap);
    }

    private void makemeoffline() {
        progressDialog.setMessage("Logging out..");
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("online","false");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                firebaseAuth.signOut();
                checkuser();

            }
        });
    }

    private void checkuser() {
        FirebaseUser firebaseuser = firebaseAuth.getCurrentUser();
        if(firebaseuser==null)
        {
            startActivity(new Intent(mainseller.this,MainActivity.class));
            finish();
        }
        else
        {
            Loadmyinfo();
        }
    }

    private void Loadmyinfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren())
                {
                    String name = ""+ds.child("name").getValue();
                    String accounttype = ""+ds.child("accounttype").getValue();
                    String phonenumber = ""+ds.child("phonenmber").getValue();
                    String profileimage = ""+ds.child("profileImage").getValue();


                    sellername.setText(name);
                    sellerphone.setText(phonenumber);

                    try {
                        Picasso.get().load(profileimage).placeholder(R.drawable.ic_baseline_person_24).into(personimg);
                    }
                    catch (Exception e)
                    {
                        personimg.setImageResource(R.drawable.ic_baseline_person_24);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}