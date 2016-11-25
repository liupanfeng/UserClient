package com.jikexueyuan.baidumap.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;


import com.jikexueyuan.baidumap.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by root on 16-5-9.
 */
public class SPUtil {

    public static final String TEL_NUMBER = "tel_number";
    public static final String USER_NAME = "user_name";

    private static final String TAG = "SPUtil";
    public static final String PREF_NAME = "com.jikexueyuan.baidumap";
    private SharedPreferences mPreferences;
    private static SPUtil mInstance;
    private Context mContext;

    private SPUtil() {
        mContext = App.getInstance().getApplicationContext();
        mPreferences = mContext.getSharedPreferences(PREF_NAME, 0);
    }

    public static SPUtil getInstant() {
        if (mInstance == null) {
            mInstance = new SPUtil();
        }
        return mInstance;
    }

    public SPUtil save(String key, Object value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (value instanceof String) {
            editor.putString(key, value.toString());
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, Boolean.valueOf(value.toString()));
        } else if (value instanceof Long) {
            editor.putLong(key, Long.valueOf(value.toString()));
        } else if (value instanceof Float) {
            editor.putFloat(key, Float.valueOf(value.toString()));
        } else if (value instanceof Integer) {
            editor.putInt(key, Integer.valueOf(value.toString()));
        }
        editor.commit();
        return mInstance;
    }

    public Object get(String key, Object value) {
        if (value instanceof String) {
            return mPreferences.getString(key, "");
        } else if (value instanceof Boolean) {
            return mPreferences.getBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            return mPreferences.getLong(key, Long.parseLong(value.toString()));
        } else if (value instanceof Float) {
            return mPreferences.getFloat(key, -1.0f);
        } else if (value instanceof Integer) {
            return mPreferences.getInt(key, Integer.parseInt(value.toString()));
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setArrayListString(String key, ArrayList<String> list) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (list != null && list.size() > 0) {
            editor.putStringSet(key, new HashSet<>(list));
            editor.commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ArrayList<String> getArrayListString(String key) {
        Set<String> set = mPreferences.getStringSet(key, null);
        if (set != null) {
            ArrayList<String> setList = new ArrayList<>(set);
            return setList;
        }
        return null;
    }

    public String getString(String key, String strDefault) {
        return mPreferences.getString(key, "");
    }

    public boolean getBoolean(String key, boolean blDefault) {
        return mPreferences.getBoolean(key, blDefault);
    }


}
