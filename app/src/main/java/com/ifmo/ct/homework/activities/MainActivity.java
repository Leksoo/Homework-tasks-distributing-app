package com.ifmo.ct.homework.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ifmo.ct.homework.App;
import com.ifmo.ct.homework.R;

public abstract class MainActivity extends AppCompatActivity {

    public static final int DM_CODE = 3;
    public static final int ADS_CODE = 4;

    protected int curSubject;

    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setToolBar(Toolbar tb) {
        toolbar = tb;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.discrete_math);
        curSubject = DM_CODE;
        changeToDM();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_math: {
                getSupportActionBar().setTitle(R.string.discrete_math);
                curSubject = DM_CODE;
                changeToDM();
                return true;
            }
            case R.id.action_algo: {
                getSupportActionBar().setTitle(R.string.algorithms);
                curSubject = ADS_CODE;
                changeToADS();
                return true;
            }
            case R.id.action_exit: {
                App.getPrefs().cleanUser();
                App.getSheetPrefs().clear();
                SignUpActivity_.intent(this)
                        .flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    protected abstract void changeToDM();

    protected abstract void changeToADS();

    @Override
    public void onBackPressed() {

    }
}
