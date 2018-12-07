package com.innovacia.mypromis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Logout extends BaseActivity {


    ProgressDialog pDialog;
    URL url;
    HttpURLConnection conn;
    //LOKASI FILE PHP DI SERVER
    String URL_LOGOUT = "http://www.innovacia.com.my/promise/mobile/mobLogout.php";
    //DAPATKAN RESPONSE DARI SERVER
    String strResponse;
    StringBuilder sbResult;

    CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_logout, null, false);
        drawer.addView(contentView, 0);

        //coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);


        FloatingActionButton fab;
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Snackbar.make(coordinatorLayout, "Standby to logout...", Snackbar.LENGTH_LONG)
               //         .setAction("Action", null).show();
                new logOut().execute();

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
            pDialog = new ProgressDialog(Logout.this);
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

                //HANTAR MAKLUMAT USER DAN PASSWORD KE SERVER
                //Uri.Builder builder = new Uri.Builder()
                //        .appendQueryParameter("username",strUserName)
                //        .appendQueryParameter("password", strHashPwd);
                //String query = builder.build().getEncodedQuery();

                //TO WRITE OUTPUT
                //OutputStream os = conn.getOutputStream();
                //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                //writer.write(query);

                //writer.flush();
                //writer.close();
                //os.close();
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
            finish();
            //startActivity(new Intent(Logout.this, LoginActivity.class));
            //finish();

            //APAKAH TINDAKAN BILA DAPAT JAWAPAN DARI SERVER
            //if (sbResult.toString().equals("true")) {
            //    startActivity(new Intent(Logout.this, LoginActivity.class));
            //    finish();
            //} else {
                //TOAST JIKA USER DAN PASSWORD TIDAK BENAR
            //    Toast.makeText(Logout.this, "User not found!", Toast.LENGTH_SHORT).show();
                //Snackbar.make(coordinatorLayout, "Something is wrong. Fail to logout!", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();


           // }
        }
    }




}
