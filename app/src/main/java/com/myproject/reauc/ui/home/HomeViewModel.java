package com.myproject.reauc.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("입찰 중인 상품이 없습니다. 메뉴에서 \"물품 등륵\"을 눌러 상품을 등록해보세요.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}