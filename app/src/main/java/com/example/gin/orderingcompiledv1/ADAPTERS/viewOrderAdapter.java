package com.example.gin.orderingcompiledv1.ADAPTERS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gin.orderingcompiledv1.R;
import com.example.gin.orderingcompiledv1.SQLITEDATABASE.cartDatabase;
import com.example.gin.orderingcompiledv1.cartPage;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Gin on 5/29/2017.
 */

public class viewOrderAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<String> orderID;
    private ArrayList<String> orderUserNumber;
    private ArrayList<String> orderDateTime;
    //private ArrayList<String> orderName;
    //private ArrayList<String> orderSize;
    //private ArrayList<String> orderQuantity;
    private ArrayList<String> orderNameJSON;

    public viewOrderAdapter(Context context, ArrayList<String> orderID, ArrayList<String> orderUserNumber, ArrayList<String> orderDateTime, ArrayList<String> orderNameJSON){
        this.context = context;
        this.orderID = orderID;
        this.orderUserNumber = orderUserNumber;
        //this.orderName = orderName;
        //this.orderSize = orderSize;
       // this.orderQuantity = orderQuantity;
        this.orderDateTime = orderDateTime;
        this.orderNameJSON = orderNameJSON;
    }

    @Override
    public int getCount() {
        return orderID.size();
    }

    @Override
    public Object getItem(int position) {
        return orderID.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View listView;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = inflater.inflate(R.layout.view_orders_item, parent, false);

        TextView id = (TextView) listView.findViewById(R.id.orderID);
        TextView number = (TextView) listView.findViewById(R.id.orderUserNumber);
        //TextView name = (TextView) listView.findViewById(R.id.orderName);
        TextView nameJSON = (TextView) listView.findViewById(R.id.orderNameJSON);
        //TextView size = (TextView) listView.findViewById(R.id.orderSize);
        //TextView quantity = (TextView) listView.findViewById(R.id.orderQuantity);
        TextView dateTime = (TextView) listView.findViewById(R.id.orderDateTime);


        id.setText(orderID.get(position));
        number.setText(orderUserNumber.get(position));
       // name.setText(orderName.get(position));
        nameJSON.setText(orderNameJSON.get(position));
       // size.setText(orderSize.get(position));
       // quantity.setText(orderQuantity.get(position));
        dateTime.setText(orderDateTime.get(position));

        return listView;
    }
}
