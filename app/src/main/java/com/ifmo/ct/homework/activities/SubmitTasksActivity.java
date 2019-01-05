package com.ifmo.ct.homework.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ifmo.ct.homework.App;
import com.ifmo.ct.homework.R;
import com.ifmo.ct.homework.model.Student;
import com.ifmo.ct.homework.utils.Callback;
import com.ifmo.ct.homework.utils.FirebaseHelper;
import com.ifmo.ct.homework.views.ExpandableHeightGridView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.List;

@EActivity(R.layout.activity_submit_tasks)
public class SubmitTasksActivity extends AppCompatActivity {

    @ViewById(R.id.gridView)
    ExpandableHeightGridView gridView;
    @ViewById(R.id.et_single_apply)
    TextView etSingleApply;
    @ViewById(R.id.et_from_apply)
    TextView etFromApply;
    @ViewById(R.id.et_to_apply)
    TextView etToApply;

    @Extra
    int curSubject;

    private ArrayAdapter<Integer> adapter;
    private List<Integer> tasks;
    private Student user;

    @AfterViews
    void init() {

        user = App.getPrefs().getStudent();
        tasks = user.getTaskList(curSubject);
        adapter = new ArrayAdapter<>(this, R.layout.item_submit_task_grid_view, R.id.tv_item_task_number, tasks);
        gridView.setAdapter(adapter);
        gridView.setExpanded(true);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Integer task = adapter.getItem(i);
                tasks.remove(task);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Click(R.id.btn_add_single)
    void addSingleTask(View view) {
        String task = etSingleApply.getText().toString().trim();
        etSingleApply.setText("");
        int taskInt;
        try {
            taskInt = Integer.parseInt(task);
        } catch (NumberFormatException e) {
            return;
        }
        if (!tasks.contains(taskInt)) {
            tasks.add(Integer.parseInt(task));
            Collections.sort(tasks);
            adapter.notifyDataSetChanged();
        }

    }

    @Click(R.id.btn_add_many)
    void addManyTasks(View view) {
        String from = etFromApply.getText().toString().trim();
        String to = etToApply.getText().toString().trim();
        etFromApply.setText("");
        etToApply.setText("");
        if (!(from.matches("\\d+") && to.matches("\\d+"))) {
            return;
        }
        int i, j;
        try {
            i = Integer.parseInt(from);
            j = Integer.parseInt(to);
        } catch (NumberFormatException e) {
            return;
        }
        if (i > j) {
            int tmp = i;
            i = j;
            j = tmp;
        }
        if (j - i > 300 || tasks.size() > 300) {
            showSnackBar(R.string.to_many_tasks_message);
            return;
        }
        while (i != j + 1) {
            if (!tasks.contains(i)) {
                tasks.add(i);
            }
            i++;
        }
        Collections.sort(tasks);
        adapter.notifyDataSetChanged();

    }

    @Click(R.id.btn_submit_done)
    void submit(View view) {
        user.setTaskList(tasks, curSubject);
        FirebaseHelper.saveToFirebase(user, user.getGroup(), user.getName(), FirebaseHelper.getStudentRef()
                , new Callback<Student>() {
                    @Override
                    public void onSuccess(Student obj) {
                        showSnackBar(R.string.successful_submit_message);
                        App.getPrefs().saveUser(obj);
                        goBack();
                    }

                    @Override
                    public void onFail(int messageId) {
                        showSnackBar(messageId);
                    }
                });
    }

    @Click(R.id.btn_clean_tasks)
    void clean(View view) {
        tasks.clear();
        adapter.notifyDataSetChanged();
    }

    @UiThread
    void goBack() {
        setResult(RESULT_OK);
        finish();
    }

    @UiThread
    public void showSnackBar(int messageId) {
        Snackbar.make(etSingleApply, messageId, Snackbar.LENGTH_SHORT).show();
    }

    public static final String TAG = SubmitTasksActivity.class.getName();
    public static final int REQUEST_CODE = 323;
}
