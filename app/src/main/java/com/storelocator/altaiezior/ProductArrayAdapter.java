package com.storelocator.altaiezior;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.storelocator.altaiezior.api.ProductSearchServer;

import java.util.List;

/**
 * Created by altaiezior on 20/4/15.
 */
public class ProductArrayAdapter extends ArrayAdapter<ProductSearchServer.Product> {
    private final Context context;
    private final List<ProductSearchServer.Product> values;

    public ProductArrayAdapter(Context context, List<ProductSearchServer.Product> values){
        super(context, R.layout.product_list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.product_list_item, parent, false);
        TextView mProductName = (TextView) rowView.findViewById(R.id.product_name);
        TextView mProductDescription = (TextView) rowView.findViewById(R.id.product_description);
        TextView mProductPopularity = (TextView) rowView.findViewById(R.id.product_popularity);
        TextView mProductBrand = (TextView) rowView.findViewById(R.id.product_brand);
        ProductSearchServer.Product current = values.get(position);
        if(current.getBrand()!=null)
            mProductBrand.setText(current.getBrand());
        else
            mProductBrand.setText("Not branded");
        if(current.getPopularity()!=null)
            mProductPopularity.setText(current.getPopularity().toString());
        else
            mProductPopularity.setText("0");
        if(current.getDescription()!=null)
            mProductDescription.setText(current.getDescription());
        else
            mProductDescription.setText("No description available");
        if(current.getName()!=null)
            mProductName.setText(current.getName());
        else
            mProductBrand.setText("No name");
        return rowView;
    }
}
