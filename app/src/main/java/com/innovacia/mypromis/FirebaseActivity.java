package com.innovacia.mypromis;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

/**
 */
public class FirebaseActivity extends BaseActivity {

    private static final String TAG = FirebaseActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage, tvStatus;
    Button btnActivate;

    SessionManager session;
    String strAlertStatus;
    CoordinatorLayout coordinatorLayout;


    String regId, regStatus;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_firebase, null, false);
        drawer.addView(contentView, 0);


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        //START SESSION
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        strAlertStatus = user.get(SessionManager.KEY_ALERT_STATUS);

        //SHARE PREF INIT
        sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);


        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvStatus.setText(strAlertStatus);


        txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);

        btnActivate = (Button) findViewById(R.id.btnActivate);
        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateAlert();
                //displayFirebaseRegId();

            }
        });




        FloatingActionButton fabClose;
        fabClose = (FloatingActionButton) findViewById(R.id.fabClose);

        fabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Welcome.class);
                i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

                //Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                //homeIntent.addCategory( Intent.CATEGORY_HOME );
                //homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(homeIntent);
                //Snackbar.make(coordinatorLayout, "Standby to logout...", Snackbar.LENGTH_LONG)
                //         .setAction("Action", null).show();

            }
        });

        FloatingActionButton fabRefresh;
        fabRefresh = (FloatingActionButton) findViewById(R.id.fabRefresh);

        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayFirebaseRegId();
                Snackbar.make(coordinatorLayout, "Refreshed", Snackbar.LENGTH_LONG)
                         .setAction("Action", null).show();

            }
        });


    }


    private void activateAlert()
    {
        //displayFirebaseRegId();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( FirebaseActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                String token = instanceIdResult.getToken();
                Log.d("FCM_TOKEN",token);

                //SUBSCRIBE TOPIC
                FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_PROMIS);

                strAlertStatus = "Activated";
                //SAVE TO SESSION/PREF
                session.saveAlert(strAlertStatus);

                tvStatus.setText(strAlertStatus);
                txtRegId.setText("Registered");


                Snackbar.make(coordinatorLayout, "Alert Activated!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();




            }
        });



    }
    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        //sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = sharedPreferences.getString("regId", null);
        //regStatus = sharedPreferences.getString("regStatus", null);


        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            //txtRegId.setText("Registration ID: " + regId);
            tvStatus.setText(strAlertStatus);
        }
        else {
            //txtRegId.setText("Not registered!");
            tvStatus.setText(strAlertStatus);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
