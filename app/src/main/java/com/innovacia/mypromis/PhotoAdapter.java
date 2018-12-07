package com.innovacia.mypromis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Belal on 10/18/2017.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ProductViewHolder> implements Filterable {


    private Context mCtx;
    //private List<Photo> productList;
    String urlPhoto = "http://www.innovacia.com.my/promise/sitephoto/";
    String urlDeletePhoto ="http://www.innovacia.com.my/promise/mobile/mobPhotoDel.php";

    ProgressDialog pDialog;
    StringBuilder sb;
    String strID;
    SitePhoto sitePhoto;



    public ArrayList<Photo> employeeArrayList;
    public ArrayList<Photo> orig;


    public PhotoAdapter(Context context, ArrayList<Photo> employeeArrayList) {
        super();
        this.mCtx = context;
        this.employeeArrayList = employeeArrayList;
    }


    //public PhotoAdapter(Context mCtx, List<Photo> productList) {
    //    this.mCtx = mCtx;
    //    this.productList = productList;
    //}

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.activity_site_photo_detail, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Photo product = employeeArrayList.get(position);

        //loading the image
        Glide.with(mCtx)
                .load(product.getSitePhotoName())
                .into(holder.ivSitePhotoName);

        holder.tvSitePhotoDate.setText(product.getSitePhotoDate());
        holder.tvSitePhotoDesc.setText(product.getSitePhotoDesc());
        holder.tvSitePhotoName.setText(product.getSitePhotoName());




    }

    @Override
    public int getItemCount() {
        return employeeArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Photo> results = new ArrayList<Photo>();
                if (orig == null)
                    orig = employeeArrayList;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final Photo g : orig) {
                            if (g.getSitePhotoDate().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                employeeArrayList = (ArrayList<Photo>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvSitePhotoDate, tvSitePhotoDesc, tvSitePhotoName;
        ImageView ivSitePhotoName;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvSitePhotoDate = itemView.findViewById(R.id.tvDate);
            tvSitePhotoDesc = itemView.findViewById(R.id.tvDesc);
            tvSitePhotoName = itemView.findViewById(R.id.tvName);

            ivSitePhotoName = itemView.findViewById(R.id.ivPhoto);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RecyclerView", "onClick：" + getAdapterPosition());
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){
                        Photo clickedDataItem = employeeArrayList.get(pos);
                        strID = String.valueOf(clickedDataItem.getId());

                        //Toast.makeText(v.getContext(), "ID: " + clickedDataItem.getId(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(v.getContext(), "ID: " + strID, Toast.LENGTH_LONG).show();


                    }


                    new AlertDialog.Builder(v.getContext())
                            .setTitle("PROMIS")
                            .setMessage("Delete this photo?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new deletePhoto().execute();
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

                    //Toast.makeText(mCtx, "clicked: " + pos, Toast.LENGTH_LONG).show();
                }
            });

        }


    }




    //ASYNTASK DIGUNAKAN UNTUK PROSES YANG PANJANG
    public class deletePhoto extends AsyncTask<String, String, String> {

        //ArrayList<HashMap<String, String>> userList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mCtx);
            pDialog.setMessage("Loading report. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            URL url;
            sb = new StringBuilder();
            try {
                url = new URL(urlDeletePhoto);
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

            //sitePhoto = new SitePhoto();
            //sitePhoto.loadPhoto();

            //new Machinery.loadMachinery().execute();

        }
    }



}
