package com.innovacia.mypromis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class SitePhotoAdd  extends AppCompatActivity {


    String strProName, strProID, strDate;
    Button btnSelect;
    ImageView ivPhoto;
    EditText etPhotoDesc;
    TextView tvProName, tvPhotoName;
    Bitmap FixBitmap;
    String strPhotoName = "image_tag" ;
    String strPhotoDesc = "image_desc" ;
    String ImageName = "image_data" ;
    String strPhotoDate;


    ProgressDialog progressDialog ;
    ByteArrayOutputStream byteArrayOutputStream ;
    byte[] byteArray ;
    String ConvertImage ;
    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC ;
    BufferedReader bufferedReader ;
    StringBuilder stringBuilder;
    boolean check = true;
    private int GALLERY = 1, CAMERA = 2;


    String strURL = "http://www.innovacia.com.my/promise/mobile/mobUpload.php";
    SessionManager session;
    FloatingActionButton fabUpload, fabClose;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_photo_add);


        btnSelect = (Button)findViewById(R.id.btnSelect);

        ivPhoto = (ImageView)findViewById(R.id.ivPhoto);
        tvPhotoName=(TextView)findViewById(R.id.tvPhotoName);
        tvProName=(TextView)findViewById(R.id.tvProName);
        etPhotoDesc=(EditText)findViewById(R.id.etPhotoDesc);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        strProName = user.get(SessionManager.KEY_PRO_NAME);
        strProID = user.get(SessionManager.KEY_PRO_ID);
        tvProName.setText(strProName);


        getDate();


        byteArrayOutputStream = new ByteArrayOutputStream();

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPictureDialog();

            }
        });


        fabUpload = (FloatingActionButton) findViewById(R.id.fabUpload);
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strPhotoName = tvPhotoName.getText().toString();
                strPhotoDesc = etPhotoDesc.getText().toString();

                UploadImageToServer();

                clearText();

            }
        });


        fabClose = (FloatingActionButton) findViewById(R.id.fabClose);
        fabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              finish();

            }
        });



        if (ContextCompat.checkSelfPermission(SitePhotoAdd.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();

                String convertedPath = getRealPathFromURI(contentURI);
                //String filename = convertedPath.substring(convertedPath.lastIndexOf("/")+1);
                String filename = convertedPath.substring(convertedPath.lastIndexOf("/")+1);

                //String file;
                if (filename.indexOf(".") > 0) {
                    strPhotoName = filename.substring(0, filename.lastIndexOf("."));
                } else {
                    strPhotoName =  filename;
                }

                tvPhotoName.setText(filename);


                //Toast.makeText(SitePhotoAdd.this, strPhotoName, Toast.LENGTH_LONG).show();


                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    //String backgroundImageName = String.valueOf(ivPhoto.getTag());

                    //String path = saveImage(bitmap);
                    //Toast.makeText(SitePhotoAdd.this, backgroundImageName, Toast.LENGTH_SHORT).show();


                    ivPhoto.setImageBitmap(FixBitmap);
                    //btnUpload.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SitePhotoAdd.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            ivPhoto.setImageBitmap(FixBitmap);
            //btnUpload.setVisibility(View.VISIBLE);
            //  saveImage(thumbnail);
            //Toast.makeText(ShadiRegistrationPart5.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }


    public void UploadImageToServer(){

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                progressDialog = ProgressDialog.show(SitePhotoAdd.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();
                Toast.makeText(SitePhotoAdd.this,string1,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put("PROID", strProID);
                HashMapParams.put("PHOTODATE", strDate);
                HashMapParams.put("PHOTONAME", strPhotoName);
                HashMapParams.put("PHOTODESC", strPhotoDesc);
                HashMapParams.put(ImageName, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(strURL, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                outputStream = httpURLConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(bufferedWriterDataFN(PData));
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReader.readLine()) != null){
                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            }
            else {

                Toast.makeText(SitePhotoAdd.this, "Unable to use Camera..Please Allow us to use Camera", Toast.LENGTH_LONG).show();

            }
        }
    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        //This method was deprecated in API level 11
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(
                this,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void getDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        strDate = mdformat.format(calendar.getTime());
    }


    private void clearText()
    {

        etPhotoDesc.setText("");
        tvPhotoName.setText("");
        ivPhoto.setImageBitmap(null);
    }


}
