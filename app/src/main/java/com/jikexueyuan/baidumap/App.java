package com.jikexueyuan.baidumap;

import android.app.Application;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by liupf on 2016/11/21.
 */
public class App extends Application {

    private static App instance;

    public static App getInstance(){
       return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

    public void showToast(int id) {
        Toast.makeText(getApplicationContext(), getResources().getString(id), Toast.LENGTH_SHORT).show();
    }
    public void showToast(String  msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void showSnackMessage(View v, String msg) {
        if (v == null) {
            showToast(msg);
            return;
        }
        showSnackMessage(v, msg, null, null, Snackbar.LENGTH_SHORT);
    }

    public void showSnackMessage(View v, int id) {
        if (v == null) {
            showToast(id);
            return;
        }
        showSnackMessage(v, id, null, null, Snackbar.LENGTH_SHORT);
    }

    public void showSnackMessage(View v, int id, Snackbar.Callback callback,
                                 View.OnClickListener listener, int length) {
        Snackbar snack = Snackbar.make(v, getResources().getString(id), length);
        snack.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snack.setActionTextColor(Color.WHITE);
        snack.setCallback(callback);
        snack.setAction(id, listener);
        snack.show();
    }
    public void showSnackMessage(View v, String msg, Snackbar.Callback callback,
                                 View.OnClickListener listener, int length) {
        Snackbar snack = Snackbar.make(v, msg, length);
        snack.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snack.setActionTextColor(Color.WHITE);
        snack.setCallback(callback);
        snack.setAction(msg, listener);
        snack.show();
    }



}
