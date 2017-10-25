package com.example.gin.orderingcompiledv1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.example.gin.orderingcompiledv1.ADAPTERS.cartListViewAdapter;
import com.example.gin.orderingcompiledv1.SQLITEDATABASE.cartDatabase;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import static com.example.gin.orderingcompiledv1.R.id.fab;

public class cartPage extends AppCompatActivity {

    private View rootView;

    private ArrayList<String> orderid;
    private ArrayList<String> orderName;
    private ArrayList<String> orderSize;
    private ArrayList<String> orderQuantity;
    private ArrayList<String> orderTotal;

    private cartDatabase db;
    private cartListViewAdapter adapter;

    private TextView textTotal;
    private ListView listView;

    final Context context = this;

    public static final String SHARED_PREF_NAME = "Amount";
    public static final String TOTALAMOUNT = "totalAmount";

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_page);

        rootView=findViewById(R.id.activity_cart_page);

        db = new cartDatabase(this);

        orderid = new ArrayList<>();
        orderName = new ArrayList<>();
        orderSize = new ArrayList<>();
        orderQuantity = new ArrayList<>();
        orderTotal = new ArrayList<>();

        textTotal = (TextView) findViewById(R.id.textOrderSumTotal);
        Button btnCheckout = (Button) findViewById(R.id.btnCheckout);
        listView = (ListView) findViewById(R.id.listView);

        actionBar = getSupportActionBar();
        actionBar.setTitle("YOUR CART");

        adapter = new cartListViewAdapter(cartPage.this, orderid, orderName, orderSize, orderQuantity, orderTotal);
        listView.setAdapter(adapter);

        Cursor data = db.getCartItems();
        if(data.getCount() == 0){
            btnCheckout.setVisibility(View.GONE);
        }
        else
        {
            btnCheckout.setVisibility(View.VISIBLE);
            data.moveToFirst();
            do{
                orderid.add(data.getString(0));
                orderName.add(data.getString(1));
                orderSize.add(data.getString(2));
                orderQuantity.add(data.getString(3));
                orderTotal.add(data.getString(4));
            } while (data.moveToNext());
        }
        data.close();

        Double totals = 0.00;
        for(String s : orderTotal)
        {
            totals += Double.parseDouble(s);
        }

        DecimalFormat format = new DecimalFormat("0.00");
        String formatted = format.format(totals);
        textTotal.setText(formatted);

        listView.setEmptyView(findViewById(R.id.emptyView));

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle("ARE YOU FINISHED ORDERING?");
                alertDialogBuilder
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                double total = Double.parseDouble(textTotal.getText().toString());
                               String totalAmount = Double.toString(total);
                                    if(total >= 50 )
                                    {
                                        SharedPreferences sharedPreferences = cartPage.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(TOTALAMOUNT, totalAmount);
                                        editor.apply();

                                        Intent intent = new Intent(cartPage.this, mapsPage.class);
                                        startActivity(intent);
                                        finish();
                                }
                                else
                                {
                                    Snackbar snackbar = Snackbar.make(rootView, "MINIMUM AMOUNT IS 50.00 Dirhams", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                   }
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    public void updateTotal(String amount)
    {
        textTotal.setText(amount);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(cartPage.this, menuPage.class);
        startActivity(intent);
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu_clearcart, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.clearCart:
                db.deleteCartItems();
                Intent intent = new Intent(cartPage.this, menuPage.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

