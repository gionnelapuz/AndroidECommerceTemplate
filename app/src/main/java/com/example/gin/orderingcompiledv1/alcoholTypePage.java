package com.example.gin.orderingcompiledv1;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.example.gin.orderingcompiledv1.ADAPTERS.alcoholTypeGridViewAdapter;
import com.example.gin.orderingcompiledv1.SQLITEDATABASE.cartDatabase;

public class alcoholTypePage extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ActionBar actionBar;
    private View rootView;

    private TextView alcoholName, textView,textPrices,textSize,
            textBottleCase, textQuantityNumber,textQuantity,
            textTotal, stringTextPrice,stringTextPrices,
            stringTextSize, stringTextSizes,stringTextQuantity;

    private RadioGroup radioSize, radioQuantity;
    private RadioButton size1, size2, quantity1, quantity2;

    private Button btnAdd;
    private FloatingActionButton fab;
    private ImageButton btnMinus,btnPlus;

    private GridView gridView;
    private LinearLayout layoutQuantity,layoutTotal;

    int counter = 1;
    private boolean ordered = false;

    final Context context = this;

    private cartDatabase db;

    private ArrayList<String> images;
    private ArrayList<String> names;
    private ArrayList<String> price;
    private ArrayList<String> prices;
    private ArrayList<String> size;
    private ArrayList<String> sizes;
    private ArrayList<String> quantity;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol_type_page);

        db = new cartDatabase(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        images = new ArrayList<>();
        names = new ArrayList<>();
        price = new ArrayList<>();
        prices = new ArrayList<>();
        size = new ArrayList<>();
        sizes = new ArrayList<>();
        quantity = new ArrayList<>();

        textView = (TextView) findViewById(R.id.textView);
        gridView = (GridView) findViewById(R.id.gridView);
        rootView=findViewById(R.id.activity_alcohol_type_page);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        Intent intent = getIntent();
        String str = intent.getStringExtra("type");
        textView.setText(str);

        actionBar = getSupportActionBar();
        actionBar.setTitle(str);

        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(alcoholTypePage.this, cartPage.class);
                startActivity(intent);
                finish();
            }
        });

        alcoholTypeGridViewAdapter adapter = new alcoholTypeGridViewAdapter(alcoholTypePage.this, images, names, price, prices, size, sizes, quantity);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    final Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog);

                    dialog.setContentView(R.layout.alcohol_type_dialog);

                    alcoholName = (TextView) dialog.findViewById(R.id.alcoholName);
                    textPrices = (TextView) dialog.findViewById(R.id.textPrices);
                    textSize = (TextView) dialog.findViewById(R.id.textSize);
                    textBottleCase = (TextView) dialog.findViewById(R.id.textBottleCase);
                    textQuantityNumber = (TextView) dialog.findViewById(R.id.textQuantityNumber);
                    textQuantity = (TextView) dialog.findViewById(R.id.textQuantity);
                    textTotal = (TextView) dialog.findViewById(R.id.textTotal);

                    stringTextPrice = (TextView) dialog.findViewById(R.id.stringTextPrice);
                    stringTextPrices = (TextView) dialog.findViewById(R.id.stringTextPrices);
                    stringTextSize = (TextView) dialog.findViewById(R.id.stringTextSize);
                    stringTextSizes = (TextView) dialog.findViewById(R.id.stringTextSizes);
                    stringTextQuantity = (TextView) dialog.findViewById(R.id.stringTextQuantity);

                    alcoholName.setText(names.get(position));
                    textQuantityNumber.addTextChangedListener(watcher);

                    btnAdd = (Button) dialog.findViewById(R.id.btnAdd);
                    btnPlus = (ImageButton) dialog.findViewById(R.id.btnPlus);
                    btnMinus = (ImageButton) dialog.findViewById(R.id.btnMinus);
                    btnMinus.setEnabled(false);

                    layoutQuantity = (LinearLayout) dialog.findViewById(R.id.linearLayout);
                    layoutTotal = (LinearLayout) dialog.findViewById(R.id.layoutTotal);

                    radioSize = (RadioGroup) dialog.findViewById(R.id.radioSize);
                    size1 = (RadioButton) dialog.findViewById(R.id.size1);
                    size2 = (RadioButton) dialog.findViewById(R.id.size2);

                    radioQuantity = (RadioGroup) dialog.findViewById(R.id.radioQuantity);
                    quantity1 = (RadioButton) dialog.findViewById(R.id.quantity1);
                    quantity2 = (RadioButton) dialog.findViewById(R.id.quantity2);
                    quantity1.setVisibility(View.GONE);
                    quantity2.setVisibility(View.GONE);
                    layoutQuantity.setVisibility(View.VISIBLE);
                    layoutTotal.setVisibility(View.VISIBLE);

                    textQuantityNumber.setText(Integer.toString(counter));

                    stringTextPrice.setText(price.get(position));
                    stringTextPrices.setText(prices.get(position));
                    stringTextSize.setText(size.get(position));
                    stringTextSizes.setText(sizes.get(position));
                    stringTextQuantity.setText(quantity.get(position));

                    String splitSizes = sizes.get(position);
                    final String splitPrices = prices.get(position);

                    radioQuantity.clearCheck();

                    StringTokenizer splitSizes1 = new StringTokenizer(splitSizes, ",");
                    String splitsizess1 = splitSizes1.nextToken();

                    textSize.setText(splitsizess1);

                    if(stringTextPrice.getText().toString().contains("one"))
                    {
                        textPrices.setText(prices.get(position));
                        size2.setVisibility(View.GONE);
                        textTotal.setText(textPrices.getText().toString());
                    }

                    if(stringTextPrice.getText().toString().contains("two"))
                    {
                        StringTokenizer splitPrices1 = new StringTokenizer(splitPrices, ",");
                        final String splitprices1 = splitPrices1.nextToken();

                        textPrices.setText(splitprices1);
                        textTotal.setText(textPrices.getText().toString());
                    }

                    if(stringTextSize.getText().toString().contains("one"))
                    {
                        String sizess = sizes.get(position);
                        size1.setText(sizess);
                        size2.setVisibility(View.GONE);
                    }

                    if(stringTextSize.getText().toString().contains("two"))
                    {
                        StringTokenizer tokens = new StringTokenizer(splitSizes, ",");
                        String sizes1 = tokens.nextToken();
                        String sizes2 = tokens.nextToken();

                        size1.setText(sizes1);
                        size2.setText(sizes2);
                    }

                    if(stringTextQuantity.getText().toString().contains("bottle"))
                    {
                        quantity2.setVisibility(View.GONE);
                    }

                    if(stringTextQuantity.getText().toString().contains("both"))
                    {
                        quantity2.setVisibility(View.VISIBLE);
                    }

                    btnPlus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            counter++;
                            textQuantityNumber.setText(Integer.toString(counter));

                            Double  num1 = Double.parseDouble(textPrices.getText().toString());
                            Double  num2 = Double.parseDouble(textQuantityNumber.getText().toString());
                            Double newtotal = num1 * num2;

                            DecimalFormat format = new DecimalFormat("0.00");
                            String formatted = format.format(newtotal);

                            textTotal.setText(formatted);
                        }
                    });
                    btnMinus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            counter--;
                            textQuantityNumber.setText(Integer.toString(counter));

                            Double  num1 = Double.parseDouble(textPrices.getText().toString());
                            Double  num2 = Double.parseDouble(textTotal.getText().toString());
                            Double newtotal = num2 - num1;

                            DecimalFormat format = new DecimalFormat("0.00");
                            String formatted = format.format(newtotal);

                            textTotal.setText(formatted);
                        }
                    });

                    radioSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            StringTokenizer tokenss = new StringTokenizer(splitPrices, ",");
                            final String prices1 = tokenss.nextToken();
                            final String prices2 = tokenss.nextToken();

                            if (checkedId == R.id.size1) {
                                String size = size1.getText().toString().trim();
                                textPrices.setText(prices1);
                                textSize.setText(size);
                                textTotal.setText(textPrices.getText().toString().trim());

                                counter = 1;
                                textQuantityNumber.setText(Integer.toString(counter));

                            } else if (checkedId == R.id.size2) {
                                String size = size2.getText().toString().trim();
                                textPrices.setText(prices2);
                                textSize.setText(size);
                                textTotal.setText(textPrices.getText().toString().trim());

                                counter = 1;
                                textQuantityNumber.setText(Integer.toString(counter));

                            }
                        }
                    });

                    radioQuantity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            if (checkedId == R.id.quantity1)
                            {
                                String bottlecase = quantity1.getText().toString().trim();
                                textBottleCase.setText(bottlecase);
                                textTotal.setText(textPrices.getText().toString().trim());
                                layoutQuantity.setVisibility(View.VISIBLE);
                                layoutTotal.setVisibility(View.VISIBLE);

                            }
                            else if (checkedId == R.id.quantity2)
                            {
                                String bottlecase = quantity2.getText().toString().trim();
                                textBottleCase.setText(bottlecase);
                                textTotal.setText(textPrices.getText().toString().trim());
                                layoutQuantity.setVisibility(View.VISIBLE);
                                layoutTotal.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (radioSize.getCheckedRadioButtonId() == -1)
                            {
                                Toast.makeText(alcoholTypePage.this, "Please choose Size!",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String name = names.get(position);
                                String size = textSize.getText().toString().trim();
                                String quantityNumber = textQuantityNumber.getText().toString().trim();
                                String bottleCase = textBottleCase.getText().toString().trim();
                                String total = textTotal.getText().toString().trim();

                                textQuantity.setText(quantityNumber + " " + bottleCase);

                                String bottleCaseQuantity = textQuantity.getText().toString().trim();

                                db.addToCart(name,size,bottleCaseQuantity, total);

                                Toast.makeText(alcoholTypePage.this, "ADDED TO CART", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            counter = 1;
                            textQuantityNumber.setText(Integer.toString(counter));
                        }
                    });
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.alcohol_type_dialog);

                    alcoholName = (TextView) dialog.findViewById(R.id.alcoholName);
                    textPrices = (TextView) dialog.findViewById(R.id.textPrices);
                    textSize = (TextView) dialog.findViewById(R.id.textSize);
                    textBottleCase = (TextView) dialog.findViewById(R.id.textBottleCase);
                    textQuantityNumber = (TextView) dialog.findViewById(R.id.textQuantityNumber);
                    textQuantity = (TextView) dialog.findViewById(R.id.textQuantity);
                    textTotal = (TextView) dialog.findViewById(R.id.textTotal);

                    stringTextPrice = (TextView) dialog.findViewById(R.id.stringTextPrice);
                    stringTextPrices = (TextView) dialog.findViewById(R.id.stringTextPrices);
                    stringTextSize = (TextView) dialog.findViewById(R.id.stringTextSize);
                    stringTextSizes = (TextView) dialog.findViewById(R.id.stringTextSizes);
                    stringTextQuantity = (TextView) dialog.findViewById(R.id.stringTextQuantity);

                    alcoholName.setText(names.get(position));
                    textQuantityNumber.addTextChangedListener(watcher);

                    btnAdd = (Button) dialog.findViewById(R.id.btnAdd);
                    btnPlus = (ImageButton) dialog.findViewById(R.id.btnPlus);
                    btnMinus = (ImageButton) dialog.findViewById(R.id.btnMinus);
                    btnMinus.setEnabled(false);

                    layoutQuantity = (LinearLayout) dialog.findViewById(R.id.linearLayout);
                    layoutTotal = (LinearLayout) dialog.findViewById(R.id.layoutTotal);

                    radioSize = (RadioGroup) dialog.findViewById(R.id.radioSize);
                    size1 = (RadioButton) dialog.findViewById(R.id.size1);
                    size2 = (RadioButton) dialog.findViewById(R.id.size2);

                    radioQuantity = (RadioGroup) dialog.findViewById(R.id.radioQuantity);
                    quantity1 = (RadioButton) dialog.findViewById(R.id.quantity1);
                    quantity2 = (RadioButton) dialog.findViewById(R.id.quantity2);
                    quantity1.setVisibility(View.GONE);
                    quantity2.setVisibility(View.GONE);
                    layoutQuantity.setVisibility(View.VISIBLE);
                    layoutTotal.setVisibility(View.VISIBLE);

                    textQuantityNumber.setText(Integer.toString(counter));

                    stringTextPrice.setText(price.get(position));
                    stringTextPrices.setText(prices.get(position));
                    stringTextSize.setText(size.get(position));
                    stringTextSizes.setText(sizes.get(position));
                    stringTextQuantity.setText(quantity.get(position));

                    textTotal.setText(textPrices.getText().toString());

                    String splitSizes = sizes.get(position);
                    final String splitPrices = prices.get(position);

                    radioQuantity.clearCheck();

                    StringTokenizer splitSizes1 = new StringTokenizer(splitSizes, ",");
                    String splitsizess1 = splitSizes1.nextToken();

                    textSize.setText(splitsizess1);

                    if(stringTextPrice.getText().toString().contains("one"))
                    {
                        textPrices.setText(prices.get(position));
                        size2.setVisibility(View.GONE);
                        textTotal.setText(textPrices.getText().toString());
                    }

                    if(stringTextPrice.getText().toString().contains("two"))
                    {
                        StringTokenizer splitPrices1 = new StringTokenizer(splitPrices, ",");
                        final String splitprices1 = splitPrices1.nextToken();

                        textPrices.setText(splitprices1);
                    }

                    if(stringTextSize.getText().toString().contains("one"))
                    {
                        String sizess = sizes.get(position);
                        size1.setText(sizess);
                        size2.setVisibility(View.GONE);
                    }

                    if(stringTextSize.getText().toString().contains("two"))
                    {
                        StringTokenizer tokens = new StringTokenizer(splitSizes, ",");
                        String sizes1 = tokens.nextToken();
                        String sizes2 = tokens.nextToken();

                        size1.setText(sizes1);
                        size2.setText(sizes2);
                    }

                    if(stringTextQuantity.getText().toString().contains("bottle"))
                    {
                        quantity2.setVisibility(View.GONE);
                    }

                    if(stringTextQuantity.getText().toString().contains("both"))
                    {
                        quantity2.setVisibility(View.VISIBLE);
                    }

                    btnPlus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            counter++;
                            textQuantityNumber.setText(Integer.toString(counter));

                            Double  num1 = Double.parseDouble(textPrices.getText().toString());
                            Double  num2 = Double.parseDouble(textQuantityNumber.getText().toString());
                            Double newtotal = num1 * num2;

                            DecimalFormat format = new DecimalFormat("0.00");
                            String formatted = format.format(newtotal);

                            textTotal.setText(formatted);
                        }
                    });

                    btnMinus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            counter--;
                            textQuantityNumber.setText(Integer.toString(counter));

                            Double  num1 = Double.parseDouble(textPrices.getText().toString());
                            Double  num2 = Double.parseDouble(textTotal.getText().toString());
                            Double newtotal = num2 - num1;

                            DecimalFormat format = new DecimalFormat("0.00");
                            String formatted = format.format(newtotal);

                            textTotal.setText(formatted);
                        }
                    });

                    radioSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            StringTokenizer tokenss = new StringTokenizer(splitPrices, ",");
                            final String prices1 = tokenss.nextToken();
                            final String prices2 = tokenss.nextToken();

                            if (checkedId == R.id.size1) {
                                String size = size1.getText().toString().trim();
                                textPrices.setText(prices1);
                                textSize.setText(size);
                                textTotal.setText(textPrices.getText().toString().trim());

                                counter = 1;
                                textQuantityNumber.setText(Integer.toString(counter));

                            } else if (checkedId == R.id.size2) {
                                String size = size2.getText().toString().trim();
                                textPrices.setText(prices2);
                                textSize.setText(size);
                                textTotal.setText(textPrices.getText().toString().trim());

                                counter = 1;
                                textQuantityNumber.setText(Integer.toString(counter));
                            }
                        }
                    });

                    radioQuantity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            if (checkedId == R.id.quantity1)
                            {
                                String bottlecase = quantity1.getText().toString().trim();
                                textBottleCase.setText(bottlecase);
                                textTotal.setText(textPrices.getText().toString().trim());
                                layoutQuantity.setVisibility(View.VISIBLE);
                                layoutTotal.setVisibility(View.VISIBLE);

                            }
                            else if (checkedId == R.id.quantity2)
                            {
                                String bottlecase = quantity2.getText().toString().trim();
                                textBottleCase.setText(bottlecase);
                                textTotal.setText(textPrices.getText().toString().trim());
                                layoutQuantity.setVisibility(View.VISIBLE);
                                layoutTotal.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (radioSize.getCheckedRadioButtonId() == -1)
                            {
                                Toast.makeText(alcoholTypePage.this, "Please choose Size!",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else
                            {

                                String name = names.get(position);
                                String size = textSize.getText().toString().trim();
                                String quantityNumber = textQuantityNumber.getText().toString().trim();
                                String bottleCase = textBottleCase.getText().toString().trim();
                                String total = textTotal.getText().toString().trim();

                                textQuantity.setText(quantityNumber + " " + bottleCase);
                                String bottleCaseQuantity = textQuantity.getText().toString().trim();

                                fab.setVisibility(View.VISIBLE);

                                db.addToCart(name,size,bottleCaseQuantity, total);
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            counter = 1;
                            textQuantityNumber.setText(Integer.toString(counter));
                        }
                    });
                }
            }
        });


        getData();
    }



    //TEXTWATCHER TO ENABLE ANDDISABLE COUNTER
    private final TextWatcher watcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (textQuantityNumber.getText().toString().equals("1")) {
                btnMinus.setEnabled(false);
            } else{
                btnMinus.setEnabled(true);
            }

            if (textQuantityNumber.getText().toString().equals("100")) {
                btnPlus.setEnabled(false);
            } else{
                btnPlus.setEnabled(true);
            }
        }
        public void afterTextChanged(Editable s) {

        }
    };

    public void getData() {
        final ProgressDialog loading = ProgressDialog.show(this, "Please wait...", "Fetching data...", false, false);

        String url = Constants.URL_GETTYPE + textView.getText().toString().trim();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        showGrid(response);
                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof NetworkError) {
                            loading.dismiss();

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
                            loading.dismiss();
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
                              loading.dismiss();
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

    private void showGrid(JSONArray jsonArray) {

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = null;
            try {
                obj = jsonArray.getJSONObject(i);
                images.add(obj.getString(Constants.TAG_IMAGE_URL));
                names.add(obj.getString(Constants.TAG_NAME));
                price.add(obj.getString(Constants.TAG_PRICE));
                prices.add(obj.getString(Constants.TAG_PRICES));
                size.add(obj.getString(Constants.TAG_SIZE));
                sizes.add(obj.getString(Constants.TAG_SIZES));
                quantity.add(obj.getString(Constants.TAG_QUANTITY));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        alcoholTypeGridViewAdapter gridAlcoholAdapter = new alcoholTypeGridViewAdapter(this, images, names, price, prices, size, sizes, quantity);
        gridView.setAdapter(gridAlcoholAdapter);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    //TOOLBAR SETTINGS
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

                                Intent intent = new Intent(alcoholTypePage.this, loginPage.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                LoginManager.getInstance().logOut();
                return true;

            case R.id.view_orders:

                Intent intent = new Intent(alcoholTypePage.this, viewOrdersPage.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(alcoholTypePage.this, menuPage.class);
        startActivity(intent);
        finish();
    }

}
