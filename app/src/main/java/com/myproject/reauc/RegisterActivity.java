package com.myproject.reauc;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.toolbox.Volley;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.StringRequest;

public class RegisterActivity extends AppCompatActivity {

    //final static String url = "http://10.0.2.2:8080/MyAuction/Android/register_pass.jsp";
    final static String url = AppHelper.SERVER_URL + "register_pass.jsp";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Button registerButton = findViewById(R.id.register);
        final ProgressBar loading = findViewById(R.id.loading);

        if(AppHelper.requestQueue == null)
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText id = findViewById(R.id.username);
                EditText pw = findViewById(R.id.password);

                if (id.length() < 6 || id.length() > 15 || pw.length() < 6 || pw.length() > 15) {
                    Toast.makeText(getApplicationContext(), "ID and PW must be 6~15 letters.", Toast.LENGTH_LONG).show();
                    return;
                }

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.trim();
                        if (response.equals("OK")) {
                            Toast.makeText(getApplicationContext(), "회원 가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            String text = "회원 가입 실패 : " + response;
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                        }
                        Log.d("ddeebbuugg", response);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ddeebbuugg", error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("id", id.getText().toString());
                        params.put("pw", pw.getText().toString());
                        return params;
                    }
                };

                stringRequest.setShouldCache(false);
                AppHelper.requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        super.onKeyDown(KeyCode, event);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (KeyCode == KeyEvent.KEYCODE_BACK) {
                finish();
                return true;
            }
            return true;
        }
        return false;
    }
}