package com.zamili.shop;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class medician extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<usermodel> list;
    private Adapterforuser adapterforuser;
    FirebaseAuth firebaseAuth;
    private AdView madview;
    private EditText seachview;

    public static final String Reardunit ="ca-app-pub-5175919488884810/5235108637";
    private RewardedAd mRewarded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medician);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        seachview =findViewById(R.id.search);
        recyclerView= findViewById(R.id.bty);
        firebaseAuth = FirebaseAuth.getInstance();
        seachview.addTextChangedListener(new TextWatcher() {
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
        madview = findViewById(R.id.badView);
        AdRequest adRequest = new AdRequest.Builder().build();
        madview.loadAd(adRequest);
        shoreardads();

        displayitem();loadfilterProduct("Madicine Tablet");



    }


    private void process(String query) {

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
                        adapterforuser = new Adapterforuser(medician.this,list);
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
        reference.child("6xB3yDexPIenHXvo7SM5OoG1kJr1").child("Products").child("Madicine Tablet").addValueEventListener(new ValueEventListener() {
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
                adapterforuser = new Adapterforuser(medician.this,list);
                GridLayoutManager layoutManager=new GridLayoutManager(medician.this,2);

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
    private void loadreardsad()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this,Reardunit ,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        mRewarded = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        mRewarded = ad;
                        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                            }

                            @Override
                            public void onAdImpression() {
                                super.onAdImpression();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                            }
                        });
                    }
                });
    }

    private void shoreardads()
    {
        if (mRewarded != null) {
            Activity activityContext = medician.this;
            mRewarded.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }
}