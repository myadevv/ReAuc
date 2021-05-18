package com.myproject.reauc.ui.pointcharge;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PointViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PointViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Point Charge Fragment (will be implemented)");
    }

    public LiveData<String> getText() {
        return mText;
    }
}