package com.myproject.reauc.ui.newproduct;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;

import com.myproject.reauc.AppHelper;
import com.myproject.reauc.MainActivity;
import com.myproject.reauc.R;

import android.app.AlertDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class ProductRegisterFragment extends Fragment {
    private static final String url = AppHelper.SERVER_URL + "image_upload.jsp";
    //private static final String url = "http://60.253.14.126:8080/MyAuction/Android/image_upload.jsp";
    EditText imgPathText;
    ImageView imageView;
    String localImgPath = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_product_register, container, false);

        Button imgBtn = root.findViewById(R.id.imageButton);
        Button uploadBtn = root.findViewById(R.id.uploadButton);
        imgPathText = root.findViewById(R.id.editTextImagePath);
        imageView = root.findViewById(R.id.tempImageView);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFile();
            }
        });
        imgPathText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFile();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post(url, localImgPath, new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseStr = response.body().string();
                            Log.d(getString(R.string.debug_message), responseStr);

                            getActivity().runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "게시물이 등록되었습니다", Toast.LENGTH_LONG).show();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager().
                                            getPrimaryNavigationFragment().getChildFragmentManager();
                                    fragmentManager.beginTransaction().remove(ProductRegisterFragment.this).commit();
                                    fragmentManager.popBackStack();
                                }
                            });
                        } else {
                            Log.d(getString(R.string.debug_message), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        //new AlertDialog.Builder(getContext()).setMessage("Error : " + e.getMessage()).create().show();
                    }
                });
            }
        });

        return root;
    }

    private Call post(String url, String imgPath, Callback callback) {
        Call call = null;
        try {
            File sourceFile = new File(imgPath);
            Log.d(getString(R.string.debug_message), "File...::::" + sourceFile + " : " + sourceFile.exists());
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
            String filename = imgPath.substring(imgPath.lastIndexOf("/")+1);
            int imgNum = 9903;

            // OKHTTP3
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("img", filename, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                    .addFormDataPart("num", Integer.toString(imgNum))
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            call = client.newCall(request);
            call.enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return call;
        }
    }

    private void getImageFile() {
        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        //Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("text/plain");
        startActivityForResult(intent, 10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    imageView.setImageURI(uri);
                    imageView.setVisibility(View.VISIBLE);
                    if(uri != null){
                        String imgPath = getRealPathFromUri(uri);
                        imgPathText.setText(imgPath.substring(imgPath.lastIndexOf("/")));
                        try {
                            InputStream in = getContext().getContentResolver().openInputStream(uri); //src

                            String extension = imgPath.substring(imgPath.lastIndexOf("."));
                            File localImgFile = new File(getContext().getFilesDir(), "img"+extension);
                            localImgPath = localImgFile.getPath();

                            if(in != null) {
                                OutputStream out = new FileOutputStream(localImgFile);//dst
                                // Transfer bytes from in to out
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }
                                out.close();
                            }
                            in.close();
                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }//onActivityResult()

    //Uri -- > 절대경로로 바꿔서 리턴시켜주는 메소드
    String getRealPathFromUri(Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(getContext(), uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return result;
    }
}