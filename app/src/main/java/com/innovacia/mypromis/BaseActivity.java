package com.innovacia.mypromis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    ImageView imageview;
    TextView tvID, tvUserName;
    TextView tvProName;

    DrawerLayout drawer;
    NavigationView navigationView;
    //FloatingActionButton fab;
    Toolbar toolbar;

    SessionManager session;
    String strProID, strProName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        String id = user.get(SessionManager.KEY_ID);
        String username = user.get(SessionManager.KEY_USERNAME);
        strProID = user.get(SessionManager.KEY_PRO_ID);
        strProName = user.get(SessionManager.KEY_PRO_NAME);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //fab = (FloatingActionButton) findViewById(R.id.fab);

        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Snackbar.make(view, "gapo dio Base Act", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //    }
        //});



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //drawer.OpenDrawer((int)GravityFlags.Left);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerview = navigationView.getHeaderView(0);
        tvUserName = (TextView)headerview.findViewById(R.id.tvUserName);
        tvID = (TextView)headerview.findViewById(R.id.tvID);
        //tvProName = (TextView)headerview.findViewById(R.id.tvProName);


        imageview = (ImageView)headerview.findViewById(R.id.imageView);


        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "header click", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });


        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "profile image click", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });


        tvUserName.setText(username);
        tvID.setText(id);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.main, menu);
        return true;

        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.search) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_alert) {
            // Handle the camera action
            Intent i = new Intent(getApplicationContext(), FirebaseActivity.class);
            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else if (id == R.id.nav_project) {
            Intent i = new Intent(getApplicationContext(), Project.class);
            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else if (id == R.id.nav_material) {
            Intent i = new Intent(getApplicationContext(), Material.class);
            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        } else if (id == R.id.nav_manpower) {
            Intent i = new Intent(getApplicationContext(), Manpower.class);
            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        } else if (id == R.id.nav_machinery) {
            Intent i = new Intent(getApplicationContext(), Machinery.class);
            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

        } else if (id == R.id.nav_photo) {
            Intent i = new Intent(getApplicationContext(), SitePhoto.class);
            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            Intent i = new Intent(getApplicationContext(), Logout.class);
            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
