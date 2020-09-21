package com.teammandroid.chatnow.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    /**
     * Live Data Instance
     */
    private MutableLiveData<String> mName = new MutableLiveData<>();

    public void setIsUpdate(String update) {
        mName.setValue(update);
    }

    public LiveData<String> getIsUpdate() {
        return mName;
    }
}