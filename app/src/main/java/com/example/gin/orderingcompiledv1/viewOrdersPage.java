package com.example.gin.orderingcompiledv1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gin.orderingcompiledv1.ADAPTERS.viewOrderAdapter;
import com.example.gin.orderingcompiledv1.SQLITEDATABASE.cartDatabase;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class viewOrdersPage extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private cartDatabase db;

    private View rootView;

    private TextView textUsername;
    private String  email;
    private ListView listView;
    private ActionBar actionBar;

    private ArrayList<String> orderID;
    private ArrayList<String> orderUserNumber;
    private ArrayList<String> orderDateTime;
    private ArrayList<String> orderNameJSON;
    private viewOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders_page);

        db = new cartDatabase(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        orderID = new ArrayList<>();
        orderUserNumber = new ArrayList<>();
        orderDateTime = new ArrayList<>();
        orderNameJSON = new ArrayList<>();

        textUsername = (TextView) findViewById(R.id.username);
        rootView=findViewById(R.id.activity_view_orders_page);
        listView = (ListView) findViewById(R.id.listViewOrder);

        actionBar = getSupportActionBar();
        actionBar.setTitle("YOUR ORDERS");

        SharedPreferences sharedPreferences = getSharedPreferences(loginPage.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(loginPage.EMAIL_SHARED_PREF,"Not Available");
        textUsername.setText(email);

        adapter = new viewOrderAdapter(viewOrdersPage.this, orderID, orderUserNumber, orderDateTime, orderNameJSON);
        listView.setAdapter(adapter);

        listView.setEmptyView(findViewById(R.id.emptyView));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(viewOrdersPage.this, viewSingleOrder.class);
                intent.putExtra("orderID", orderID.get(position));
                intent.putExtra("orderUserNumber", orderUserNumber.get(position));
                intent.putExtra("orderDateTime", orderDateTime.get(position));
                intent.putExtra("orderNameJSON", orderNameJSON.get(position));
                startActivity(intent);
            }
        });


        getData();
    }

    public void getData() {
        String url = Constants.URL_GETORDER + textUsername.getText().toString().trim();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        showLIST(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {

                            Snackbar snackbar = Snackbar.make(rootView, "Please connect to the Internet", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getData();
                                }
                            });
                            snackbar.show();
                        }
                        else if (error instanceof TimeoutError) {

                            Snackbar snackbar = Snackbar.make(rootView, "Check if you have Internet Access", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getData();
                                }
                            });
                            snackbar.show();
                        }
                        else if (error instanceof NoConnectionError) {

                            Snackbar snackbar = Snackbar.make(rootView, "Check if you have Internet Access", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getData();
                                }
                            });
                            snackbar.show();
                        }
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
    private void showLIST(JSONArray jsonArray) {
        JSONObject obj = null;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                obj = jsonArray.getJSONObject(i);
                orderID.add(obj.getString(Constants.ORDERID));
                orderUserNumber.add(obj.getString(Constants.ORDERUSERNUMBER));
                orderDateTime.add(obj.getString(Constants.ORDERDATETIME));
                orderNameJSON.add(obj.getString(Constants.ORDERNAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        viewOrderAdapter listOrderAdapter = new viewOrderAdapter(this,orderID, orderUserNumber, orderDateTime, orderNameJSON);
        listView.setAdapter(listOrderAdapter);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(viewOrdersPage.this, menuPage.class);
        startActivity(intent);
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu_logoutonly, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                db.deleteCartItems();

                                SharedPreferences preferences = getSharedPreferences(loginPage.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean(loginPage.LOGGEDIN_SHARED_PREF, false);
                                editor.putString(loginPage.EMAIL_SHARED_PREF, "");
                                editor.apply();

                                SharedPreferences preferencess = getSharedPreferences(checkOutPage.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editors = preferencess.edit();
                                editors.putBoolean(checkOutPage.ORDERED, false);
                                editors.apply();

                                Intent intent = new Intent(viewOrdersPage.this, loginPage.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                LoginManager.getInstance().logOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
