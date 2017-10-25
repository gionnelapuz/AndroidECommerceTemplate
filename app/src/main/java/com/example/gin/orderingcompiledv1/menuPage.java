package com.example.gin.orderingcompiledv1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import com.example.gin.orderingcompiledv1.ADAPTERS.mainMenuListViewAdapter;
import com.example.gin.orderingcompiledv1.SQLITEDATABASE.cartDatabase;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class menuPage extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView username,email;
    private FloatingActionButton fab;
    private ActionBar actionBar;

    private GoogleApiClient mGoogleApiClient;

    ListView list;
    String[] web = {
            "BURGERS",
            "PASTA",
            "CHICKEN",
            "DRINKS",
            "SIDES"};

    private cartDatabase db;

    private boolean ordered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        db = new cartDatabase(this);

        username = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        actionBar = getSupportActionBar();
        actionBar.setTitle("MAIN MENU");

        SharedPreferences sharedPreferences = getSharedPreferences(loginPage.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(loginPage.USERNAME_SHARED_PREF,"Not Available");
        String mail = sharedPreferences.getString(loginPage.EMAIL_SHARED_PREF,"Not Available");
        username.setText(name);
        email.setText(mail);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mainMenuListViewAdapter adapter = new mainMenuListViewAdapter(menuPage.this, web);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(menuPage.this, alcoholTypePage.class);
                intent.putExtra("type", web[position]);
                startActivity(intent);
                finish();
            }
        });

        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(menuPage.this, cartPage.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu_icon, menu);

         invalidateOptionsMenu();
        MenuItem menuItem = menu.findItem(R.id.view_orders);

        SharedPreferences sharedPreferences = getSharedPreferences(checkOutPage.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        ordered = sharedPreferences.getBoolean(checkOutPage.ORDERED, false);
        if(ordered){
            menuItem.setVisible(true);
        }
        else
        {
            menuItem.setVisible(false);
        }
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

                                SharedPreferences preferences = getSharedPreferences(loginPage.SHARED_PREF_NAME,Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean(loginPage.LOGGEDIN_SHARED_PREF, false);
                                editor.putString(loginPage.EMAIL_SHARED_PREF, "");
                                editor.apply();

                                Intent intent = new Intent(menuPage.this, loginPage.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                LoginManager.getInstance().logOut();
                return true;

            case R.id.view_orders:
                Intent intent = new Intent(menuPage.this, viewOrdersPage.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

