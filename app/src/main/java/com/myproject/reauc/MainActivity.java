package com.myproject.reauc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.myproject.reauc.data.model.LoggedInUser;
import com.myproject.reauc.ui.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        TextView displayNameView = (TextView)navigationView.getHeaderView(0).findViewById(R.id.displayName);
        displayNameView.setText(LoggedInUser.getDisplayName().concat(" ???"));
        TextView pointView = (TextView)navigationView.getHeaderView(0).findViewById(R.id.currentPoint);
        pointView.setText(Integer.toString(LoggedInUser.getPoint()).concat(" p"));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawer.closeDrawers();

                int id = menuItem.getItemId();

                if(id == R.id.nav_register){
                    replaceFragment(R.id.action_nav_register);
                }
                else if(id == R.id.nav_point){
                    String msg = "?????? ?????? ????????? : " + LoggedInUser.getPoint() + " p\n"
                            + "????????? ????????? : \n";
                    EditText et = new EditText(getApplicationContext());
                    et.setHint("00000");
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                    et.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context)
                            .setTitle("????????? ????????????")
                            .setMessage(msg)
                            .setView(et)
                            .setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (et.getText().toString().isEmpty()) {
                                        Toast.makeText(getApplicationContext(), "????????? ???????????? ??????????????????.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    else if (Integer.parseInt(et.getText().toString()) >= 10000000) {
                                        Toast.makeText(getApplicationContext(), "??? ?????? 10,000,000 p ?????? ????????? ??? ????????????.", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    String url = AppHelper.SERVER_URL + "point_charge.jsp";
                                    int point = LoggedInUser.getPoint() + Integer.parseInt(et.getText().toString());

                                    final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            response = response.trim();
                                            if (response.equals("OK")) {
                                                setPoint(point);
                                                String msg = "????????? ????????? ?????????????????????.\n ?????? ????????? : " + point;
                                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                String text = "????????? ?????? ?????? : " + response;
                                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                                            }
                                            Log.d(getString(R.string.debug_message), response);
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d(getString(R.string.debug_message), error.getMessage());
                                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            String name = LoggedInUser.getUserId();

                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("id", name);
                                            params.put("point", Integer.toString(point));
                                            return params;
                                        }
                                    };

                                    stringRequest.setShouldCache(false);
                                    AppHelper.requestQueue.add(stringRequest);
                                }
                            })
                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(MainActivity.this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    msgBuilder.create().show();

                    //replaceFragment(R.id.action_nav_point);
                    //navController.navigate(R.id.action_nav_point);
                }
                else if (id == R.id.nav_orderlist) {
                    replaceFragment(R.id.action_orderlist);
                    //navController.navigate(R.id.action_orderlist);
                }

                else if(id == R.id.nav_logout){
                    AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context)
                            .setTitle("?????? ???????????? ???????????????????")
                            .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    LoggedInUser.logout();
                                    Toast.makeText(context, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(MainActivity.this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    msgBuilder.create().show();
                }

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void replaceFragment(int resId) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(resId);
    }

    public void setPoint(int point) {
        LoggedInUser.setPoint(point);
        NavigationView navigationView = findViewById(R.id.nav_view);
        TextView pointView = (TextView)navigationView.getHeaderView(0).findViewById(R.id.currentPoint);
        pointView.setText(Integer.toString(LoggedInUser.getPoint()).concat(" p"));
    }
}