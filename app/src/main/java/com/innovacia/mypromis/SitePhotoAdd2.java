package com.innovacia.mypromis;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class SitePhotoAdd2 extends BaseActivity {

    //Web api url
    public static final String DATA_URL = "http://www.innovacia.com.my/promise/mobile/mobSitePhotoAdd.php";

    //Tag values to read from json
    public static final String TAG_IMAGE_URL = "image";
    public static final String TAG_NAME = "name";

    //GridView Object
    private GridView gridView;

    //ArrayList for Storing image urls and titles
    private ArrayList<String> images;
    private ArrayList<String> names;

    Button btnSelect;
    TextView tvProName;
    EditText etDesc;
    ImageView imageView;

    String strDate, strDesc, strName;
    FloatingActionButton fabUpload;

    private int STORAGE_PERMISSION_CODE = 23;

    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private String UPLOAD_URL ="http://www.innovacia.com.my/promise/mobile/mobSitePhotoAdd.php";
    private String KEY_PHOTO_NAME = "sitePhotoName";
    private String KEY_PHOTO_DESC = "sitePhotoDesc";
    private String KEY_PHOTO_DATE = "sitePhotoDate";

    ProgressDialog pDialog;
    URL url;
    HttpURLConnection conn;
    String strResponse;
    StringBuilder sbResult;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_site_photo_add, null, false);
        drawer.addView(contentView, 0);

        btnSelect = (Button) findViewById(R.id.btnSelect);
        tvProName = (TextView) findViewById(R.id.tvProName);
        etDesc = (EditText) findViewById(R.id.etDesc);
        imageView = (ImageView) findViewById(R.id.imageView);


        if(isReadStorageAllowed()){
            //If permission is already having then showing the toast
            Toast.makeText(SitePhotoAdd2.this,"You already have the permission",Toast.LENGTH_LONG).show();
            //Existing the method with return
            //return;
        }

        //If the app has not the permission then asking for the permission
        requestStoragePermission();

        getDate();
        //START SESSION
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        strProName = user.get(SessionManager.KEY_PRO_NAME);
        strProID = user.get(SessionManager.KEY_PRO_ID);
        tvProName.setText(strProName);


        btnSelect.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(SitePhotoAdd2.this,"to select...",Toast.LENGTH_LONG).show();

                //Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(i, 100);
                showFileChooser();
            }
        });



        fabUpload = (FloatingActionButton) findViewById(R.id.fabUpload);
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SitePhotoAdd2.this,"to upload...",Toast.LENGTH_LONG).show();



            }
        });


    }


    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        //return true;
        //If permission is not granted returning false
        return false;
    }

    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Toast.makeText(this,"You need permission to access the image storage!",Toast.LENGTH_LONG).show();

        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private void getDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        strDate = mdformat.format(calendar.getTime());


    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }



    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(SitePhotoAdd2.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(SitePhotoAdd2.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                strDesc = etDesc.getText().toString().trim();

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_PHOTO_DATE, image);
                params.put(KEY_PHOTO_DESC, strDesc);
                params.put(KEY_PHOTO_NAME, "waklu");


                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class addMaterial extends AsyncTask<String, String, String> {

        //SEBELUM MULA ASYNTASK
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //DAPATKAN NAMA USER DAN PASSWORD
            strDesc =  etDesc.getText().toString();
            getDate();

            //TUNJUKKAN PROGRESS DIALOG
            pDialog = new ProgressDialog(SitePhotoAdd2.this);
            pDialog.setMessage("Submitting! ...");
            pDialog.setCancelable(true);
            pDialog.show();

        }


        //START BACKGROUND PROCESS
        protected String doInBackground(String... params) {

            try {

                url = new URL(UPLOAD_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000); //MILLISECOND
                conn.setConnectTimeout(15000); //MILLISECOND
                conn.setRequestMethod("POST");
                conn.setDoInput(true); //UNTUK TERIMA INPUT DARI PHP
                conn.setDoOutput(true); //UNTUK HANTAR OUTPUT

                //HANTAR MAKLUMAT USER DAN PASSWORD KE SERVER
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("proID",strProID)
                        .appendQueryParameter("sitePhotoAdd",strDate)
                        .appendQueryParameter("sitePhotoDesc",strDesc);
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

            //clearText();
            //Snackbar.make(coordinatorLayout, "Report submitted!", Snackbar.LENGTH_LONG)
            //        .setAction("Action", null).show();



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


}