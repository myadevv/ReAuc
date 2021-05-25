package com.myproject.reauc.ui.newproduct;

import android.content.DialogInterface;
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
import android.widget.ProgressBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;

import com.myproject.reauc.AppHelper;
import com.myproject.reauc.MainActivity;
import com.myproject.reauc.R;
import com.myproject.reauc.data.model.LoggedInUser;
import com.myproject.reauc.ui.login.LoginActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
    private static final String url = AppHelper.SERVER_URL + "product_upload.jsp";
    EditText imgPathText;
    ImageView imageView;
    EditText titleText;
    EditText descriptionText;
    EditText priceText;
    EditText yearText; EditText monthText; EditText dayText; EditText hourText; EditText minuteText;
    String localImgPath = "";
    ProgressBar loading;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_product_register, container, false);

        Button imgBtn = root.findViewById(R.id.imageButton);
        Button uploadBtn = root.findViewById(R.id.uploadButton);
        imgPathText = root.findViewById(R.id.editTextImagePath);
        imageView = root.findViewById(R.id.tempImageView);
        titleText = root.findViewById(R.id.titleEditText);
        descriptionText = root.findViewById(R.id.descriptionEditText);
        priceText = root.findViewById(R.id.priceEditText);
        loading = root.findViewById(R.id.productRegisterLoading);
        yearText = root.findViewById(R.id.yearEditText);
        monthText = root.findViewById(R.id.monthEditText);
        dayText = root.findViewById(R.id.dayEditText);
        hourText = root.findViewById(R.id.hourEditText);
        minuteText = root.findViewById(R.id.minuteEditText);

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

        String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date((new java.util.Date()).getTime() + (1000 * 3600 * 24)));

        yearText.setText(datetime.substring(0, 4));
        monthText.setText(datetime.substring(5, 7));
        dayText.setText(datetime.substring(8, 10));
        hourText.setText(datetime.substring(11, 13));
        minuteText.setText(datetime.substring(14, 16));

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmpty()) {
                    Toast.makeText(getContext(), "제목, 내용, 가격, 이미지 파일 등 빈 칸이 없는지 확인해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (checkValidDateTime()) {
                    Toast.makeText(getContext(), "날짜 형식이 올바르지 않거나 과거 날짜입니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                    try {
                        Integer.parseInt(priceText.getText().toString());
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                        return;
                    }

                androidx.appcompat.app.AlertDialog.Builder msgBuilder = new AlertDialog.Builder(getContext())
                        .setTitle("이대로 상품을 등록하시겠습니까?")
                        .setPositiveButton("등록하기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loading.setVisibility(View.VISIBLE);
                                post(url, localImgPath, new Callback() {
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String msg = response.body().string();
                                        if (response.isSuccessful() && msg.trim().equals("OK")) {
                                            getActivity().runOnUiThread(new Runnable(){
                                                @Override
                                                public void run() {
                                                    loading.setVisibility(View.GONE);
                                                    Toast.makeText(getContext(), "게시물이 등록되었습니다", Toast.LENGTH_LONG).show();
                                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager().
                                                            getPrimaryNavigationFragment().getChildFragmentManager();
                                                    fragmentManager.beginTransaction().remove(ProductRegisterFragment.this).commit();
                                                    fragmentManager.popBackStack();
                                                }
                                            });
                                        } else {
                                            Log.d(getString(R.string.debug_message), msg);
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loading.setVisibility(View.GONE);
                                                    Toast.makeText(getContext(), "게시물 등록에 실패했습니다. " + msg, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                        //new AlertDialog.Builder(getContext()).setMessage("Error : " + e.getMessage()).create().show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //ignore
                            }
                        });
                msgBuilder.create().show();
            }
        });

        return root;
    }

    private Call post(String url, String imgPath, Callback callback) {
        Call call = null;
        String name = LoggedInUser.getUserId();

        try {
            String endDate = yearText.getText().toString() + "-" + monthText.getText().toString()
                    + "-" + dayText.getText().toString() + " " + hourText.getText().toString()
                    + ":" + minuteText.getText().toString();

            File sourceFile = new File(imgPath);
            Log.d(getString(R.string.debug_message), "File...::::" + sourceFile + " : " + sourceFile.exists());
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
            String filename = imgPath.substring(imgPath.lastIndexOf("/")+1);

            // OKHTTP3
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("img", filename, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                    .addFormDataPart("title", titleText.getText().toString())
                    .addFormDataPart("description", descriptionText.getText().toString())
                    .addFormDataPart("price", priceText.getText().toString())
                    .addFormDataPart("endDate", endDate)
                    .addFormDataPart("name", name)
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
    private String getRealPathFromUri(Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(getContext(), uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private boolean checkEmpty() {
        return titleText.getText().toString().isEmpty() || descriptionText.getText().toString().isEmpty()
                || priceText.getText().toString().isEmpty() || imgPathText.getText().toString().isEmpty()
                || yearText.getText().toString().isEmpty() || monthText.getText().toString().isEmpty()
                || dayText.getText().toString().isEmpty() || hourText.getText().toString().isEmpty()
                || minuteText.getText().toString().isEmpty();
    }

    private boolean checkValidDateTime() {
        Integer year = Integer.parseInt(yearText.getText().toString());
        Integer month = Integer.parseInt(monthText.getText().toString());
        Integer day = Integer.parseInt(dayText.getText().toString());
        Integer hour = Integer.parseInt(hourText.getText().toString());
        Integer minute = Integer.parseInt(minuteText.getText().toString());

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.Date now = new java.util.Date();

        try {
            java.util.Date date = form.parse(year.toString() + "-" + month.toString() + "-" + day.toString()
                    + " " + hour.toString() + ":" + minute.toString());
            if (now.compareTo(date) > 0)
                return true;
        }
        catch (ParseException e) {
            e.printStackTrace();
            return true;
        }

        final ArrayList<Integer> month30 = new ArrayList();
        month30.add(4); month30.add(6); month30.add(9); month30.add(11);

        if (year <= 2020 || year >= 2100 || month < 1 || month > 12 || day < 1 || day > 31
                || hour < 0 || hour >= 24 || minute < 0 || minute >= 60)
            return true;

        else if (month30.contains(month) && day == 31)
            return true;

        else if (month == 2) {
             if (year % 4 == 0 && day > 29) return true;
             else if (day > 28) return true;
        }

        return false;

    }
}