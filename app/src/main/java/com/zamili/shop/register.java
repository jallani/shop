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


public class register extends AppCompatActivity {

    private EditText r_txtname, r_txtphoneno, r_txtemail, r_txtpassword, r_txtcmpassword;
    private Button btnregister;

    private ImageView profileimg;
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
        setContentView(R.layout.activity_register);
        profileimg =  findViewById(R.id.profileimage);
        r_txtname = (EditText) findViewById(R.id.name);
        r_txtphoneno = (EditText) findViewById(R.id.phoneno);
        r_txtemail = (EditText) findViewById(R.id.email);
        r_txtpassword = (EditText) findViewById(R.id.password);
        r_txtcmpassword = (EditText) findViewById(R.id.cmpassword);
        btnregister = (Button) findViewById(R.id.register);
        firebaseauth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait.....");
        progressDialog.setCanceledOnTouchOutside(false);

        //permission arrays

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowimagepickDialoge();
            }
        });


        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputdata();

            }
        });

    }



    public void Alreadyhaveaccount(View view) {
        Intent intent = new Intent(register.this,MainActivity.class);
        startActivity(intent);


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
        fullname = r_txtname.getText().toString().trim();
        phoneno = r_txtphoneno.getText().toString().trim();
        emailrt = r_txtemail.getText().toString().trim();
        password =r_txtpassword.getText().toString().trim();
        confirmpassword = r_txtcmpassword.getText().toString().trim();


        if(TextUtils.isEmpty(fullname))
        {
            r_txtname.setError("Please Enter Name");
            Toast.makeText(this,"Enter Name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phoneno))
        {
            r_txtphoneno.setError("Please Enter Phone Number");
            Toast.makeText(this,"Enter Phone number",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailrt).matches())
        {
            r_txtemail.setError("Enter Email");
            Toast.makeText(this,"Enter Email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<7)
        {
            r_txtpassword.setError("Please Enter 8 Character");
            Toast.makeText(this,"Please Enter 8 Character",Toast.LENGTH_SHORT).show();
            return;

        }
        if(!password.equals(confirmpassword))
        {
            r_txtcmpassword.setError("Your Password is Not Same.");
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
                        Toast.makeText(register.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void savefirebasedatabase() {

        progressDialog.setMessage("Saving Account Info ...");

        String timestamp  = ""+System.currentTimeMillis();

        if(image_uri==null)
        {
            HashMap <String ,Object> hashMap = new HashMap<>();

            hashMap.put("uid",""+firebaseauth.getUid());
            hashMap.put("email",""+emailrt);
            hashMap.put("phonenmber",""+phoneno);
            hashMap.put("password",""+password);
            hashMap.put("name",""+fullname);
            hashMap.put("profileImage","");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseauth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    startActivity(new Intent(register.this,mainseller.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    startActivity(new Intent( register.this,mainseller.class));
                    finish();
                }
            });

        }else {
            String filepathandname = "profile+images/" + "" + firebaseauth.getUid();

            StorageReference  storageReference = FirebaseStorage.getInstance().getReference(filepathandname);
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
                        hashMap.put("profileImage", "" + downloadImageuri);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        ref.child(firebaseauth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                startActivity(new Intent(register.this, mainseller.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                startActivity(new Intent(register.this, mainseller.class));
                                finish();
                            }
                        });
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(register.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
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
                profileimg.setImageURI(image_uri);
            } else if(requestCode==IMAGE_PICK_CAMERA_CODE)
            {
                profileimg.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}