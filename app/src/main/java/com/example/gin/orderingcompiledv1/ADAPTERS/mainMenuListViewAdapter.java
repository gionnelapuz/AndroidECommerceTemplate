package com.example.gin.orderingcompiledv1.ADAPTERS;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gin.orderingcompiledv1.R;

/**
 * Created by Gin on 5/14/2017.
 */

public class mainMenuListViewAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] web;

    public mainMenuListViewAdapter(Activity context, String[] web) {
        super(context, R.layout.mainmenu_list_item, web);
        this.context = context;
        this.web = web;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.mainmenu_list_item, null, true);


        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        txtTitle.setText(web[position]);

        return rowView;
    }
}
