package com.example.gin.orderingcompiledv1;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gin.orderingcompiledv1.ADAPTERS.viewSingleOrderAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class viewSingleOrder extends AppCompatActivity {

    private TextView orderID, orderUserNumber, orderDateTime, orderNameJSON;
    private ListView listView;

    private String id, number, dateTime, JSON;

    private ArrayList<String> orderName;
    private ArrayList<String> orderSize;
    private ArrayList<String> orderQuantity;
    private viewSingleOrderAdapter adapter;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_order);

        orderName = new ArrayList<>();
        orderSize = new ArrayList<>();
        orderQuantity = new ArrayList<>();

        orderID = (TextView) findViewById(R.id.orderID);
        orderUserNumber = (TextView) findViewById(R.id.orderUserNumber);
        orderDateTime = (TextView) findViewById(R.id.orderDateTime);
        orderNameJSON = (TextView) findViewById(R.id.orderNameJSON);

        listView = (ListView) findViewById(R.id.listViewSingleOrder);
        adapter = new viewSingleOrderAdapter(viewSingleOrder.this, orderName, orderSize, orderQuantity);
        listView.setAdapter(adapter);

         Intent intent = getIntent();
         id = intent.getStringExtra("orderID");
         number = intent.getStringExtra("orderUserNumber");
         dateTime = intent.getStringExtra("orderDateTime");
         JSON = intent.getStringExtra("orderNameJSON");

        orderID.setText(id);
        orderUserNumber.setText(number);
        orderDateTime.setText(dateTime);
        orderNameJSON.setText(JSON);

        try {
            JSONArray jsonarray = new JSONArray(JSON);
            for(int i=0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String name = jsonobject.getString("name");
                String size = jsonobject.getString("size");
                String quantity = jsonobject.getString("quantity");

                orderName.add(name);
                orderSize.add(size);
                orderQuantity.add(quantity);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        actionBar = getSupportActionBar();
        actionBar.setTitle("ORDER DETAILS");
    }
}
