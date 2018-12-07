package com.innovacia.mypromis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MessageShowActivity extends BaseActivity {

    private ImageView imageView;
    private TextView titleTextView;
    private TextView timeStampTextView;
    private TextView tvMessage;
    private TextView articleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_message_show, null, false);
        drawer.addView(contentView, 0);


        viewInitialization();

        //receive data from MyFirebaseMessagingService class
        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");
        String timeStamp = getIntent().getStringExtra("timestamp");
        String article = getIntent().getStringExtra("article_data");
        String imageUrl = getIntent().getStringExtra("image");

        //Set data on UI
        titleTextView.setText(title);
        timeStampTextView.setText(timeStamp);
        tvMessage.setText(message);
        articleTextView.setText(article);
       // Picasso.with(this)
        //        .load(imageUrl)
        //        .error(R.drawable.ic_menu_logout)
        //        .into(imageView);
    }

    private void viewInitialization() {
        imageView = (ImageView) findViewById(R.id.featureGraphics);
        titleTextView = (TextView) findViewById(R.id.header);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        timeStampTextView = (TextView) findViewById(R.id.timeStamp);
        articleTextView = (TextView) findViewById(R.id.article);
    }
}
