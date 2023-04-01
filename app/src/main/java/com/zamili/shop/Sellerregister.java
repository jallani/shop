package com.zamili.shop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Sellerregister extends AppCompatActivity {

    private EditText u_txtname, u_txtphoneno, u_txtemail, u_txtpassword, u_txtcmpassword;
    private Button ubtnregister;

    private ImageView uprofileimg;
    // permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    // permission arrays

    private FirebaseAuth firebaseauth;
    private ProgressDialog progressDialog;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;


    private String[] cameraPermission;
    private String[] storagePermission;
    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellerregister);

        uprofileimg =  findViewById(R.id.userprofileimage);
        u_txtname = (EditText) findViewById(R.id.username);
        u_txtphoneno = (EditText) findViewById(R.id.userphoneno);
        u_txtemail = (EditText) findViewById(R.id.useremail);
        u_txtpassword = (EditText) findViewById(R.id.userpassword);
        u_txtcmpassword = (EditText) findViewById(R.id.usercmpassword);
        ubtnregister = (Button) findViewById(R.id.userregister);
        firebaseauth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait.....");
        progressDialog.setCanceledOnTouchOutside(false);

        //permission arrays

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        uprofileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowimagepickDialoge();
            }
        });


        ubtnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputdata();

            }
        });
    }

    private void ShowimagepickDialoge() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (checkcamerapermission()) {
                        pickFromCamera();
                    } else {
                        requestCamerapermission();
                    }
                } else {
                    if (checkstoragepermission()) {
                        pickfromGallery();
                    } else {
                        requeststoragePremission();
                    }

                }
            }
        }).show();
    }

    private boolean checkstoragepermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);


        return result;


    }

    private void pickfromGallery()

    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkcamerapermission(){
        boolean result =ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);


        return result && result1;



    }
    private void requeststoragePremission()
    {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private  void requestCamerapermission()
    {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put(MediaStore.Images.Media.TITLE,"Temp_Image Title");
        contentvalues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentvalues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);

        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }


    private String fullname,phoneno,emailrt,password,confirmpassword;


    private void inputdata() {
        fullname =u_txtname.getText().toString().trim();
        phoneno = u_txtphoneno.getText().toString().trim();
        emailrt = u_txtemail.getText().toString().trim();
        password =u_txtpassword.getText().toString().trim();
        confirmpassword = u_txtcmpassword.getText().toString().trim();


        if(TextUtils.isEmpty(fullname))
        {
            u_txtname.setError("Please Enter Your Name");
            Toast.makeText(this,"Enter Name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phoneno))
        {
            u_txtphoneno.setError("Please Enter Phone No.");
            Toast.makeText(this,"Enter Phone number",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailrt).matches())
        {
            u_txtemail.setError("Please Enter Your Email: example@gmail.com");
            Toast.makeText(this,"Enter Email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()>7)
        {
            u_txtpassword.setError("Please Enter Password More Then 6 Characters");
            Toast.makeText(this,"",Toast.LENGTH_SHORT).show();
            return;

        }
        if(!password.equals(confirmpassword))
        {
            u_txtcmpassword.setError("Please Enter Confirm Password");
            Toast.makeText(this, "Password Doesn 't match ....", Toast.LENGTH_SHORT).show();
            return;
        }
        createAccount();
    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account ....");
        progressDialog.show();
        firebaseauth.createUserWithEmailAndPassword(emailrt,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        savefirebasedatabase();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Sellerregister.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void savefirebasedatabase() {

        progressDialog.setMessage("Saving Account Info ...");

        String timestamp  = ""+System.currentTimeMillis();

        if(image_uri==null)
        {
            HashMap<String ,Object> hashMap = new HashMap<>();

            hashMap.put("uid",""+firebaseauth.getUid());
            hashMap.put("email",""+emailrt);
            hashMap.put("phonenmber",""+phoneno);
            hashMap.put("password",""+password);
            hashMap.put("name",""+fullname);
            hashMap.put("accounttype",""+"Seller");
            hashMap.put("profileImage","");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseauth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    startActivity(new Intent(Sellerregister.this,userview.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    startActivity(new Intent( Sellerregister.this,userview.class));
                    finish();
                }
            });

        }else {
            String filepathandname = "profile+images/" + "" + firebaseauth.getUid();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filepathandname);
            storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadImageuri =uriTask.getResult();
                    if(uriTask.isSuccessful()) {
                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("uid", "" + firebaseauth.getUid());
                        hashMap.put("email", "" + emailrt);
                        hashMap.put("phonenmber", "" + phoneno);
                        hashMap.put("password", "" + password);
                        hashMap.put("name", "" + fullname);
                        hashMap.put("accounttype",""+"Seller");
                        hashMap.put("profileImage", "" + downloadImageuri);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        ref.child(firebaseauth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                startActivity(new Intent(Sellerregister.this, userview.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                startActivity(new Intent(Sellerregister.this, userview.class));
                                finish();
                            }
                        });
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Sellerregister.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });


        }


    }

    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted)
                    {
                        pickFromCamera();
                    }
                    else
                    {
                        Toast.makeText(this,"Camera permission are necessary...",Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0)
                {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted)
                    {
                        pickfromGallery();
                    }
                    else
                    {
                        Toast.makeText(this,"Storage permission are necessary...",Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;


        }

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                uprofileimg.setImageURI(image_uri);
            } else if(requestCode==IMAGE_PICK_CAMERA_CODE)
            {
                uprofileimg.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void ShowHidePass(View view) {

        if(view.getId()==R.id.show_pass_btn){
            if(u_txtpassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.ic_baseline_visibility_off_24);
                //Show Password
                u_txtpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_baseline_visibility_24);
                //Hide Password
                u_txtpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }

        if(view.getId()==R.id.show_pass_btn1){
            if(u_txtcmpassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.ic_baseline_visibility_off_24);
                //Show Password
                u_txtcmpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_baseline_visibility_24);
                //Hide Password
                u_txtcmpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public void Alreadyhaveaccount(View view) {
        Intent intent = new Intent(Sellerregister.this,MainActivity.class);
        startActivity(intent);


    }
}