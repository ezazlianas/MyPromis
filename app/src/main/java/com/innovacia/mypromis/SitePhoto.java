package com.innovacia.mypromis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SitePhoto extends BaseActivity implements SearchView.OnQueryTextListener{

    private static final String URL_PRODUCTS = "http://www.innovacia.com.my/promise/mobile/mobPhoto.php";
    String urlDeletePhoto ="http://www.innovacia.com.my/promise/mobile/mobPhotoDel.php";


    String urlPhoto = "http://www.innovacia.com.my/promise/sitephoto/";

    //a list to store all the products
    //List<Photo> productList;
    private ArrayList<Photo> productList;
    private PhotoAdapter employeeAdapter;


    //the recyclerview
    RecyclerView recyclerView;
    FloatingActionButton fabAdd, fabRefresh;

    SessionManager session;
    TextView tvProName, tvProID;
    ProgressDialog pDialog;
    StringBuilder sb;

    PhotoAdapter photoAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_site_photo, null, false);
        drawer.addView(contentView, 0);

        //getting the recyclerview from xml
        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




        tvProName = (TextView) findViewById(R.id.tvProName);
        tvProID = (TextView) findViewById(R.id.tvProID);




        //initializing the productlist

        productList = new ArrayList<>();

        //this method will fetch and parse json
        //to display it in recyclerview
        //loadProducts();


        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabRefresh = (FloatingActionButton) findViewById(R.id.fabRefresh);


        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadPhoto();

            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SitePhoto.this, SitePhotoAdd.class));

            }
        });



        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        strProName = user.get(SessionManager.KEY_PRO_NAME);
        strProID = user.get(SessionManager.KEY_PRO_ID);
        tvProName.setText(strProName);
        tvProID.setText(strProID);



        if(TextUtils.isEmpty(strProName)) {
            new AlertDialog.Builder(SitePhoto.this)
                    .setTitle("Select Project...")
                    .setMessage("You must first select the Project!")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Log.d("MainActivity", "Sending atomic bombs to Jupiter");
                            Intent intent = new Intent(getBaseContext(), Project.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Log.d("MainActivity", "Aborting mission...");
                            dialog.cancel();
                        }
                    })
                    .show();
        }


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        photoAdapter.getFilter().filter(newText);
       /// adapter.get
        return false;
    }


    public void loadPhoto() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */


        StringRequest sr = new StringRequest(Request.Method.POST, URL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            productList.clear();
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                //adding the product to product list

                                productList.add(new Photo(
                                        product.getInt("id"),
                                        product.getString("sitePhotoDate"),
                                        product.getString("sitePhotoDesc"),
                                        product.getString("sitePhotoName")
                                ));
                            }

                            //creating adapter object and setting it to recyclerview
                            photoAdapter = new PhotoAdapter(SitePhoto.this, productList);
                            recyclerView.setAdapter(photoAdapter);


                            //photoAdapter = new SimpleAdapter(SitePhoto.this, productList, R.layout.activity_site_photo_detail,
                            //        new String[] {TAG_ID, TAG_DATE, TAG_DESC,TAG_UNIT,TAG_COST},
                            //        new int[] {R.id.tvID,R.id.tvSiteMapDate,R.id.tvSiteMatDesc,R.id.tvSiteMatUnit,R.id.tvSiteMatCost});

                            //lvPhoto.setAdapter(photoAdapter);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("proID",strProID);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        //queue.add(sr);
        Volley.newRequestQueue(this).add(sr);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }



}
