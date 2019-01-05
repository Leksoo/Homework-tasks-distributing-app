package com.ifmo.ct.homework.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.firebase.database.GenericTypeIndicator;
import com.ifmo.ct.homework.App;
import com.ifmo.ct.homework.R;
import com.ifmo.ct.homework.model.Teacher;
import com.ifmo.ct.homework.model.User;
import com.ifmo.ct.homework.utils.Callback;
import com.ifmo.ct.homework.utils.FirebaseHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.hoang8f.widget.FButton;


@EActivity(R.layout.activity_main_teacher)
public class MainTeacherActivity extends MainActivity {


    @ViewById(R.id.tv_name_info)
    TextView tvNameInfo;
    @ViewById(R.id.spinner)
    Spinner spinner;

    private Teacher user;
    private String curGroup;
    private List<String> groups = new ArrayList<>();


    @AfterViews
    void init() {
        user = App.getPrefs().getTeacher();
        super.setToolBar((Toolbar) findViewById(R.id.toolbar));
        fillInfo();
        FirebaseHelper.getFromFirebase(null, null, FirebaseHelper.getStudentRef(),
                new GenericTypeIndicator<HashMap<String, Object>>() {
                }, new Callback<HashMap<String, Object>>() {
                    @Override
                    public void onSuccess(HashMap<String, Object> obj) {
                        groups.addAll(obj.keySet());
                        fillSpinner(groups);

                    }

                    @Override
                    public void onFail(int idMessage) {
                        showSnackBar(idMessage);
                    }
                });
    }

    @UiThread
    public void fillSpinner(final List<String> groups) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner_group_list, groups);
        adapter.setDropDownViewResource(R.layout.item_spinner_group_list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curGroup = groups.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void changeToDM() {

    }

    @Override
    protected void changeToADS() {

    }

    private void fillInfo() {
        tvNameInfo.setText(user.getName());
    }

    @Click(R.id.btn_build)
    public void OnClickBuildTable(View view) {
        App.getPrefs().setDistribution(null);
        TeacherTaskListActivity_.intent(this).curSubject(curSubject).curGroup(curGroup).start();

    }

    @Click(R.id.btn_show)
    public void OnClickShowTable(View view) {
        if(App.getPrefs().getDistribution()!=null) {
            TeacherTaskListActivity_.intent(this).curSubject(curSubject).curGroup(curGroup).start();
        }

    }

    @Click(R.id.btn_clean_table)
    public void OnClickCleanTable(View view) {
        App.getPrefs().setDistribution(null);
    }

    @UiThread
    public void showSnackBar(int messageId) {
        Snackbar.make(tvNameInfo, messageId, Snackbar.LENGTH_SHORT).show();
    }

    public static final String TAG = MainTeacherActivity.class.getName();
}

