package com.myproject.reauc.ui;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.ImageRequest;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.myproject.reauc.AppHelper;
import com.myproject.reauc.MainActivity;
import com.myproject.reauc.R;
import com.myproject.reauc.data.model.LoggedInUser;
import com.myproject.reauc.ui.home.ProductAdapter;
import com.myproject.reauc.ui.home.ProductValue;
import com.myproject.reauc.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class ContentFragment extends Fragment {
    public final static String url = AppHelper.SERVER_URL + "contentview.jsp";

    ImageView imgView;
    TextView title;
    TextView sellerId;
    TextView description;
    TextView price;
    EditText priceEditText;
    TextView buyText;
    TextView errText;
    Button buyButton;

    int curPoint;

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
        buyText = root.findViewById(R.id.contentBuyText);
        errText = root.findViewById(R.id.contentErrorText);
        buyButton = root.findViewById(R.id.contentBuyButton);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int buyPoint = Integer.parseInt(priceEditText.getText().toString());
                if (buyPoint <= curPoint) {
                    Toast.makeText(getContext(), "현재 입찰가보다 높은 가격으로 입찰해야 합니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (buyPoint > curPoint * 2 || buyPoint > curPoint + 10000) {
                    Toast.makeText(getContext(), "입찰 가격은 현재 입찰가의 2배 혹은 현재 입찰가보다 10000p 이상일 수 없습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (buyPoint > LoggedInUser.getPoint()) {
                    Toast.makeText(getContext(), "입찰 가격보다 소지 포인트가 적어 입찰할 수 없습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(getContext())
                        .setTitle("정말 입찰하시겠습니까?")
                        .setPositiveButton("입찰하기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String buyUrl = AppHelper.SERVER_URL + "bill_product.jsp";

                                final StringRequest stringRequest = new StringRequest(Request.Method.POST, buyUrl, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        response = response.trim();
                                        if (response.equals("OK")) {
                                            Toast.makeText(getContext(), "입찰이 완료되었습니다.", Toast.LENGTH_LONG).show();

                                            int curPoint = LoggedInUser.getPoint() - Integer.parseInt(priceEditText.getText().toString());
                                            ((MainActivity)getActivity()).setPoint(curPoint);
                                            getContentData();
                                        }
                                        else {
                                            String text = "입찰 실패 : " + response;
                                            Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
                                        }
                                        Log.d(getString(R.string.debug_message), response);
                                        //finish();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(getString(R.string.debug_message), error.getMessage());
                                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        String id = LoggedInUser.getUserId();
                                        int productId = AppHelper.posNum;

                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("buyerID", id);
                                        params.put("point", priceEditText.getText().toString());
                                        params.put("resid", Integer.toString(productId));
                                        return params;
                                    }
                                };

                                stringRequest.setShouldCache(false);
                                AppHelper.requestQueue.add(stringRequest);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getContext(), "입찰을 취소했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                msgBuilder.create().show();
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
                        String id = jObject.getString("name");
                        curPoint = jObject.getInt("price");

                        title.setText(jObject.getString("title"));
                        sellerId.setText(jObject.getString("name"));
                        description.setText(jObject.getString("description"));
                        price.setText("현재 입찰가 : " + curPoint + " p");
                        priceEditText.setText(Integer.toString(jObject.getInt("price") + 1000));
                        String endDate = jObject.getString("endDate");
                        String imageDir = jObject.getString("imageDir");
                        String buyerId = "";

                        if (jObject.has("buyerID"))
                            buyerId = jObject.getString("buyerID");

                        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        java.util.Date date = form.parse(endDate);

                        String imageUrl = AppHelper.SERVER_URL + "Image/" + AppHelper.posNum + "/" + imageDir;
                        getImage(imageUrl);

                        title.setVisibility(View.VISIBLE);
                        sellerId.setVisibility(View.VISIBLE);
                        description.setVisibility(View.VISIBLE);
                        price.setVisibility(View.VISIBLE);

                        if (date.compareTo(new java.util.Date()) < 0) {
                            setErrorText("입찰 기한이 종료된 상품입니다.");
                        }
                        else if (id.equals(LoggedInUser.getUserId())) {
                            setErrorText("판매자는 입찰할 수 없습니다.");
                        }
                        else if (buyerId.equals(LoggedInUser.getUserId())) {
                            setErrorText("이미 입찰 중인 상품입니다.");
                        }
                    }
                    catch (Exception e) {
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

    private void setErrorText(String msg) {
        priceEditText.setVisibility(View.INVISIBLE);
        buyText.setVisibility(View.INVISIBLE);
        buyButton.setVisibility(View.INVISIBLE);

        errText.setText(msg);
        errText.setVisibility(View.VISIBLE);
    }
}