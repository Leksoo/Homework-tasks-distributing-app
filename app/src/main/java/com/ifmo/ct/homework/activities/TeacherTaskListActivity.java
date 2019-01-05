package com.ifmo.ct.homework.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.firebase.database.GenericTypeIndicator;
import com.ifmo.ct.homework.App;
import com.ifmo.ct.homework.R;
import com.ifmo.ct.homework.model.Distribution;
import com.ifmo.ct.homework.model.Student;
import com.ifmo.ct.homework.model.User;
import com.ifmo.ct.homework.utils.Callback;
import com.ifmo.ct.homework.utils.FirebaseHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@EActivity(R.layout.activity_task_list)
public class TeacherTaskListActivity extends AppCompatActivity {

    @ViewById(R.id.gridLayoutNames)
    GridLayout gridLayoutNames;
    @ViewById(R.id.gridLayoutTasks)
    GridLayout gridLayoutTasks;

    @Extra
    int curSubject;
    @Extra
    String curGroup;

    private float scale;

    public static final int NOT_SUBMITTED = 0;
    public static final int SUBMITTED = 1;
    public static final int CHOSEN = 2;
    public static final int CONFLICT = 3;

    private TextView[][] table;
    private Distribution distribution;
    private int taskCount;
    private int studentCount;

    @AfterViews
    void init() {
        scale = getResources().getDisplayMetrics().density;
        distribution = App.getPrefs().getDistribution();
        if (distribution == null) {
            FirebaseHelper.getListFromFirebase(curGroup, FirebaseHelper.getStudentRef()
                    , new GenericTypeIndicator<Student>() {
                    }, new Callback<List<Student>>() {
                        @Override
                        public void onSuccess(List<Student> studentList) {
                            Collections.sort(studentList, (Student a, Student b) -> a.getName().compareTo(b.getName()));
                            List<Integer> taskList = getTaskList(studentList);
                            fillDistribution(taskList, studentList);
                            fillGridLayout();
                        }

                        @Override
                        public void onFail(int messageId) {
                            showSnackBar(messageId);
                        }
                    });
        } else {
            taskCount = distribution.getTaskList().size();
            studentCount = distribution.getStudentNames().size();
            fillGridLayout();
        }

    }


    List<Integer> getTaskList(List<Student> studentList) {
        Set<Integer> taskList = new HashSet<>();
        for (Student student : studentList) {
            taskList.addAll(student.getTaskList(curSubject));
        }
        return Stream.of(taskList).sorted().collect(Collectors.toList());

    }


    @UiThread
    public void fillGridLayout() {
        table = new TextView[studentCount][taskCount];
        gridLayoutNames.setColumnCount(2);
        gridLayoutNames.setRowCount(studentCount + 1);
        gridLayoutTasks.setColumnCount(taskCount);
        gridLayoutTasks.setRowCount(studentCount + 1);
        // fill empty cells
        addTableItem(gridLayoutNames, createNameTextView(""), 0, 0);
        addTableItem(gridLayoutNames, createTaskTextView("", R.color.colorPrimaryLight), 1, 0);
        //fill task row
        for (int i = 0; i < taskCount; i++) {
            TextView tv = createTaskTextView(Integer.toString(distribution.getTaskList().get(i)), R.color.colorPrimaryLight);
            addTableItem(gridLayoutTasks, tv, i, 0);
        }
        for (int i = 0; i < studentCount; i++) {
            //add name
            addTableItem(gridLayoutNames, createNameTextView(distribution.getStudentNames().get(i)), 0, i + 1);
            //add points
            addTableItem(gridLayoutNames
                    , createTaskTextView(Integer.toString(distribution.getStudentPoints().get(i))
                            , R.color.colorPrimaryLight), 1, i + 1);
            for (int j = 0; j < taskCount; j++) {
                TextView tvTaskItem;
                switch (distribution.getDistributionTable().get(i).get(j)) {
                    case SUBMITTED: {
                        tvTaskItem = createTaskTextView("+", R.color.colorPrimaryLight);
                        break;
                    }
                    case CHOSEN: {
                        tvTaskItem = createTaskTextView("+", R.color.colorGreen);
                        break;
                    }
                    case CONFLICT: {
                        tvTaskItem = createTaskTextView("+", R.color.colorOrange);
                        break;
                    }
                    default: {
                        tvTaskItem = createTaskTextView("", R.color.colorPrimaryLight);
                    }

                }
                tvTaskItem.setTag(Integer.toString(i) + " " + Integer.toString(j));
                tvTaskItem.setOnClickListener(v -> {
                    if (v.getTag() != null) {
                        String[] indexes = ((String) v.getTag()).split(" ");
                        switchTaskStatus(v, Integer.parseInt(indexes[0]), Integer.parseInt((indexes[1])));
                    }
                });
                table[i][j] = tvTaskItem;
                //add submitted task
                addTableItem(gridLayoutTasks, tvTaskItem, j, i + 1);

            }

        }


    }

