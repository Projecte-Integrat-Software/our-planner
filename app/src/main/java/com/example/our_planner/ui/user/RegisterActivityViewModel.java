package com.example.our_planner.ui.user;

import android.app.Application;
import android.graphics.drawable.Drawable;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.our_planner.DataBaseAdapter;

public class RegisterActivityViewModel extends AndroidViewModel implements DataBaseAdapter.DBInterface {

    private final MutableLiveData<String> mToast;

    public RegisterActivityViewModel(Application application) {
        super(application);
        mToast = new MutableLiveData<>();
    }

    public void register(String email, String password, String username, Drawable drawableDefault) {
        DataBaseAdapter.register(this, email, password, username, drawableDefault);
    }

    public MutableLiveData<String> getToast() {
        return mToast;
    }

    @Override
    public void setToast(String s) {
        mToast.setValue(s);
    }
}
