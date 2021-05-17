package com.myproject.reauc.ui.home;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.error.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.ImageRequest;
import com.android.volley.request.JsonObjectRequest;
import com.myproject.reauc.AppHelper;
import com.myproject.reauc.MainActivity;
import com.myproject.reauc.R;
import com.myproject.reauc.RegisterActivity;
import com.myproject.reauc.data.model.LoggedInUser;
import com.myproject.reauc.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private final static String url = AppHelper.SERVER_URL + "listboard.jsp";
    View root;
    TextView text;
    ListView listView;
    ProductAdapter adapter = new ProductAdapter();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        text = root.findViewById(R.id.text_home);
        listView = root.findViewById(R.id.productListView);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                text.setText(s);
            }
        });

        getListboard();

        return root;
    }


    private void getListboard() {
        try {
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(getString(R.string.debug_message), response.toString());
                    try {
                        String result = response.getString("data");
                        if (result.equals("No data")) {
                            text.setVisibility(View.VISIBLE);
                        }
                        else {
                            adapter.clear();
                            JSONArray jArray = response.getJSONArray("data");
                            for(int i = 0; i < jArray.length(); i++) {
                                ProductValue vo = new ProductValue();

                                JSONObject jObject = jArray.getJSONObject(i);
                                int num = jObject.getInt("registerNo");
                                String title = jObject.getString("title");
                                int price = jObject.getInt("price");
                                String registerDate = jObject.getString("registerDate");
                                String endDate = jObject.getString("endDate");
                                String description = jObject.getString("description");
                                String name = jObject.getString("name");

                                String imageDir = jObject.getString("imageDir");

                                vo.setResId(num);
                                vo.setTitle(title);
                                vo.setName(name);
                                vo.setPrice(price);
                                vo.setImageDir(imageDir);

                                // will be deprecated
                                vo.description = description;
                                vo.endDate = endDate;
                                vo.registerDate = registerDate;
                                adapter.addItem(vo);
                            }
                            listView.setAdapter(adapter);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            jsonRequest.setShouldCache(false);
            AppHelper.requestQueue.add(jsonRequest);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}