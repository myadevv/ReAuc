package com.myproject.reauc.ui.newproduct;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;

import com.android.volley.Response;
import com.myproject.reauc.AppHelper;
import com.myproject.reauc.MainActivity;
import com.myproject.reauc.R;

import android.app.AlertDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;


import static android.app.Activity.RESULT_OK;

public class ProductRegisterFragment extends Fragment {
    //private static final String url = AppHelper.SERVER_URL + "image_upload.jsp";
    private static final String url = "http://60.253.14.126:8080/MyAuction/Android/image_upload.jsp";
    EditText imgPathText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_product_register, container, false);

        Button imgBtn = root.findViewById(R.id.imageButton);
        Button uploadBtn = root.findViewById(R.id.uploadButton);
        imgPathText = root.findViewById(R.id.editTextImagePath);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                //Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType("text/plain");
                startActivityForResult(intent, 10);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imgPath = imgPathText.getText().toString();

                SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        new AlertDialog.Builder(getContext()).setMessage("응답 : " + response).create().show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new AlertDialog.Builder(getContext()).setMessage("Error : " + error.getMessage()).create().show();
                    }
                });
                //이미지 파일 추가
                //smpr.addStringParam("imgName", imgPath);
                smpr.addFile("img", imgPath);

                AppHelper.requestQueue.add(smpr);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    if(uri != null){
                        Log.d(getString(R.string.debug_message), uri.toString());
                        String imgPath = getRealPathFromUri(uri);

                        imgPathText.setText(imgPath);
                        Toast.makeText(getContext(), uri.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }//onActivityResult()

    //Uri -- > 절대경로로 바꿔서 리턴시켜주는 메소드
    String getRealPathFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(getContext(), uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return result;
    }
}