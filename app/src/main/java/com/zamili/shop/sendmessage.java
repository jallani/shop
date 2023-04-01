package com.zamili.shop;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.ACTION_VIEW;
import static android.media.CamcorderProfile.get;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Objects;

public class sendmessage extends AppCompatActivity {
    Button sendbtn,sharebtn;

    private CountryCodePicker countryCodePicker;
    private EditText phone;
    private TextInputEditText message;
    private ImageView imgvb;
    String messagestr, phonestr = "";

    private String stringFile = Environment.getExternalStorageDirectory().getPath() + File.separator + "Test.pdf";

    private static int REQUEST_CODE = 100;
    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmessage);
        message = findViewById(R.id.message);
        sendbtn = findViewById(R.id.sendbtn);
        sharebtn = findViewById(R.id.shareimage);

        imgvb = findViewById(R.id.whatsimg);
        Intent intent=getIntent();
        String imgurl = intent.getStringExtra("ProImage");
        Picasso.get().load(imgurl).placeholder(R.drawable.ic_baseline_shopping_cart_checkout_24).into(imgvb);
        Zoomy.Builder builder = new Zoomy.Builder(this)
                .target(imgvb)
                .animateZooming(false)
                .enableImmersiveMode(false)
                .tapListener(new TapListener() {
                    @Override
                    public void onTap(View v) {
                        Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                    }
                });
        builder.register();



        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        StrictMode.VmPolicy.Builder buildern = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(buildern.build());
        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(sendmessage.this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                    saveImage();



                }else {


                    askPermission();

                }



            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagestr = message.getText().toString();

                if (!messagestr.isEmpty() ) {


                    if (isWhatappInstalled()){

                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=+923215130036"+
                                "&text="+messagestr));
                        startActivity(i);
                        message.setText("");


                    }else {

                        Toast.makeText(sendmessage.this,"Whatsapp is not installed",Toast.LENGTH_SHORT).show();

                    }


                } else {

                    Toast.makeText(sendmessage.this, "Please fill in the Phone no. and message it can't be empty", Toast.LENGTH_LONG).show();

                }

            }
        });







    }


    private boolean isWhatappInstalled(){

        PackageManager packageManager = getPackageManager();
        boolean whatsappInstalled;

        try {

            packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            whatsappInstalled = true;


        }catch (PackageManager.NameNotFoundException e){

            whatsappInstalled = false;

        }

        return whatsappInstalled;

    }



    public void buttonShareFile(View view){

    }





    private void askPermission() {

        ActivityCompat.requestPermissions(sendmessage.this,new String[]{WRITE_EXTERNAL_STORAGE},REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {

        if (requestCode == REQUEST_CODE)
        {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                saveImage();

            }else {


                Toast.makeText(sendmessage.this,"Please provide the required permissions",Toast.LENGTH_SHORT).show();

            }

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void saveImage() {

        File dir = new File(Environment.getExternalStorageDirectory(),"SaveImage");

        if (!dir.exists()){

            dir.mkdir();

        }

        BitmapDrawable drawable = (BitmapDrawable) imgvb.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File file = new File(dir,System.currentTimeMillis()+".jpg");
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        //Toast.makeText(sendmessage.this,"Successfuly Saved",Toast.LENGTH_SHORT).show();

        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


                Intent intentShare = new Intent(ACTION_SEND);
//                String url = "https://api.whatsapp.com/send?phone=" + "+923485613566" + "&text=" + "my work";
                //Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("application/image");
                intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file));
                intentShare.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(intentShare,"share it"));




    }





}