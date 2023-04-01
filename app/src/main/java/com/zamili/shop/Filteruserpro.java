package com.zamili.shop;

import android.widget.Filter;

import java.util.ArrayList;

public class Filteruserpro extends Filter {
    private Adapterforuser adapterproductseller;
    private ArrayList<usermodel> filterlist;

    public Filteruserpro(Adapterforuser adapterproductseller, ArrayList<usermodel> filterlist) {
        this.adapterproductseller = adapterproductseller;
        this.filterlist = filterlist;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if(charSequence != null && charSequence.length() > 0)
        {
            charSequence = charSequence.toString().toUpperCase();

            ArrayList<usermodel> filtermodel = new ArrayList<>();
            for(int i=0; i< filterlist.size(); i++)
            {
                if(filterlist.get(i).getProname().toUpperCase().contains(charSequence))
                {
                    filtermodel.add(filterlist.get(i));
                }


            }
            results.count = filtermodel.size();
            results.values = filtermodel;
        }
        else
        {
            results.count = filterlist.size();
            results.values = filterlist;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterproductseller.userprolist = (ArrayList<usermodel>) filterResults.values;

        //refresh adapter
        adapterproductseller.notifyDataSetChanged();

    }
}
