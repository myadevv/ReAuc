package com.myproject.reauc.ui.newproduct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProductRegisterViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ProductRegisterViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}