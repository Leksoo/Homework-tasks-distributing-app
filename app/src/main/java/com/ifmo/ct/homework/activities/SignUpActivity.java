package com.ifmo.ct.homework.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LongDef;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ifmo.ct.homework.App;
import com.ifmo.ct.homework.R;
import com.ifmo.ct.homework.model.GoogleSheet;
import com.ifmo.ct.homework.model.Row;
import com.ifmo.ct.homework.model.SheetRefs;
import com.ifmo.ct.homework.model.Student;
import com.ifmo.ct.homework.model.Teacher;
import com.ifmo.ct.homework.model.User;
import com.ifmo.ct.homework.utils.Callback;
import com.ifmo.ct.homework.utils.FirebaseHelper;
import com.ifmo.ct.homework.utils.Api;
import com.ifmo.ct.homework.utils.GoogleSheetHelper;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Response;

@EActivity(R.layout.activity_sign_up)
public class SignUpActivity extends AppCompatActivity {

    @ViewById(R.id.rb_student)
    RadioButton rbStudent;
    @ViewById(R.id.rb_teacher)
    RadioButton rbTeacher;
    @ViewById(R.id.et_group)
    MaterialEditText etGroup;
    @ViewById(R.id.et_name)
    MaterialEditText etName;
    @ViewById(R.id.btn_sign_up)
    FButton btnSignUp;
    @ViewById(R.id.rg_log_type)
    RadioGroup rgLogType;

    private String group = "";
    private String name = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        autoLogin();
    }

    @AfterViews
    void init() {
        etGroup.addValidator(new RegexpValidator("wrong group format!"
                , "[A-Z]\\d\\d\\d\\d|\\d\\d\\d\\d|[А-Я]\\d\\d\\d\\d"));
        etName.addValidator(new RegexpValidator("wrong name format!", "(\\D+|\\D+\\d)"));
        rgLogType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbStudent.isChecked()) {
                    etGroup.setVisibility(View.VISIBLE);
                } else if (rbTeacher.isChecked()) {
                    etGroup.setVisibility(View.INVISIBLE);
                }
            }
        });
        progressDialog = App.createLoadingDialog(this);
    }

    private void autoLogin() {
        int userType = App.getPrefs().getUserStatus();
        if (userType == -1) return;
        goToMainActivity(userType);

    }

    @Click(R.id.btn_sign_up)
    public void onSignUpClicked() {
        group = etGroup.getText().toString();
        name = etName.getText().toString();
        if (!etName.validate()) return;
        if (rbStudent.isChecked() && etGroup.validate()) {
            authStudent();
        } else if (rbTeacher.isChecked()) {
            authTeacher();
        }

    }

    private void authStudent() {
        getSheetRefs();
    }

    private void getSheetRefs() {
        progressDialog.show();
        FirebaseHelper.getFromFirebase(null, "course" + getGroupNumber(group).charAt(1)
                , FirebaseHelper.getSheetsRef(), new GenericTypeIndicator<SheetRefs>() {}, new Callback<SheetRefs>() {
                    @Override
                    public void onSuccess(SheetRefs obj) {
                        App.getSheetPrefs().edit().ADS().put(obj.getADS()).DM().put(obj.getDM()).apply();
                        checkUserInGoogleSheet();
                    }

                    @Override
                    public void onFail(int messageId) {
                        progressDialog.dismiss();
                        showSnackBar(messageId);
                    }
                });
    }

    private void checkUserInGoogleSheet() {
        final String requestPath = App.getSheetPrefs().DM().get();
        Call<String> spreadsheet = App.getApi().get(requestPath);
        spreadsheet.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String data = response.body();
                try {
                    GoogleSheet googleSheet = GoogleSheetHelper.stringToGoogleSheet(data);
                    checkStudentInGoogleSheet(googleSheet);
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

    private void checkStudentInGoogleSheet(GoogleSheet googleSheet) {
        final String trueName = GoogleSheetHelper.findStudent(googleSheet,
                name.toLowerCase().trim(), getGroupNumber(group));
        if (trueName == null) {
            progressDialog.dismiss();
            showSnackBar(R.string.user_finding_fail_message);
            return;
        }
        Student user = new Student(trueName, getGroupNumber(group));
        FirebaseHelper.saveToFirebaseIfNotExist(user, user.getGroup(), user.getName()
                , FirebaseHelper.getStudentRef(), Student.class, new Callback<Student>() {
                    @Override
                    public void onSuccess(Student obj) {
                        App.getPrefs().saveUser(obj);
                        progressDialog.dismiss();
                        goToMainActivity(obj.getType());
                    }

                    @Override
                    public void onFail(int messageId) {
                        progressDialog.dismiss();
                        showSnackBar(messageId);
                    }
                });


    }


    private void authTeacher() {
        FirebaseHelper.getListFromFirebase(null, FirebaseHelper.getTeacherRef()
                , new GenericTypeIndicator<Teacher>() {}, new Callback<List<Teacher>>() {
                    @Override
                    public void onSuccess(List<Teacher> obj) {
                        progressDialog.dismiss();
                        for (Teacher teacher : obj) {
                            if (teacher.getName().trim().equals(name.toLowerCase().trim())) {
                                App.getPrefs().saveUser(teacher);
                                goToMainActivity(teacher.getType());
                            }
                        }
                        showSnackBar(R.string.user_finding_fail_message);
                    }

                    @Override
                    public void onFail(int messageId) {
                        progressDialog.dismiss();
                        showSnackBar(messageId);
                    }
                });
    }

    @UiThread
    void goToMainActivity(int type) {
        Log.d(TAG, "goToMainActivity called");
        if (type == User.STUDENT_TYPE) {
            MainStudentActivity_.intent(this).start();
        } else if (type == User.TEACHER_TYPE) {
            MainTeacherActivity_.intent(this).start();
        }
        finish();
    }

    String getGroupNumber(String g){
        if(Character.isLetter(g.charAt(0))){
            return g.substring(1);
        }
        return g;
    }

    @UiThread
    public void showSnackBar(int messageId) {
        Snackbar.make(btnSignUp, messageId, Snackbar.LENGTH_LONG).show();
    }

    public static final String TAG = SignUpActivity.class.getName();


}

