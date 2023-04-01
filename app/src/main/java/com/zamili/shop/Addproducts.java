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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Addproducts extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private EditText proname,prophone,proprice;
    private ImageButton backbtn;
    private ImageView productimgbtn;
    private Button savadata;
    String ctg;
    Spinner spinner;
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
        setContentView(R.layout.activity_addproducts);

        proname = findViewById(R.id.proname);
        proprice = findViewById(R.id.proprice);
        prophone =findViewById(R.id.prophone);
        spinner = findViewById(R.id.spinnerctg);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.ctg, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        savadata = findViewById(R.id.savaproduct);

        productimgbtn = findViewById(R.id.productimg);
        backbtn = findViewById(R.id.backact);
        firebaseauth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait.....");
        progressDialog.setCanceledOnTouchOutside(false);





        productimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowimagepickDialoge();
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Addproducts.this,mainseller.class));
                finish();
            }
        });

        savadata.setOnClickListener(new View.OnClickListener() {
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


    private String dbproname,dbprophone,dbproprice;


    private void inputdata() {
        dbproname = proname.getText().toString().trim();
        dbprophone = prophone.getText().toString().trim();
        dbproprice = proprice.getText().toString().trim();


        if(TextUtils.isEmpty(dbproname))
        {
            Toast.makeText(this,"Enter Name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(dbprophone))
        {
            Toast.makeText(this,"Enter Category",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(dbproprice))
        {
            Toast.makeText(this,"Enter Price",Toast.LENGTH_SHORT).show();
            return;
        }

        addproduct();
    }


    private void addproduct() {

        progressDialog.setMessage("Adding Products ...");

        String timestamp  = ""+System.currentTimeMillis();

        if(image_uri==null)
        {
            Toast.makeText(Addproducts.this,"Please Select the Image First..",Toast.LENGTH_SHORT).show();


        }else {
            String filepathandname = "Product+images/" + "" + timestamp;

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
                        hashMap.put("productId",""+timestamp);
                        hashMap.put("Proname",""+dbproname);
                        hashMap.put("Prophonenumber",""+dbprophone);
                        hashMap.put("Proprice",""+dbproprice);
                        hashMap.put("ProImage", "" + downloadImageuri);
                        hashMap.put("timestamp",""+timestamp);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        ref.child(firebaseauth.getUid()).child("Products").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(Addproducts.this,"Product Added Successfully..",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(Addproducts.this,"Product not added Try again..",Toast.LENGTH_SHORT).show();


                            }
                        });
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Addproducts.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
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
                productimgbtn.setImageURI(image_uri);
            } else if(requestCode==IMAGE_PICK_CAMERA_CODE)
            {
                productimgbtn.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        prophone.setText(text);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}