    private void switchTaskStatus(View view, int i, int j) {
        int status = distribution.getStatus(i, j);
        if (status == CHOSEN || status == CONFLICT) {
            distribution.setStatus(i, j, SUBMITTED);
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        } else if (status == SUBMITTED) {
            distribution.setStatus(i, j, CHOSEN);
            view.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        }
        checkConflicts(j);
    }

    private void checkConflicts(int task) {
        int chosenCount = 0;
        for (int student = 0; student < studentCount; student++) {
            if (distribution.getStatus(student, task) == CHOSEN
                    || distribution.getStatus(student, task) == CONFLICT) {
                chosenCount++;
            }
        }
        if (chosenCount == 0) {
            return;
        }
        if (chosenCount > 1) {
            for (int student = 0; student < studentCount; student++) {
                if (distribution.getStatus(student, task) == CHOSEN) {
                    distribution.setStatus(student, task, CONFLICT);
                    table[student][task].setBackgroundColor(getResources().getColor(R.color.colorOrange));
                }
            }
        } else {
            for (int student = 0; student < studentCount; student++) {
                if (distribution.getStatus(student, task) == CONFLICT) {
                    distribution.setStatus(student, task, CHOSEN);
                    table[student][task].setBackgroundColor(getResources().getColor(R.color.colorGreen));
                }
            }
        }
    }

    private void fillDistribution(List<Integer> taskList, List<Student> studentList) {
        taskCount = taskList.size();
        studentCount = studentList.size();
        int[][] distributionTable = new int[studentCount][taskCount];
        List<Integer> taskSubmitCount = new ArrayList<>(Collections.nCopies(taskCount, -1));
        for (int i = 0; i < studentList.size(); i++) {
            for (int taskNum : studentList.get(i).getTaskList(curSubject)) {
                int taskIndex = taskList.indexOf(taskNum);
                if (taskIndex != -1) {
                    if (taskSubmitCount.get(taskIndex) == -1) {
                        taskSubmitCount.set(taskIndex, i);
                    } else if (taskSubmitCount.get(taskIndex) >= 0) {
                        taskSubmitCount.set(taskIndex, -2);
                    }
                    distributionTable[i][taskIndex] = SUBMITTED;
                }
            }
        }

        List<Pair<Student, Integer>> sortedStudentList = new ArrayList<>();
        for (int i = 0; i < studentList.size(); i++) {
            sortedStudentList.add(new Pair<>(studentList.get(i), i));
        }
        Comparator<Pair<Student, Integer>> studentComparator = (s1, s2) -> {
            if (s1.first.getPoints(curSubject) < s2.first.getPoints(curSubject)) {
                return -1;
            } else if (s1.first.getPoints(curSubject) > s2.first.getPoints(curSubject)) {
                return 1;
            } else {
                if (s1.first.getTaskList(curSubject).size() > s2.first.getTaskList(curSubject).size()) {
                    return -1;
                } else if (s1.first.getTaskList(curSubject).size() < s2.first.getTaskList(curSubject).size()) {
                    return 1;
                }
                int randInd = new Random().nextInt(2);
                return randInd == 1 ? 1 : -1;
            }
        };
        sortedStudentList = Stream.of(sortedStudentList)
                .sorted(studentComparator).collect(Collectors.toList());

        int[] matching = new int[taskList.size()];
        boolean[] used = new boolean[studentList.size()];
        Arrays.fill(matching, -1);
        List<List<Integer>> studentToTaskEdges = new ArrayList<>();
        List<List<Integer>> taskToStudentEdges = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            taskToStudentEdges.add(new ArrayList<>());
        }
        for (int i = 0; i < taskCount; i++) {
            if (taskSubmitCount.get(i) >= 0) {
                distributionTable[taskSubmitCount.get(i)][i] = CHOSEN;
            }
        }
        for (int student = 0; student < studentCount; student++) {
            studentToTaskEdges.add(new ArrayList<>());
            if (taskSubmitCount.indexOf(student) != -1) {
                continue;
            }
            for (int task : studentList.get(student).getTaskList(curSubject)) {
                int taskId = taskList.indexOf(task);
                studentToTaskEdges.get(student).add(taskId);
                taskToStudentEdges.get(taskId).add(student);
            }
        }


