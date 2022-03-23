package com.example.our_planner.ui.planner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlannerViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PlannerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is planner fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}