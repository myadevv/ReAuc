package com.myproject.reauc.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.ImageRequest;
import com.android.volley.request.JsonObjectRequest;
import com.myproject.reauc.AppHelper;
import com.myproject.reauc.R;
import com.myproject.reauc.data.model.LoggedInUser;
import com.myproject.reauc.ui.home.ProductAdapter;
import com.myproject.reauc.ui.home.ProductValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentFragment extends Fragment {
    public final static String url = AppHelper.SERVER_URL + "contentview.jsp";

    ImageView imgView;
    TextView title;
    TextView sellerId;
    TextView description;
    TextView price;
    EditText priceEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_contents, container, false);

        imgView = root.findViewById(R.id.contentImage);
        title = root.findViewById(R.id.contentTitleText);
        sellerId = root.findViewById(R.id.contentSellerText);
        description = root.findViewById(R.id.contentDescription);
        price = root.findViewById(R.id.contentPriceText);
        priceEditText = root.findViewById(R.id.contentPriceEditText);
        Button buyButton = root.findViewById(R.id.contentBuyButton);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 입찰 기능 구현
            }
        });

        getContentData();

        return root;
    }


    private void getContentData() {
        //loadingBar.setVisibility(View.VISIBLE);
        try {
            JSONObject params = new JSONObject();
            params.put("resid", AppHelper.posNum);

            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(getString(R.string.debug_message), response.toString());
                    try {
                        JSONObject jObject = response;
                        title.setText(jObject.getString("title"));
                        sellerId.setText(jObject.getString("name"));
                        description.setText(jObject.getString("description"));
                        price.setText("현재 입찰가 : " + jObject.getInt("price") + " p");
                        priceEditText.setText(Integer.toString(jObject.getInt("price") + 1000));
                        String endDate = jObject.getString("endDate");
                        String imageDir = jObject.getString("imageDir");

                        String imageUrl = AppHelper.SERVER_URL + "Image/" + AppHelper.posNum + "/" + imageDir;
                        getImage(imageUrl);
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
        //loadingBar.setVisibility(View.GONE);
    }

    private void getImage(String url) {
        try {
            final ImageRequest imageRequest = new ImageRequest(url, Resources.getSystem(), getContext().getContentResolver(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            try {
                                imgView.setImageBitmap(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });

            imageRequest.setShouldCache(false);
            AppHelper.requestQueue.add(imageRequest);
        }
        catch (Exception e) {
            Log.e(String.valueOf(R.string.debug_message), e.getMessage());
        }
    }
}