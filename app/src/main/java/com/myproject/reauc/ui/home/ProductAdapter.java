package com.myproject.reauc.ui.home;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.ImageRequest;
import com.myproject.reauc.AppHelper;
import com.myproject.reauc.R;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {

    private ArrayList<ProductValue> listCustom = new ArrayList<>();

    // ListView에 보여질 Item 수
    @Override
    public int getCount() {
        return listCustom.size();
    }

    // 하나의 Item(ImageView 1, TextView ?)
    @Override
    public Object getItem(int position) {
        return listCustom.get(position);
    }

    // Item의 id : Item을 구별하기 위한 것으로 position 사용
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 실제로 Item이 보여지는 부분
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter, null, false);

            holder = new CustomViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.textTitle = (TextView) convertView.findViewById(R.id.text_title);
            holder.textName = (TextView) convertView.findViewById(R.id.text_sellerName);
            holder.textPrice = (TextView) convertView.findViewById(R.id.text_price);
            holder.textEndDate = (TextView) convertView.findViewById(R.id.text_end_date);

            convertView.setTag(holder);
        } else {
            holder = (CustomViewHolder) convertView.getTag();
        }

        ProductValue vo = listCustom.get(position);

        GradientDrawable drawable = (GradientDrawable) parent.getContext().getDrawable(R.drawable.background_rounding);
        holder.imageView.setBackground(drawable);
        holder.imageView.setClipToOutline(true);

        String imageUrl = AppHelper.SERVER_URL + "Image/" + vo.getResId() + "/" + vo.getImageDir();
        getImage(imageUrl, parent, holder);

        holder.textTitle.setText(vo.getTitle());
        holder.textName.setText(vo.getName());
        holder.textPrice.setText(vo.getPrice());
        holder.textEndDate.setText(vo.getEndDate());

        convertView.setOnClickListener(new View.OnClickListener() {
            String msg = vo.description + " /// " + vo.getPrice() + " /// " + vo.registerDate + " /// " + vo.getEndDate();
            @Override
            public void onClick(View v) {
                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(v.getContext())
                        .setTitle(vo.getTitle())
                        .setMessage(msg)
                        .setPositiveButton("입찰하기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do something
                            }
                        })
                        .setNegativeButton("나가기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do something
                            }
                        });
                msgBuilder.create().show();
            }
        });

        return convertView;
    }

    class CustomViewHolder {
        ImageView imageView;
        TextView textTitle;
        TextView textName;
        TextView textPrice;
        TextView textEndDate;
    }

    // MainActivity에서 Adapter에있는 ArrayList에 data를 추가시켜주는 함수
    public void addItem(ProductValue vo) {
        listCustom.add(vo);
    }

    public void clear() {
        listCustom.clear();
    }

    private void getImage(String url, ViewGroup parent, CustomViewHolder holder) {
        try {
            final ImageRequest imageRequest = new ImageRequest(url, Resources.getSystem(), parent.getContext().getContentResolver(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            try {
                                // Do something with response
                                holder.imageView.setImageBitmap(response);

                                // Save this downloaded bitmap to internal storage
                                //Uri uri = saveImageToInternalStorage(response);

                                // Display the internal storage saved image to image view
                                //mImageViewInternal.setImageURI(uri);
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
