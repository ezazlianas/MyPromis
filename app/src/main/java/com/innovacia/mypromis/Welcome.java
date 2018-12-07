package com.innovacia.mypromis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Welcome extends BaseActivity {


    SessionManager session;
    TextView tvTitle, tvUserName, tvProName, tvProID;



    ProgressDialog pDialog;
    URL url;
    HttpURLConnection conn;
    //LOKASI FILE PHP DI SERVER
    String URL_LOGOUT = "http://www.innovacia.com.my/promise/mobile/mobLogout.php";
    //DAPATKAN RESPONSE DARI SERVER
    String strResponse;
    StringBuilder sbResult;

    CoordinatorLayout coordinatorLayout;
    String strUserName;
    String strProID, strProName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_welcome, null, false);
        drawer.addView(contentView, 0);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvProName = (TextView) findViewById(R.id.tvProName);
        tvProID = (TextView) findViewById(R.id.tvProID);

        //START SESSION
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        String id = user.get(SessionManager.KEY_ID);
        strUserName = user.get(SessionManager.KEY_USERNAME);
        strProName = user.get(SessionManager.KEY_PRO_NAME);
        strProID = user.get(SessionManager.KEY_PRO_ID);

        tvTitle.setText("Welcome " + strUserName);
        //getProID();
        tvProName.setText(strProName);
        tvProID.setText(strProID);




        FloatingActionButton fabLogout;
        fabLogout = (FloatingActionButton) findViewById(R.id.fabLogout);

        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(Welcome.this)
                        .setTitle("PROMIS")
                        .setMessage("Sure to logout?")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.d("MainActivity", "Sending atomic bombs to Jupiter");
                                new logOut().execute();
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

                //Snackbar.make(coordinatorLayout, "Standby to logout...", Snackbar.LENGTH_LONG)
                //         .setAction("Action", null).show();


            }
        });



    }


    //GUNAKAN ASYNTASK UNTUK OPERASI YANG PANJANG
    private class logOut extends AsyncTask<String, String, String> {

        //SEBELUM MULA ASYNTASK
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //TUNJUKKAN PROGRESS DIALOG
            pDialog = new ProgressDialog(Welcome.this);
            pDialog.setMessage("Wait! ...");
            pDialog.setCancelable(true);
            pDialog.show();

        }


        //START BACKGROUND PROCESS
        protected String doInBackground(String... params) {

            try {

                url = new URL(URL_LOGOUT);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000); //MILLISECOND
                conn.setConnectTimeout(15000); //MILLISECOND
                conn.setRequestMethod("POST");
                conn.setDoInput(true); //UNTUK TERIMA INPUT DARI PHP
                conn.setDoOutput(true); //UNTUK HANTAR OUTPUT

                conn.connect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                int response_code = conn.getResponseCode();

                //SEMAK JIKA RESPONSE DARI SERVER ADALAH OK
                if (response_code == HttpURLConnection.HTTP_OK) {

                    //BACA DATA YANG DITERIMA DARI SERVER
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    sbResult = new StringBuilder();

                    while ((strResponse = reader.readLine()) != null) {
                        sbResult.append(strResponse);
                    }

                    //JIKA BERJAYA, DAPATKAN RESULT DARI SERVER
                    return (sbResult.toString());

                } else {

                    //JIKA TIDAK BERJAYA
                    return ("No response from server!");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        /**
         * APABILA ASYNTASK SIAP
         **/
        protected void onPostExecute(String params) {
            //TUTUP DIALOG BILA SIAP
            pDialog.dismiss();

            session.logoutUser();

        }
    }




}
