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
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.example.gin.orderingcompiledv1.R;
import com.example.gin.orderingcompiledv1.SQLITEDATABASE.cartDatabase;
import com.example.gin.orderingcompiledv1.cartPage;

/**
 * Created by Gin on 5/14/2017.
 */

public class cartListViewAdapter extends BaseAdapter {


    private Context context;

    private ArrayList<String> orderid;
    private ArrayList<String> orderName;
    private ArrayList<String> orderSize;
    private ArrayList<String> orderQuantity;
    private ArrayList<String> orderTotal;

    public cartListViewAdapter(Context context, ArrayList<String> orderid, ArrayList<String> orderName, ArrayList<String> orderSize, ArrayList<String> orderQuantity, ArrayList<String> orderTotal){
        this.context = context;
        this.orderid = orderid;
        this.orderName = orderName;
        this.orderSize = orderSize;
        this.orderQuantity = orderQuantity;
        this.orderTotal = orderTotal;
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

        final cartDatabase db = new cartDatabase(context);

        final View listView;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = inflater.inflate(R.layout.cart_item, null);

        TextView number = (TextView) listView.findViewById(R.id.textID);
        TextView name = (TextView) listView.findViewById(R.id.textOrderName);
        TextView size = (TextView) listView.findViewById(R.id.textOrderSize);
        TextView quantity = (TextView) listView.findViewById(R.id.textOrderQuantity);
        final TextView total = (TextView) listView.findViewById(R.id.textOrderTotal);

        ImageButton btnRemove = (ImageButton) listView.findViewById(R.id.btnRemove);

        number.setText(orderid.get(position));
        name.setText(orderName.get(position));
        size.setText(orderSize.get(position));
        quantity.setText(orderQuantity.get(position));
        total.setText(orderTotal.get(position));

//BUTTON TO REMOVE ROW
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(context)
                        .setTitle("REMOVE" + " " + orderName.get(position) + " " + "FROM CART?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                db.removeCartItem(orderid.get(position));
                                orderName.remove(position);
                                orderSize.remove(position);
                                orderQuantity.remove(position);
                                orderTotal.remove(position);
                                notifyDataSetChanged();

                                db.close();



                                Double totals = 0.00;
                                for(String s : orderTotal){
                                    totals += Double.parseDouble(s);
                                }
                                DecimalFormat format = new DecimalFormat("0.00");
                                String formatted = format.format(totals);
                                ((cartPage)context).updateTotal(String.valueOf(formatted));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        return listView;
    }
}
