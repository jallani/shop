package com.zamili.shop;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapterproductseller extends RecyclerView.Adapter<Adapterproductseller.HolderProductseller> implements Filterable {

    private Context context;
    public ArrayList<Modelproduct> productList,filterlist;
    private Filterproducts filter;

     public Adapterproductseller( Context context,ArrayList<Modelproduct> productList)
     {
         this.context = context;
         this.productList = productList;
         this.filterlist = productList;
     }

    @NonNull
    @Override
    public HolderProductseller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rowproductseller,parent,false);

        return new HolderProductseller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductseller holder, int position) {

        Modelproduct modelproduuct = productList.get(position);
//        Log.d("mData2", "onDataChange: " + modelproduuct.getUid());
//        Log.d("mData2", "onDataChange: " + modelproduuct.getProImage());
//        Log.d("mData2", "onDataChange: " + modelproduuct.getProname());
//
//        Log.d("mData2", "onDataChange: " + modelproduuct.getProphonenumber());
//        Log.d("mData2", "onDataChange: " + modelproduuct.getProprice());
//        Log.d("mData2", "onDataChange: " + modelproduuct.getProductId());
//
        String proid = modelproduuct.getProductId();
        String uid = modelproduuct.getUid();
        String dbproname = modelproduuct.getProname();
        String dbproprice = modelproduuct.getProprice();
        String dbpronumber = modelproduuct.getProphonenumber();
          String img = modelproduuct.getProImage();


        //set text
//        holder.proname.setText(dbproname);
//        holder.proprice.setText(dbpronumber);
//        holder.callpro.setText("RS :"+ dbproprice);
        holder.proname.setText(modelproduuct.getProname());
        holder.proprice.setText("RS:"+modelproduuct.getProprice());
        holder.callpro.setText(modelproduuct.getProphonenumber());

        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n = new Intent(context,sendmessage.class);
                context.startActivity(n);
            }
        });


        try{
            Picasso.get().load(img).placeholder(R.drawable.ic_baseline_shopping_cart_checkout_24).into(holder.productIconvi);

        }catch(Exception e)
        {
            holder.productIconvi.setImageResource(R.drawable.ic_baseline_shopping_cart_checkout_24);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailBottomSheet(modelproduuct);

            }
        });

    }

    private void detailBottomSheet(Modelproduct modelproduuct) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bs_product_details_seller, null);

        bottomSheetDialog.setContentView(view);
        ImageButton backbtn = view.findViewById(R.id.backbutton);
        ImageButton deletebtn = view.findViewById(R.id.deleteproduct);
        ImageButton editbtn = view.findViewById(R.id.editproduct);
        ImageView imgview = view.findViewById(R.id.imagedetails);

        TextView  detailproname = view.findViewById(R.id.detailproname);
        TextView  detailproprice = view.findViewById(R.id.detailproprice);
        TextView  detailpronumber = view.findViewById(R.id.detailpronumber);

        String dbproname = modelproduuct.getProname();
        String dbproprice = modelproduuct.getProprice();
        String dbpronumber = modelproduuct.getProphonenumber();
        String proid = modelproduuct.getProductId();
        String uid = modelproduuct.getUid();
        String img = modelproduuct.getProImage();

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

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,EditProduct.class);
                intent.putExtra("productId",proid);
                context.startActivity(intent);

            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete").setMessage("Are you want to delete product "+dbproname+"?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteProduct(proid);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void deleteProduct(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Product Delete ......", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
        {
            filter=new Filterproducts(this,filterlist);
        }
        return  filter;
    }

    class HolderProductseller extends RecyclerView.ViewHolder{
        private ImageView productIconvi;
        private TextView callpro;
        private TextView proname;
        private TextView proprice;
        private Button message;

        public HolderProductseller(@NonNull View itemView) {
            super(itemView);

            productIconvi = itemView.findViewById(R.id.viewproduct);
            proname = itemView.findViewById(R.id.productviewname);
            proprice = itemView.findViewById(R.id.productviewprice);
            callpro = itemView.findViewById(R.id.callnow);
            message = itemView.findViewById(R.id.viewdetails);





        }
    }
}
