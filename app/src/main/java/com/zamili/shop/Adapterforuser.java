package com.zamili.shop;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapterforuser extends RecyclerView.Adapter<Adapterforuser.holderuseradp> implements Filterable{

    public Adapterforuser(Context context, ArrayList<usermodel> userprolist) {
        this.context = context;
        this.userprolist = userprolist;
        this.filterlist = userprolist;
    }

    private Context context;
    public ArrayList<usermodel> userprolist,filterlist;
    private Filteruserpro filter;
    @NonNull
    @Override
    public holderuseradp onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.userpro,parent,false);
        return new holderuseradp(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderuseradp holder, int position) {
        usermodel usermodel= userprolist.get(position);
        Log.d("mData2", "onDataChange: " + usermodel.getUid());
        Log.d("mData2", "onDataChange: " + usermodel.getProImage());
        Log.d("mData2", "onDataChange: " + usermodel.getProname());

        Log.d("mData2", "onDataChange: " + usermodel.getProphonenumber());
        Log.d("mData2", "onDataChange: " + usermodel.getProprice());
        Log.d("mData2", "onDataChange: " + usermodel.getProductId());
        String img = usermodel.getProImage();
        String proid = usermodel.getProductId();
        String uid = usermodel.getUid();
        String dbproname = usermodel.getProname();
        String dbproprice = usermodel.getProprice();
        String dbpronumber = usermodel.getProphonenumber();

        holder.userproname.setText(usermodel.getProname());
        holder.userproprice.setText("RS:"+usermodel.getProprice());
        holder.userproctg.setText(usermodel.getProphonenumber());

        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n = new Intent(context,sendmessage.class);
                n.putExtra("ProImage",img);
                context.startActivity(n);
            }
        });
        holder.viewimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n = new Intent(context,viewimg.class);
                n.putExtra("ProImage",img);
                context.startActivity(n);
            }
        });

       


        try{
            Picasso.get().load(img).placeholder(R.drawable.ic_baseline_shopping_cart_checkout_24).into(holder.userproimg);

        }catch(Exception e)
        {
            holder.userproimg.setImageResource(R.drawable.ic_baseline_shopping_cart_checkout_24);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailBottomSheet(usermodel);

            }
        });

    }

    private void detailBottomSheet(usermodel usermodel) {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            BottomSheetBehavior<View> bottomSheetBehavior;
            View view = LayoutInflater.from(context).inflate(R.layout.bs_user, null);

            bottomSheetDialog.setContentView(view);
            bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            ImageButton backbtn = view.findViewById(R.id.backbutton);
            ImageView imgview = view.findViewById(R.id.imagedetails);
            ImageButton dmessage = view.findViewById(R.id.detailmessage);

            TextView  detailproname = view.findViewById(R.id.detailproname);
            TextView  detailproprice = view.findViewById(R.id.detailproprice);
            TextView  detailpronumber = view.findViewById(R.id.detailpronumber);

            String dbproname = usermodel.getProname();
            String dbproprice = usermodel.getProprice();
            String dbpronumber = usermodel.getProphonenumber();
            String proid = usermodel.getProductId();

            String img = usermodel.getProImage();

            detailproname.setText(dbproname);
            detailproprice.setText("RS :"+dbproprice);
            detailpronumber.setText(dbpronumber);


            try{
                Picasso.get().load(img).placeholder(R.drawable.ic_baseline_shopping_cart_checkout_24).into(imgview);

            }catch(Exception e)
            {
                imgview.setImageResource(R.drawable.ic_baseline_shopping_cart_checkout_24);

            }

            bottomSheetDialog.show();


            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                }
            });
            dmessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent n = new Intent(context,sendmessage.class);
                    n.putExtra("ProImage",img);
                    context.startActivity(n);
                }
            });
            imgview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent n = new Intent(context,viewimg.class);
                    n.putExtra("ProImage",img);
                    context.startActivity(n);
                }
            });
    }

    @Override
    public int getItemCount() {
        return userprolist.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
        {
            filter=new Filteruserpro(this,filterlist);
        }
        return  filter;
    }

    class holderuseradp extends RecyclerView.ViewHolder {

        private ImageView userproimg;
        private TextView userproname,userproprice,userproctg;
        private Button message,viewimg;
        public holderuseradp(@NonNull View itemView) {
            super(itemView);
            userproimg = itemView.findViewById(R.id.userviewproduct);
            userproname = itemView.findViewById(R.id.userproductviewname);
            userproprice = itemView.findViewById(R.id.userproductviewprice);
            userproctg = itemView.findViewById(R.id.usercallnow);
            message =itemView.findViewById(R.id.userviewdetails);
            viewimg =itemView.findViewById(R.id.viewim);

        }
    }

}
