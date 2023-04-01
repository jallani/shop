package com.zamili.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class userview extends AppCompatActivity {

     private  RecyclerView recyclerView;
     private ArrayList<usermodel> list;
     private Adapterforuser adapterforuser;
     FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ImageButton logout,filterbtn,filterbtn2,filterbtn3,filterbtn4,filterbtn5,filterbtn6,filterbtn7,filterbtn8,filterbtn9,filterbtn10,filterbtn11,youtube,facebook,instagam;
    private ImageView personimg;
    private TextView username,userphone,ushowall;
    private EditText searchproducts;
    private AdView madview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userview);

        youtube = findViewById(R.id.youtube);
        facebook = findViewById(R.id.facebook);
        instagam = findViewById(R.id.instagram);

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent yt = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/@muzammalsadique"));
                yt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                yt.setPackage("com.google.android.youtube");
                startActivity(yt);
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fb = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/people/zamil-online-shopping-App/100088872177806/"));

                startActivity(fb);
            }
        });
        instagam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inst = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/zamilonlineshopping/?igshid=ZDdkNTZiNTM%3D"));

                startActivity(inst);
            }
        });





        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        madview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        madview.loadAd(adRequest);

       
        searchproducts = findViewById(R.id.searchproducts);
        ushowall = findViewById(R.id.usershowall);
        filterbtn = findViewById(R.id.filterbtn);
        filterbtn2 = findViewById(R.id.filterbtn2);
        filterbtn3 = findViewById(R.id.filterbtn3);
        filterbtn4 = findViewById(R.id.filterbtn4);
        filterbtn5 = findViewById(R.id.filterbtn5);
        filterbtn6 = findViewById(R.id.filterbtn6);
        filterbtn7 = findViewById(R.id.filterbtn7);
        filterbtn8 = findViewById(R.id.filterbtn8);
        filterbtn9 = findViewById(R.id.filterbtn9);
        filterbtn10 = findViewById(R.id.filterbtn10);
        filterbtn11 = findViewById(R.id.filterbtn11);


        recyclerView= findViewById(R.id.userallproduct);
        logout = findViewById(R.id.userlogoutbutton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait.....");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();


        displayitem();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makemeoffline();
            }
        });
        searchproducts.addTextChangedListener(new TextWatcher() {
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

        filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(userview.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productcategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String Selected = Constants.productcategory1[i];
                                ushowall.setText(Selected);
                                if(Selected.equals("ALL"))
                                {
                                    displayitem();
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

        filterbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loadfilterProduct("Beauty Plus Fasion");
                Intent intent = new Intent(userview.this,BeautyPlusFashion.class);
                startActivity(intent);
            }
        });
        filterbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(userview.this,GarmitsforMen.class);
                startActivity(intent);
            }
        });
        filterbtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(userview.this,Garmitsforwomen.class);
                startActivity(intent);
            }
        });
        filterbtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(userview.this,Sports.class);
                startActivity(intent);
            }
        });
        filterbtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(userview.this,BabyToys.class);
                startActivity(intent);
            }
        });
        filterbtn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(userview.this,Mobileaccessories.class);
                startActivity(intent);
            }
        });
        filterbtn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(userview.this,Electronic.class);
                startActivity(intent);
            }
        });
        filterbtn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userview.this,Cosmetic.class);
                startActivity(intent);
            }
        });
        filterbtn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userview.this,medician.class);
                startActivity(intent);
            }
        });
        filterbtn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userview.this,Manfashion.class);
                startActivity(intent);
            }
        });






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
            startActivity(new Intent(userview.this,MainActivity.class));
            finish();
        }
        else
        {
            Loadmyinfo();
        }
    }

    private void Loadmyinfo() {
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
                        adapterforuser = new Adapterforuser(userview.this,list);
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
        reference.child("6xB3yDexPIenHXvo7SM5OoG1kJr1").child("Products").addValueEventListener(new ValueEventListener() {
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
                        adapterforuser = new Adapterforuser(userview.this,list);
                        GridLayoutManager layoutManager=new GridLayoutManager(userview.this,2);

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

}