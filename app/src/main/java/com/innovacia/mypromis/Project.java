package com.innovacia.mypromis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Project extends BaseActivity  {

    String JsonProject = "http://www.innovacia.com.my/promise/mobile/mobProject.php";
    private static final String TAG_JSON = "project";
    public static String TAG_PRO_ID= "proID";
    public static String TAG_PRO_NAME= "proName";
    public static String TAG_PRO_LOCATION= "proLocation";
    public static String TAG_PRO_CLIENT= "proClient";
    public static String TAG_PRO_VALUE= "proCostEst";



    JSONObject jsonobject;
    JSONArray jsonarray;
    StringBuilder sb;
    ListView lvProject;
    ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> userList;
    String strProID, strProName, strProLocation, strProClient, strProValue;
    String proIDSelect, proNameSelect;

    SimpleAdapter projectAdapter;
    //CoordinatorLayout coordinatorLayout;

    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_project, null, false);
        drawer.addView(contentView, 0);

        //setContentView(R.layout.activity_project);


        //coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        lvProject = (ListView) findViewById(R.id.lvProject);
        session = new SessionManager(getApplicationContext());


        new loadMat().execute();

    }


    //ASYNTASK DIGUNAKAN UNTUK PROSES YANG PANJANG
    private class loadMat extends AsyncTask<String, String, String> {

        //ArrayList<HashMap<String, String>> userList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Project.this);
            pDialog.setMessage("Loading project. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            URL url;
            sb = new StringBuilder();
            try {
                url = new URL(JsonProject);
                //CREATE CONNECTION TO SERVER
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

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

            DecimalFormat formatter = new DecimalFormat("#,###.00");

            try {
                jsonobject = new JSONObject(sb.toString());
                jsonarray = jsonobject.getJSONArray(TAG_JSON);

                //LOOP JSON UNTUK DAPATKAN MAKLUMAT
                for (int i = 0; i < jsonarray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject c = jsonarray.getJSONObject(i);
                    strProID = c.getString(TAG_PRO_ID);
                    strProName = c.getString(TAG_PRO_NAME);
                    strProLocation = c.getString(TAG_PRO_LOCATION);
                    strProClient = c.getString(TAG_PRO_CLIENT);
                    strProValue = c.getString(TAG_PRO_VALUE);

                    map.put(TAG_PRO_ID, strProID);
                    map.put(TAG_PRO_NAME, strProName);
                    map.put(TAG_PRO_LOCATION, strProLocation);
                    map.put(TAG_PRO_CLIENT, strProClient);
                    double dblProValue = Double.parseDouble(strProValue);
                    strProValue =  formatter.format(dblProValue);
                    map.put(TAG_PRO_VALUE, "RM" + strProValue);

                    userList.add(map);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            projectAdapter = new SimpleAdapter(Project.this, userList, R.layout.project_list_detail,
                    new String[] {TAG_PRO_NAME, TAG_PRO_LOCATION,TAG_PRO_CLIENT,TAG_PRO_VALUE},
                    new int[] {R.id.tvProName,R.id.tvProLocation,R.id.tvProClient,R.id.tvProCostEst});


            lvProject.setAdapter(projectAdapter);


            lvProject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected item text from ListView
                    //String selectedItem = (String) parent.getItemAtPosition(position);
                    proIDSelect = userList.get(position).get("proID");
                    proNameSelect = userList.get(position).get("proName");

                    //Snackbar.make(coordinatorLayout, "Project: " + proNameSelect, Snackbar.LENGTH_LONG)
                    //        .setAction("Action", null).show();


                    new AlertDialog.Builder(Project.this)
                            .setTitle("Select Project...")
                            .setMessage(proNameSelect)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Log.d("MainActivity", "Sending atomic bombs to Jupiter");
                                    //new Welcome.logOut().execute();
                                    session.saveProject(proIDSelect, proNameSelect);


                                    Intent intent = new Intent(getBaseContext(), Welcome.class);
                                    //intent.putExtra("PROJECT_NAME", strProName);
                                   // intent.putExtra("PROJECT_ID", strProID);
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
            });




        }
    }




}
