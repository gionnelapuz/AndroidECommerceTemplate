package com.example.gin.orderingcompiledv1.ADAPTERS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gin.orderingcompiledv1.R;

import java.util.ArrayList;

/**
 * Created by Gin on 6/2/2017.
 */

public class viewSingleOrderAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<String> orderName;
    private ArrayList<String> orderSize;
    private ArrayList<String> orderQuantity;

    public viewSingleOrderAdapter(Context context, ArrayList<String> orderName, ArrayList<String> orderSize, ArrayList<String> orderQuantity){
        this.context = context;
        this.orderName = orderName;
        this.orderSize = orderSize;
        this.orderQuantity = orderQuantity;
    }

    @Override
    public int getCount() {
        return orderName.size();
    }

    @Override
    public Object getItem(int position) {
        return orderName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View listView;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = inflater.inflate(R.layout.view_single_orders_item, parent, false);

        TextView name = (TextView) listView.findViewById(R.id.orderName);
        TextView size = (TextView) listView.findViewById(R.id.orderSize);
        TextView quantity = (TextView) listView.findViewById(R.id.orderQuantity);

        name.setText(orderName.get(position));
        size.setText(orderSize.get(position));
        quantity.setText(orderQuantity.get(position));

        return listView;
    }

}
