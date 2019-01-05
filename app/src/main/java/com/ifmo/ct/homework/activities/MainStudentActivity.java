package com.ifmo.ct.homework.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ifmo.ct.homework.App;
import com.ifmo.ct.homework.R;
import com.ifmo.ct.homework.model.GoogleSheet;
import com.ifmo.ct.homework.model.Student;
import com.ifmo.ct.homework.model.User;
import com.ifmo.ct.homework.utils.Callback;
import com.ifmo.ct.homework.utils.FirebaseHelper;
import com.ifmo.ct.homework.utils.GoogleSheetHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Response;

@EActivity(R.layout.activity_main_student)
public class MainStudentActivity extends MainActivity {


    @ViewById(R.id.tv_name_info)
    TextView tvNameInfo;
    @ViewById(R.id.tv_group_info)
    TextView tvGroupInfo;
    @ViewById(R.id.tv_points_info)
    TextView tvPointsInfo;
    @ViewById(R.id.tv_submitted_tasks)
    TextView tvSubmittedTasks;

    private Student user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @AfterViews
    void init() {
        user = App.getPrefs().getStudent();
        progressDialog = App.createLoadingDialog(this);
        fillInfo();
        super.setToolBar((Toolbar) findViewById(R.id.toolbar));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void changeToDM() {
        tvPointsInfo.setText(Integer.toString(user.getPointsDM()));
        updateSubmittedTask();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void changeToADS() {
        tvPointsInfo.setText(Integer.toString(user.getPointsADS()));
        updateSubmittedTask();
    }

    private void getPointsFromGoogleSheet() {
        progressDialog.show();
        final String requestPathDM = App.getSheetPrefs().DM().get();
        Call<String> spreadsheetDM = App.getApi().get(requestPathDM);
        spreadsheetDM.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String data = response.body();
                try {
                    if (data == null) {
                        throw new NullPointerException();
                    }
                    GoogleSheet googleSheet = GoogleSheetHelper.stringToGoogleSheet(data);
                    int points = GoogleSheetHelper.getPoints(googleSheet, user.getName());
                    user.setPointsDM(points);
                    if (curSubject == DM_CODE) {
                        changeToDM();
                    }
                    savePointsToFirebase();
                } catch (Exception e) {
                    progressDialog.dismiss();
                    showSnackBar(R.string.google_sheet_fail_message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                showSnackBar(R.string.connection_fail_message);
            }
        });
    }

    private void fillInfo() {
        tvNameInfo.setText(user.getName());
        tvGroupInfo.setText(user.getGroup());
        getPointsFromGoogleSheet();
    }


    private void savePointsToFirebase() {
        FirebaseHelper.saveToFirebase(user, user.getGroup(), user.getName(), FirebaseHelper.getStudentRef()
                , new Callback<Student>() {
                    @Override
                    public void onSuccess(Student obj) {
                        progressDialog.dismiss();
                        showSnackBar(R.string.points_updates_message);
                        updateSubmittedTask();
                    }

                    @Override
                    public void onFail(int messageId) {
                        progressDialog.dismiss();
                        showSnackBar(messageId);
                    }
                });
    }

    @Click(R.id.btn_submit)
    void submitButtonClicked(View view) {
        SubmitTasksActivity_.intent(this).curSubject(curSubject).startForResult(SubmitTasksActivity.REQUEST_CODE);
    }

    @Click(R.id.btn_show_table)
    void showTableButtonClicked(View view) {
        StudentTaskListActivity_.intent(this).curSubject(curSubject).start();
    }

    @OnActivityResult(SubmitTasksActivity.REQUEST_CODE)
    void onSubmitTasksActivityResult(int resultCode) {
        if (resultCode == RESULT_OK) {
            user = App.getPrefs().getStudent();
            Snackbar.make(tvNameInfo, R.string.successful_submit_message, Snackbar.LENGTH_SHORT).show();
            updateSubmittedTask();
        }
    }

    @UiThread
    void updateSubmittedTask() {
        List<Integer> tasks = user.getTaskList(curSubject);
        StringBuilder str = new StringBuilder();
        if (tasks == null) {
            throw new NullPointerException("task list is null");
        }
        if (tasks.isEmpty()) {
            str.append(getString(R.string.empty));
        } else {
            for (int task : tasks) {
                str.append(task).append(" ");
            }
        }
        tvSubmittedTasks.setText(str.toString());
    }

    @Click(R.id.btn_clean)
    void cleanButtonClicked(View view) {
        user.setTaskList(new ArrayList<>(), curSubject);
        FirebaseHelper.saveToFirebase(user, user.getGroup(), user.getName(), FirebaseHelper.getStudentRef()
                , new Callback<Student>() {
                    @Override
                    public void onSuccess(Student obj) {
                        showSnackBar(R.string.cleaned_message);
                        App.getPrefs().saveUser(user);
                        updateSubmittedTask();
                    }

                    @Override
                    public void onFail(int messageId) {
                        showSnackBar(messageId);
                    }
                });
    }

    @UiThread
    public void showSnackBar(int messageId) {
        Snackbar.make(tvNameInfo, messageId, Snackbar.LENGTH_SHORT).show();
    }

    public static final String TAG = MainStudentActivity.class.getName();


}
