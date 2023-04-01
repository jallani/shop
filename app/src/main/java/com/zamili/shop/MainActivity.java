package com.zamili.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private EditText txtemail, txtpassword;
    private Button btnlogin;
    private FirebaseAuth firebase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        //Get ids  of all email and password
        txtemail = (EditText) findViewById(R.id.email);
        txtpassword = (EditText) findViewById(R.id.password);
        btnlogin = (Button) findViewById(R.id.login);
        firebase =  FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait ....");
        progressDialog.setCanceledOnTouchOutside(false);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginuser();
            }
        });
    }
    private String emailet, passwordet;

    private void loginuser() {
        emailet = txtemail.getText().toString().trim();
        passwordet = txtpassword.getText().toString().trim();


        if (!Patterns.EMAIL_ADDRESS.matcher(emailet).matches())
        {
            txtemail.setError("Email Is Not Correct");
            Toast.makeText(this,"Invalid Email pattern ...........",Toast.LENGTH_SHORT).show();

            return;
        }
        if(TextUtils.isEmpty(passwordet))
        {
            txtpassword.setError("Password Is Not Correct");
            Toast.makeText(this,"Enter password...." ,Toast.LENGTH_SHORT).show();
            return;

        }
        progressDialog.setMessage("Logging in.....");
        progressDialog.show();
        firebase.signInWithEmailAndPassword(emailet ,passwordet).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                makemeonline();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makemeonline() {

        progressDialog.setMessage("checking User ....");


        HashMap<String ,Object> hashMap  = new HashMap<>();
        hashMap.put("online","true");

        // update vale to db

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(firebase.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                CheckuserType();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void CheckuserType() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebase.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren())
                        {
                            String accounttype=""+ds.child("accounttype").getValue();
                            if (accounttype.equals("Seller"))
                            {
                                startActivity(new Intent(MainActivity.this,userview.class));

                                finish();

                            }
                            else
                            {
                                startActivity(new Intent(MainActivity.this,mainseller.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void createnewaccount(View view) {
        Intent intent = new Intent(MainActivity.this,Sellerregister.class);
        startActivity(intent);
    }

    public void ShowHidePass(View view) {

        if(view.getId()==R.id.show_pass_btn){
            if(txtpassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.ic_baseline_visibility_off_24);
                //Show Password
                txtpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_baseline_visibility_24);
                //Hide Password
                txtpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public void loginphone(View view) {

            Intent intent = new Intent(MainActivity.this,phonecall.class);
            startActivity(intent);


    }



}