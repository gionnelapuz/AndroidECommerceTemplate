    package com.example.gin.orderingcompiledv1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gin.orderingcompiledv1.ADAPTERS.cartListViewAdapter;
import com.example.gin.orderingcompiledv1.SQLITEDATABASE.cartDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


    public class checkOutPage extends AppCompatActivity {

        public static final String SHARED_PREF_NAME = "ordereds";
        public static final String ORDERED = "orderedss";

        private JSONArray orderNameArray;
        private JSONObject orderNameObject;

        private cartDatabase db;

        Random random = new Random();

        private String email, emailRandomText, ORDERID, location;
        private String randomText = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        private int RANDOM_STR_LENGTH = 30;

        private View rootView;
        private TextView textUsername, textCheckOutTotal, textChangeLocation, textViewItems, orderID, textPhoneNumber, latLong;
        private EditText editPhoneNumber;

        private ActionBar actionBar;

        final Context context = this;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_check_out_page);

            db = new cartDatabase(this);

            rootView = findViewById(R.id.activity_check_out_page);

            textUsername = (TextView) findViewById(R.id.username);
            textCheckOutTotal = (TextView) findViewById(R.id.textCheckOutTotal);
            textChangeLocation = (TextView) findViewById(R.id.textChangeLocation);
            textViewItems = (TextView) findViewById(R.id.textViewItems);
            orderID = (TextView) findViewById(R.id.orderID);
            textPhoneNumber = (TextView) findViewById(R.id.textPhoneNumber);
            latLong = (TextView) findViewById(R.id.latLong);

            editPhoneNumber = (EditText) findViewById(R.id.editPhoneNumber);

            actionBar = getSupportActionBar();
            actionBar.setTitle("ORDER DETAILS");

            Button btnCancel = (Button) findViewById(R.id.btnCancel);
            Button btnPayment = (Button) findViewById(R.id.btnPayment);

            SharedPreferences sharedPreferences = getSharedPreferences(loginPage.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            email = sharedPreferences.getString(loginPage.EMAIL_SHARED_PREF, "Not Available");
            textUsername.setText(email);

            SharedPreferences sharedPreferencesss = getSharedPreferences(mapsPage.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            location = sharedPreferencesss.getString(mapsPage.LATLONG, "Not Available");
            latLong.setText(location);

            emailRandomText = randomText.concat(email);

            orderID.setText(getRandomString());
            ORDERID = orderID.getText().toString().trim();

            SharedPreferences sharedPreferencess = getSharedPreferences(cartPage.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String totalAmount = sharedPreferencess.getString(cartPage.TOTALAMOUNT, "Not Available");
            double total = Double.parseDouble(totalAmount);
            DecimalFormat format = new DecimalFormat("0.00");
            String formatted = format.format(total);
            textCheckOutTotal.setText(formatted);


            textChangeLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(checkOutPage.this, mapsPage.class);
                    startActivity(intent);
                    // finish();
                }
            });

            textViewItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(checkOutPage.this, cartPage.class);
                    startActivity(intent);
                    //     finish();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);
                    alertDialogBuilder.setTitle("CANCEL YOUR ORDER?");
                    alertDialogBuilder
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    SharedPreferences preferences = getSharedPreferences(loginPage.SHARED_PREF_NAME,Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(cartPage.TOTALAMOUNT, "");
                                    editor.apply();

                                    SharedPreferences preferencess = getSharedPreferences(loginPage.SHARED_PREF_NAME,Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editors = preferencess.edit();
                                    editors.putString(mapsPage.LATLONG, "");
                                    editors.apply();

                                    Intent intent = new Intent(checkOutPage.this, menuPage.class);
                                    startActivity(intent);
                                    finish();

                                    db.deleteCartItems();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                }
            });

            btnPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);
                    alertDialogBuilder.setTitle("PROCEED WITH ORDER?");
                    alertDialogBuilder
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    order();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

            Cursor data = db.getCartSpecificItems();
            orderNameArray = new JSONArray();
            data.moveToFirst();
            while(data.isAfterLast() == false)
            {
                int totalColumn = data.getColumnCount();
                orderNameObject = new JSONObject();
                for (int i = 0; i < totalColumn; i++)
                {
                    try {
                        orderNameObject.put(data.getColumnName(i), data.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                orderNameArray.put(orderNameObject);
                data.moveToNext();
            }
            data.close();
            }


        private void order(){
            textPhoneNumber.setText("+639"+editPhoneNumber.getText().toString().trim());
            final String number = textPhoneNumber.getText().toString().trim();
            final String location = latLong.getText().toString().trim();

          StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_INSERT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            db.deleteCartItems();

                            SharedPreferences preferences = getSharedPreferences(loginPage.SHARED_PREF_NAME,Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(cartPage.TOTALAMOUNT, "");
                            editor.apply();

                            SharedPreferences preferencess = getSharedPreferences(loginPage.SHARED_PREF_NAME,Context.MODE_PRIVATE);
                            SharedPreferences.Editor editors = preferencess.edit();
                            editors.putString(mapsPage.LATLONG, "");
                            editors.apply();

                            SharedPreferences sharedPreferences = checkOutPage.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editorss = sharedPreferences.edit();
                            editorss.putBoolean(ORDERED, true);
                            editorss.apply();

                            Toast.makeText(checkOutPage.this, "ORDER SUCCESSFUL", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(checkOutPage.this, viewOrdersPage.class);
                            startActivity(intent);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if (error instanceof NetworkError) {
                                Snackbar snackbar = Snackbar.make(rootView, "Please connect to the Internet, and Try Again", Snackbar.LENGTH_INDEFINITE);
                                snackbar.show();
                            }
                            else if (error instanceof TimeoutError) {
                               Snackbar snackbar = Snackbar.make(rootView, "Check if you have Internet Access, and Try Again", Snackbar.LENGTH_INDEFINITE);
                                snackbar.show();
                            }
                            else if (error instanceof NoConnectionError) {
                                Snackbar snackbar = Snackbar.make(rootView, "Check if you have Internet Access, and Try Again", Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        order();
                                    }
                                });
                                snackbar.show();
                            }
                            }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put(Constants.UNIQUEORDERID,ORDERID);
                    params.put(Constants.USERNAME,email);
                    params.put(Constants.USERNUMBER,number);
                    params.put(Constants.ORDERNAME,orderNameArray.toString());
                    params.put(Constants.LATLONG,location);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }


        public String getRandomString(){
            StringBuffer randStr = new StringBuffer();
            for (int i = 0; i < RANDOM_STR_LENGTH; i++) {
                int number = getRandomNumber();
                char ch = emailRandomText.charAt(number);
                randStr.append(ch);
            }
            return randStr.toString();
        }

        private int getRandomNumber() {
            int randomInt = 0;
            randomInt = random.nextInt(emailRandomText.length());
            if (randomInt - 1 == -1) {
                return randomInt;
            } else {
                return randomInt - 1;
            }
        }


        @Override
        public void onBackPressed() {
            Intent intent = new Intent(checkOutPage.this, menuPage.class);
            startActivity(intent);
            finish();
        }
    }

