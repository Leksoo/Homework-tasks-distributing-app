package com.ifmo.ct.homework;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;

import com.ifmo.ct.homework.utils.Api;
import com.ifmo.ct.homework.utils.Prefs;
import com.ifmo.ct.homework.utils.SheetPrefs_;
import com.squareup.moshi.Moshi;

import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class App extends Application {

    public static Application context;
    private static Prefs prefs;
    private static SheetPrefs_ sheetPrefs;
    private static Retrofit retrofit;
    private static Api api;

    private static String baseUrl="https://spreadsheets.google.com/";

    public static Prefs getPrefs() {
        return prefs;
    }
    public static SheetPrefs_ getSheetPrefs() {
        return sheetPrefs;
    }
    public static Api getApi(){
        return api;
    }




    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sheetPrefs = new SheetPrefs_(context);
        prefs = new Prefs(context);
        retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        api = retrofit.create(Api.class);

    }

    public static ProgressDialog createLoadingDialog(Activity activity) {
        final ProgressDialog mDialog = new ProgressDialog(activity);
        mDialog.setMessage(activity.getString(R.string.loading));
        mDialog.setCancelable(false);
        return mDialog;
    }

}
