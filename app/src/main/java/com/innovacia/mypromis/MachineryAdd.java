package com.innovacia.mypromis;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MachineryAdd extends AppCompatActivity {

    String URL_ADD ="http://www.innovacia.com.my/promise/mobile/mobMachineryAdd.php";

    TextView tvProName;
    EditText etDesc, etUnit, etCost;
    //Spinner spnProName;

    ProgressDialog pDialog;
    URL url;
    HttpURLConnection conn;
    String strResponse;
    StringBuilder sbResult;


    String strProID, strProName;
    String strDate, strMachDesc, strMachUnit, strMachCost;
    SessionManager session;
    CoordinatorLayout coordinatorLayout;
    FloatingActionButton fabAdd, fabClose;

    TextView tvDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machinery_add);

        etDesc = (EditText) findViewById(R.id.etDesc);
        etUnit = (EditText) findViewById(R.id.etUnit);
        etCost = (EditText) findViewById(R.id.etCost);
        tvProName = (TextView) findViewById(R.id.tvProName);
        tvDate = (TextView) findViewById(R.id.tvDate);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);


        //START SESSION
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        strProName = user.get(SessionManager.KEY_PRO_NAME);
        strProID = user.get(SessionManager.KEY_PRO_ID);
        tvProName.setText(strProName);

        getDate();
        tvDate.setText(strDate);


        if(TextUtils.isEmpty(strProName)) {
            new AlertDialog.Builder(MachineryAdd.this)
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

        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabClose = (FloatingActionButton) findViewById(R.id.fabClose);


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(etDesc.getText())) {

                    etDesc.requestFocus();
                    Snackbar.make(coordinatorLayout, "Fill in the Machinery Descriptions!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if(TextUtils.isEmpty(etUnit.getText())) {

                    etUnit.requestFocus();
                    Snackbar.make(coordinatorLayout, "Fill in the No of Unit!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if(TextUtils.isEmpty(etCost.getText())) {

                    etCost.requestFocus();
                    Snackbar.make(coordinatorLayout, "Fill in the Cost!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }


                new AlertDialog.Builder(MachineryAdd.this)
                        .setTitle("Submit report...")
                        .setMessage("Confirm to submit report?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new addMachinery().execute();
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



        fabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MachineryAdd.this, Machinery.class));
                finish();


            }
        });


    }



    //GUNAKAN ASYNTASK UNTUK OPERASI YANG PANJANG
    private class addMachinery extends AsyncTask<String, String, String> {

        //SEBELUM MULA ASYNTASK
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //DAPATKAN NAMA USER DAN PASSWORD
            strMachDesc =  etDesc.getText().toString();
            strMachUnit =  etUnit.getText().toString();
            strMachCost =  etCost.getText().toString();
            getDate();

            //TUNJUKKAN PROGRESS DIALOG
            pDialog = new ProgressDialog(MachineryAdd.this);
            pDialog.setMessage("Submitting! ...");
            pDialog.setCancelable(true);
            pDialog.show();

        }


        //START BACKGROUND PROCESS
        protected String doInBackground(String... params) {

            try {

                url = new URL(URL_ADD);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000); //MILLISECOND
                conn.setConnectTimeout(15000); //MILLISECOND
                conn.setRequestMethod("POST");
                conn.setDoInput(true); //UNTUK TERIMA INPUT DARI PHP
                conn.setDoOutput(true); //UNTUK HANTAR OUTPUT

                //HANTAR MAKLUMAT USER DAN PASSWORD KE SERVER
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("proID",strProID)
                        .appendQueryParameter("siteMachDate",strDate)
                        .appendQueryParameter("siteMachDesc",strMachDesc)
                        .appendQueryParameter("siteMachUnit",strMachUnit)
                        .appendQueryParameter("siteMachCost", strMachCost);
                String query = builder.build().getEncodedQuery();

                //TO WRITE OUTPUT
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);

                writer.flush();
                writer.close();
                os.close();
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

            clearText();
            Snackbar.make(coordinatorLayout, "Report submitted!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            //APAKAH TINDAKAN BILA DAPAT JAWAPAN DARI SERVER
            if (sbResult.toString().equals("false")) {
                //Snackbar.make(coordinatorLayout, "Problem!", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

            } else {

                //Snackbar.make(coordinatorLayout, "Material submitted to server!", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

            }
        }
    }


    private void getDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        strDate = mdformat.format(calendar.getTime());


    }

    private void clearText()
    {

        etDesc.setText("");
        etUnit.setText("");
        etCost.setText("");


    }

}
