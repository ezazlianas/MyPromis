package com.innovacia.mypromis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 */
public class LoginActivity2 extends AppCompatActivity  {


    private static final String TAG = BaseActivity.class.getSimpleName();
    private TextView txtRegId, txtMessage;


    private EditText etUserName, etPwd;
    private Button btnLogin;

    ProgressDialog pDialog;
    URL url;
    HttpURLConnection conn;
    //LOKASI FILE PHP DI SERVER
    String URL_LOGIN = "http://www.innovacia.com.my/promise/mobile/mobLogin.php";
    //DAPATKAN RESPONSE DARI SERVER
    String strResponse;
    StringBuilder sbResult;
    String strUserName, strUserPwd, strHashPwd;
    String dbID, dbUserName, dbFullName;

    CoordinatorLayout coordinatorLayout;

    ArrayList<HashMap<String, String>> userList;
    JSONObject jsonobject;
    JSONArray jsonarray;
    StringBuilder sb;
    private static final String TAG_JSON = "user";
    public static String TAG_ID = "mem_id";
    public static String TAG_USERNAME = "username";
    public static String TAG_FULLNAME = "fullname";

    SessionManager session;
    String tkn;

    FloatingActionButton fabClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());

        txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);


        etUserName = (EditText) findViewById(R.id.etUserName);
        etPwd = (EditText) findViewById(R.id.etPwd);
        //etPwd.setPasswordVisibilityToggleEnabled(true);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new checkUser().execute();
                //Intent intent = new Intent(getApplicationContext(), Welcome.class);
                //startActivity(intent);


            }
        });

        fabClose = (FloatingActionButton) findViewById(R.id.fabClose);
        fabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);

            }
        });



        //tkn = FirebaseInstanceId.getInstance().getToken();
        //tvText.setText("Current token [" + tkn + "]");
       // Toast.makeText(LoginActivity.this, "Current token [" + tkn + "]",
       //         Toast.LENGTH_LONG).show();


    }



    //GUNAKAN ASYNTASK UNTUK OPERASI YANG PANJANG
    private class checkUser extends AsyncTask<String, String, String> {

        //SEBELUM MULA ASYNTASK
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //DAPATKAN NAMA USER DAN PASSWORD
            strUserName =  etUserName.getText().toString();
            strUserPwd =  etPwd.getText().toString();

            hash();

            //TUNJUKKAN PROGRESS DIALOG
            pDialog = new ProgressDialog(LoginActivity2.this);
            pDialog.setMessage("Wait! ...");
            pDialog.setCancelable(true);
            pDialog.show();

        }


        //START BACKGROUND PROCESS
        protected String doInBackground(String... params) {

            try {

                url = new URL(URL_LOGIN);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000); //MILLISECOND
                conn.setConnectTimeout(15000); //MILLISECOND
                conn.setRequestMethod("POST");
                conn.setDoInput(true); //UNTUK TERIMA INPUT DARI PHP
                conn.setDoOutput(true); //UNTUK HANTAR OUTPUT

                //HANTAR MAKLUMAT USER DAN PASSWORD KE SERVER
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username",strUserName)
                        .appendQueryParameter("password", strHashPwd);
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




            //PAPARKAN RESULT KE TEXTVIEW
            //txtMessage.setText(sbResult.toString());

            //APAKAH TINDAKAN BILA DAPAT JAWAPAN DARI SERVER
            if (sbResult.toString().equals("false")) {
                Snackbar.make(coordinatorLayout, "User not found!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            } else {

                userList = new ArrayList<HashMap<String, String>>();
               // sb = new StringBuilder();
                try {
                    jsonobject = new JSONObject(sbResult.toString());
                    jsonarray = jsonobject.getJSONArray(TAG_JSON);

                    //LOOP JSON UNTUK DAPATKAN MAKLUMAT
                    for (int i = 0; i < jsonarray.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject c = jsonarray.getJSONObject(i);

                        dbID = c.getString(TAG_ID);
                        dbUserName = c.getString(TAG_USERNAME);

                        map.put(TAG_ID, dbID);
                        map.put(TAG_USERNAME, dbUserName);

                        userList.add(map);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                session.createLoginSession(dbID, dbUserName);

                //TOAST JIKA USER DAN PASSWORD TIDAK BENAR
                //Toast.makeText(LoginActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                Snackbar.make(coordinatorLayout, "Welcome! " + dbUserName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                startActivity(new Intent(LoginActivity2.this, Project.class));
                finish();

                //txtMessage.setText(sbResult.toString());


            }
        }
    }


    private void hash()
    {
        //String passwordToHash = "editor";
        String passwordToHash = etPwd.getText().toString();
        //String generatedPassword = null;

        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("SHA1");
            //Add password bytes to digest
            md.update(passwordToHash.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            strHashPwd = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        //System.out.println(generatedPassword);
        txtMessage.setText(strHashPwd);

    }



}

