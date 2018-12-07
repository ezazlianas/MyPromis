package com.innovacia.mypromis;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;

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

public class Manpower extends BaseActivity implements SearchView.OnQueryTextListener{


    String JsonURL ="http://www.innovacia.com.my/promise/mobile/mobManpower.php";
    String urlDeleteManpower ="http://www.innovacia.com.my/promise/mobile/mobManpowerDel.php";


    RequestQueue requestQueue;

    private static final String TAG_JSON = "manpower";
    public static String TAG_ID = "id";
    public static String TAG_DESIG= "siteManDesig";
    public static String TAG_DATE = "siteManDate";
    public static String TAG_UNIT = "siteManUnit";
    public static String TAG_COST= "siteManCost";

    ProgressDialog pDialog;
    FloatingActionButton fabAdd, fabRefresh;

    ListView lvManpower;
    String strID, strDate, strDesig, strUnit, strCost;
    JSONObject jsonobject;
    JSONArray jsonarray;
    StringBuilder sb;
    SimpleAdapter userAdapter;
    ArrayList<HashMap<String, String>> userList;

    SessionManager session;
    TextView tvProName, tvProID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_manpower, null, false);
        drawer.addView(contentView, 0);

        lvManpower = (ListView) findViewById(R.id.lvManpower);
        tvProName = (TextView) findViewById(R.id.tvProName);

        tvProID = (TextView) findViewById(R.id.tvProID);


        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabRefresh = (FloatingActionButton) findViewById(R.id.fabRefresh);


        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new loadManpower().execute();

            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Manpower.this, ManpowerAdd.class));

                //Snackbar.make(view, "To add manpower report", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

            }
        });


        //CHECK SESSION
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        strProName = user.get(SessionManager.KEY_PRO_NAME);
        strProID = user.get(SessionManager.KEY_PRO_ID);
        tvProName.setText(strProName);
        tvProID.setText(strProID);



        lvManpower.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                strID = userList.get(position).get(TAG_ID);
                //Toast.makeText(view.getContext(), strID, Toast.LENGTH_SHORT).show();


                new AlertDialog.Builder(Manpower.this)
                        .setTitle("PROMIS")
                        .setMessage("Delete this record?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new deleteManpower().execute();
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
        });


    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        userAdapter.getFilter().filter(newText);
        return false;
    }


    //ASYNTASK DIGUNAKAN UNTUK PROSES YANG PANJANG
    private class loadManpower extends AsyncTask<String, String, String> {

        //ArrayList<HashMap<String, String>> userList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Manpower.this);
            pDialog.setMessage("Loading report. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            URL url;
            sb = new StringBuilder();
            try {
                url = new URL(JsonURL);
                //CREATE CONNECTION TO SERVER
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //HANTAR MAKLUMAT USER DAN PASSWORD KE SERVER
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("proID", strProID);
                String query = builder.build().getEncodedQuery();

                //TO WRITE OUTPUT
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);

                writer.flush();
                writer.close();
                os.close();

                //DAPATKAN RESPONSE DARI SERVER
                String response;
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    sb = new StringBuilder();

                    while ((response = br.readLine()) != null){
                        sb.append(response);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String args) {
            //DISMISS DIALOG
            pDialog.dismiss();
            userList = new ArrayList<HashMap<String, String>>();

            try {
                jsonobject = new JSONObject(sb.toString());
                jsonarray = jsonobject.getJSONArray(TAG_JSON);

                //LOOP JSON UNTUK DAPATKAN MAKLUMAT
                for (int i = 0; i < jsonarray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject c = jsonarray.getJSONObject(i);

                    strID = c.getString(TAG_ID);
                    strDate = c.getString(TAG_DATE);
                    strDesig = c.getString(TAG_DESIG);
                    strUnit = c.getString(TAG_UNIT);
                    strCost = c.getString(TAG_COST);

                    map.put(TAG_ID, strID);
                    map.put(TAG_DATE, strDate);
                    map.put(TAG_DESIG, strDesig);
                    map.put(TAG_UNIT, strUnit);
                    map.put(TAG_COST, "RM" + strCost);

                    userList.add(map);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            userAdapter = new SimpleAdapter(Manpower.this, userList, R.layout.activity_manpower_detail,
                    new String[] {TAG_ID, TAG_DATE, TAG_DESIG,TAG_UNIT,TAG_COST},
                    new int[] {R.id.tvID,R.id.tvSiteManDate,R.id.tvSiteManDesig,R.id.tvSiteManUnit,R.id.tvSiteManCost});


            lvManpower.setAdapter(userAdapter);



        }
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




    //ASYNTASK DIGUNAKAN UNTUK PROSES YANG PANJANG
    private class deleteManpower extends AsyncTask<String, String, String> {

        //ArrayList<HashMap<String, String>> userList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Manpower.this);
            pDialog.setMessage("Loading report. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            URL url;
            sb = new StringBuilder();
            try {
                url = new URL(urlDeleteManpower);
                //CREATE CONNECTION TO SERVER
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //HANTAR MAKLUMAT USER DAN PASSWORD KE SERVER
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", strID);
                String query = builder.build().getEncodedQuery();

                //TO WRITE OUTPUT
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);


                writer.flush();
                writer.close();
                os.close();

                //DAPATKAN RESPONSE DARI SERVER
                String response;
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    sb = new StringBuilder();

                    while ((response = br.readLine()) != null){
                        sb.append(response);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String args) {
            //DISMISS DIALOG
            pDialog.dismiss();

            new loadManpower().execute();

        }
    }



}
