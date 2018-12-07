package com.innovacia.mypromis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

public class Material extends BaseActivity implements SearchView.OnQueryTextListener{

    //String JsonProject = "http://www.innovacia.com.my/promise/report/mobProInfo.php";
    String JsonURL ="http://www.innovacia.com.my/promise/mobile/mobMaterial.php";
    String urlDeleteMat ="http://www.innovacia.com.my/promise/mobile/mobMaterialDel.php";


    RequestQueue requestQueue;

    private static final String TAG_JSON = "material";
    public static String TAG_ID = "id";
    public static String TAG_DESC= "siteMatDesc";
    public static String TAG_DATE = "siteMatDate";
    public static String TAG_UNIT = "siteMatUnit";
    public static String TAG_COST= "siteMatCost";


    //private static final String TAG_JSON_PROJECT = "project";
    //public static String TAG_PRO_NAME= "proName";

    //ArrayList<HashMap<String, String>> arraySend = new ArrayList<>();
    ProgressDialog pDialog;
    FloatingActionButton fabAdd, fabRefresh;

    ListView lvMaterial;
    String strID, strDate, strDesc, strUnit, strCost;
    String strProName;
    JSONObject jsonobject;
    JSONArray jsonarray;
    StringBuilder sb;
    SimpleAdapter userAdapter;
    ArrayList<HashMap<String, String>> userList;

    //ArrayList<HashMap<String, String>> projectList;
    //ArrayList<String> projectList;
    //ArrayList<String> listItems=new ArrayList<>();


    //Spinner spnProject;

    Toolbar toolbar;
    TextView tvProName, tvProID;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_material, null, false);
        drawer.addView(contentView, 0);



        lvMaterial = (ListView) findViewById(R.id.lvMaterial);
        tvProName = (TextView) findViewById(R.id.tvProName);
        tvProID = (TextView) findViewById(R.id.tvProID);

        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabRefresh = (FloatingActionButton) findViewById(R.id.fabRefresh);


        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        strProName = user.get(SessionManager.KEY_PRO_NAME);
        strProID = user.get(SessionManager.KEY_PRO_ID);
        tvProName.setText(strProName);
        tvProID.setText(strProID);

        if(TextUtils.isEmpty(strProName)) {
            new AlertDialog.Builder(Material.this)
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






        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new loadMat().execute();

            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Material.this, MaterialAdd.class));

                //Snackbar.make(view, "To add material report", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

            }
        });


        lvMaterial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                strID = userList.get(position).get(TAG_ID);
                //Toast.makeText(view.getContext(), strID, Toast.LENGTH_SHORT).show();


                new AlertDialog.Builder(Material.this)
                        .setTitle("PROMIS")
                        .setMessage("Delete this record?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new deleteMat().execute();
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

        //new loadProject().execute();


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        //userAdapter.filter(text);
        userAdapter.getFilter().filter(newText);
        return false;
    }


    //ASYNTASK DIGUNAKAN UNTUK PROSES YANG PANJANG
    private class loadMat extends AsyncTask<String, String, String> {

        //ArrayList<HashMap<String, String>> userList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Material.this);
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
                    strDesc = c.getString(TAG_DESC);
                    strUnit = c.getString(TAG_UNIT);
                    strCost = c.getString(TAG_COST);

                    map.put(TAG_ID, strID);
                    map.put(TAG_DATE, strDate);
                    map.put(TAG_DESC, strDesc);
                    map.put(TAG_UNIT, strUnit);
                    map.put(TAG_COST, "RM" + strCost);

                    userList.add(map);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            userAdapter = new SimpleAdapter(Material.this, userList, R.layout.activity_material_detail,
                    new String[] {TAG_ID, TAG_DATE, TAG_DESC,TAG_UNIT,TAG_COST},
                    new int[] {R.id.tvID,R.id.tvSiteMapDate,R.id.tvSiteMatDesc,R.id.tvSiteMatUnit,R.id.tvSiteMatCost});


            lvMaterial.setAdapter(userAdapter);



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
    private class deleteMat extends AsyncTask<String, String, String> {

        //ArrayList<HashMap<String, String>> userList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Material.this);
            pDialog.setMessage("Loading report. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            URL url;
            sb = new StringBuilder();
            try {
                url = new URL(urlDeleteMat);
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

            new loadMat().execute();

        }
    }



}