        for (int i = 0; i < studentCount; ++i) {
            Arrays.fill(used, false);
            findMatching(sortedStudentList.get(i).second, studentToTaskEdges, matching, used);
        }

        boolean[] isStudentPickedSecondTime = new boolean[studentCount];
        for (int task = 0; task < taskToStudentEdges.size(); task++) {
            if (matching[task] != -1) {
                distributionTable[matching[task]][task] = CHOSEN;
            } else {
                List<Pair<Student, Integer>> candidates = new ArrayList<>();
                for (int student = 0; student < taskToStudentEdges.get(task).size(); student++) {
                    int studentInd = taskToStudentEdges.get(task).get(student);
                    if (!isStudentPickedSecondTime[studentInd]) {
                        candidates.add(new Pair<>(studentList.get(studentInd), studentInd));
                    }
                }
                com.annimon.stream.Optional<Pair<Student, Integer>> chosen
                        = Stream.of(candidates).min(studentComparator);
                if (chosen.isPresent()) {
                    int chosenInd = chosen.get().second;
                    isStudentPickedSecondTime[chosenInd] = true;
                    distributionTable[chosenInd][task] = CHOSEN;
                }


            }
        }

        List<String> studentNames = Stream.of(studentList).map(User::getName).collect(Collectors.toList());
        List<Integer> studentPoints = Stream.of(studentList).map(student -> student.getPoints(curSubject)).collect(Collectors.toList());
        List<List<Integer>> distributionList = new ArrayList<>();
        for (int[] row : distributionTable) {
            List<Integer> rowList = new ArrayList<>();
            for (int el : row) {
                rowList.add(el);
            }
            distributionList.add(rowList);
        }
        distribution = new Distribution(studentNames, studentPoints, taskList, distributionList);


    }

    boolean findMatching(int v, List<List<Integer>> edges, int[] matching, boolean[] used) {
        if (used[v]) return false;
        used[v] = true;
        for (int i = 0; i < edges.get(v).size(); ++i) {
            int u = edges.get(v).get(i);
            if (matching[u] == -1 || findMatching(matching[u], edges, matching, used)) {
                matching[u] = v;
                return true;
            }
        }
        return false;
    }

    private void addTableItem(GridLayout gridLayout, TextView tv, int column, int row) {
        gridLayout.addView(tv);
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(row);
        param.setMargins(2, 2, 2, 2);
        tv.setLayoutParams(param);
    }

    private TextView createNameTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setSingleLine(true);
        tv.setTextSize(20f);
        tv.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        tv.setPadding(2, 0, 2, 0);
        tv.setWidth((int) (150 * scale + 0.5f));
        tv.setHeight((int) (40 * scale + 0.5f));
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    private TextView createTaskTextView(String text, int colorId) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(20f);
        tv.setSingleLine(true);
        tv.setBackgroundColor(getResources().getColor(colorId));
        tv.setPadding(0, 0, 0, 0);
        tv.setWidth((int) (40 * scale + 0.5f));
        tv.setHeight((int) (40 * scale + 0.5f));
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    @Override
    protected void onStop() {
        App.getPrefs().setDistribution(distribution);
        super.onStop();
    }

    @UiThread
    public void showSnackBar(int messageId) {
        Snackbar.make(gridLayoutNames, messageId, Snackbar.LENGTH_SHORT).show();
    }

    public static final String TAG = StudentTaskListActivity.class.getName();

}

