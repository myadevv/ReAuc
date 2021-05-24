package com.myproject.reauc.ui.showproduct;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.myproject.reauc.AppHelper;
import com.myproject.reauc.R;
import com.myproject.reauc.data.model.LoggedInUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyProductFragment extends Fragment {
    private final static String url = AppHelper.SERVER_URL + "get_myproduct.jsp";

    ProductAdapter adapter = new ProductAdapter();
    ListView listView;
    TextView text;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_myproduct, container, false);

        listView = rootView.findViewById(R.id.myproductListView);
        text = rootView.findViewById(R.id.text_myproduct);

        getMyProduct();


        return rootView;
    }

    private void getMyProduct() {
        try {
            JSONObject params = new JSONObject();
            params.put("id", LoggedInUser.getUserId());
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
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
                                String payed = jObject.getString("payedStatus");

                                vo.setResId(num);
                                vo.setTitle(title);
                                vo.setName(name);
                                vo.setPrice(price);
                                vo.setImageDir(imageDir);
                                vo.setEndDate(endDate);
                                vo.setPayedStatus(payed);

                                // will be deprecated
                                vo.description = description;
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
