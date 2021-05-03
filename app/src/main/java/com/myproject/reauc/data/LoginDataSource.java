package com.myproject.reauc.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.myproject.reauc.AppHelper;
import com.myproject.reauc.data.model.LoggedInUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    final static String url = "http://60.253.14.126:8080/MyAuction/Android/login_pass.jsp";
    static Result<LoggedInUser> result;

    public Result<LoggedInUser> login(String username, String password) {
        LoginDataSource.result = null;
        try {

            final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    LoggedInUser user = new LoggedInUser(java.util.UUID.randomUUID().toString(), username);
                    LoginDataSource.result = new Result.Success<>(user);
                    Log.d("ddeebbuugg", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LoginDataSource.result = new Result.Error(error);
                    Log.d("ddeebbuugg", error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", username);
                    params.put("pw", password);
                    return params;
                }
            };

            stringRequest.setShouldCache(false);
            AppHelper.requestQueue.add(stringRequest);

            LoggedInUser fakeUser = new LoggedInUser(java.util.UUID.randomUUID().toString(), username);
            return new Result.Success<>(fakeUser);

            //return LoginDataSource.result;


        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}