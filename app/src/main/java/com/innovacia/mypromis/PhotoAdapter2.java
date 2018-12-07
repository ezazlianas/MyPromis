package com.innovacia.mypromis;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Belal on 10/18/2017.
 */

public class PhotoAdapter2 extends RecyclerView.Adapter<PhotoAdapter2.ProductViewHolder> implements Filterable {


    private Context mCtx;
    private List<Photo> productList;
    String urlPhoto = "http://www.innovacia.com.my/promise/sitephoto/";


    public ArrayList<Photo> employeeArrayList;
    public ArrayList<Photo> orig;


    public PhotoAdapter2(Context mCtx, List<Photo> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.activity_site_photo_detail, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Photo product = productList.get(position);

        //loading the image
        Glide.with(mCtx)
                .load(product.getSitePhotoName())
                .into(holder.ivSitePhotoName);

        holder.tvSitePhotoDate.setText(product.getSitePhotoDate());
        holder.tvSitePhotoDesc.setText(product.getSitePhotoDesc());




    }

    @Override
    public int getItemCount() {
        return productList.size();
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

        TextView tvSitePhotoDate, tvSitePhotoDesc;
        ImageView ivSitePhotoName;

        public ProductViewHolder(View itemView) {
            super(itemView);

            tvSitePhotoDate = itemView.findViewById(R.id.tvDate);
            tvSitePhotoDesc = itemView.findViewById(R.id.tvDesc);
            ivSitePhotoName = itemView.findViewById(R.id.ivPhoto);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RecyclerView", "onClick：" + getAdapterPosition());
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){
                        Photo clickedDataItem = productList.get(pos);
                        Toast.makeText(v.getContext(), "ID: " + clickedDataItem.getId(), Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(mCtx, "clicked: " + pos, Toast.LENGTH_LONG).show();
                }
            });

        }


    }



}
