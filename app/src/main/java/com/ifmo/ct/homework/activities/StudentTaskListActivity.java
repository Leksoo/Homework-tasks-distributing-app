package com.ifmo.ct.homework.activities;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.firebase.database.GenericTypeIndicator;
import com.ifmo.ct.homework.App;
import com.ifmo.ct.homework.R;
import com.ifmo.ct.homework.model.Student;
import com.ifmo.ct.homework.model.Teacher;
import com.ifmo.ct.homework.utils.Callback;
import com.ifmo.ct.homework.utils.FirebaseHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EActivity(R.layout.activity_task_list)
public class StudentTaskListActivity extends AppCompatActivity {

    @ViewById(R.id.gridLayoutNames)
    GridLayout gridLayoutNames;
    @ViewById(R.id.gridLayoutTasks)
    GridLayout gridLayoutTasks;

    @Extra
    int curSubject;

    private Student user;
    private float scale;
    private ProgressDialog progressDialog;

    @AfterViews
    void init() {
        progressDialog = App.createLoadingDialog(this);
        scale = getResources().getDisplayMetrics().density;
        user = App.getPrefs().getStudent();
        progressDialog.show();
        FirebaseHelper.getListFromFirebase(user.getGroup(), FirebaseHelper.getStudentRef()
                , new GenericTypeIndicator<Student>() {
                }, new Callback<List<Student>>() {
                    @Override
                    public void onSuccess(List<Student> obj) {
                        fillGridView(obj);
                    }

                    @Override
                    public void onFail(int messageId) {
                        progressDialog.dismiss();
                        showSnackBar(messageId);
                    }
                });

    }

    List<Integer> getTaskList(List<Student> studentList) {
        Set<Integer> taskList = new HashSet<>();
        for (Student student : studentList) {
            taskList.addAll(student.getTaskList(curSubject));
        }
        return Stream.of(taskList).sorted().collect(Collectors.toList());

    }


    @UiThread
    public void fillGridView(List<Student> studentList) {
        Collections.sort(studentList, (Student a, Student b) -> a.getName().compareTo(b.getName()));
        List<Integer> taskList = getTaskList(studentList);
        gridLayoutNames.setColumnCount(2);
        gridLayoutNames.setRowCount(studentList.size() + 1);
        gridLayoutTasks.setColumnCount(taskList.size());
        gridLayoutTasks.setRowCount(studentList.size()+1);
        addTableItem(gridLayoutNames,createNameTextView(""), 0, 0);
        for (int i = 0; i < taskList.size(); i++) {

            TextView tv = createTaskTextView(Integer.toString(taskList.get(i)),R.color.colorPrimaryLight);
            addTableItem(gridLayoutTasks,tv, i, 0);
        }
        for (int i = 0; i < studentList.size(); i++) {
            if (studentList.get(i) == null) {
                Log.d(TAG, "student null");
                continue;
            }

            addTableItem(gridLayoutNames,createNameTextView(studentList.get(i).getName()), 0, i + 1);
            addTableItem(gridLayoutNames
                    , createTaskTextView(Integer.toString(studentList.get(i).getPoints(curSubject))
                            , R.color.colorPrimaryLight), 1, i + 1);
            for (int j = 0; j <taskList.size() ; j++) {
                TextView tvTaskItem;
                if(studentList.get(i).getTaskList(curSubject).contains(taskList.get(j))){
                    tvTaskItem = createTaskTextView("+",R.color.colorGreen);
                }
                else {
                    tvTaskItem = createTaskTextView("",R.color.colorPrimaryLight);
                }
                addTableItem(gridLayoutTasks,tvTaskItem, j, i + 1);

            }

        }
        progressDialog.dismiss();


    }

    private void addTableItem(GridLayout gridLayout,TextView tv, int column, int row) {
        gridLayout.addView(tv);
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(row);
        param.setMargins(2,2,2,2);
        tv.setLayoutParams(param);
    }

    private TextView createNameTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setSingleLine(true);
        tv.setTextSize(20f);
        tv.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        tv.setPadding(2,0,2,0);
        tv.setWidth((int) (150 * scale + 0.5f));
        tv.setHeight((int) (40 * scale + 0.5f));
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    private TextView createTaskTextView(String text,int colorId) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(20f);
        tv.setSingleLine(true);
        tv.setBackgroundColor(getResources().getColor(colorId));
        tv.setPadding(0,0,0,0);
        tv.setWidth((int) (40 * scale + 0.5f));
        tv.setHeight((int) (40 * scale + 0.5f));
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    @UiThread
    public void showSnackBar(int messageId) {
        Snackbar.make(gridLayoutNames, messageId, Snackbar.LENGTH_SHORT).show();
    }

    public static final String TAG = StudentTaskListActivity.class.getName();

}
