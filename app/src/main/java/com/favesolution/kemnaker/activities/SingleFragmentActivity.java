package com.favesolution.kemnaker.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.favesolution.kemnaker.R;

import java.lang.reflect.Field;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    private TextView mTitleText;
    public abstract Fragment createFragment();
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleText = (TextView) toolbar.findViewById(R.id.text_title);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container_fragment);
        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.container_fragment,fragment).commit();
        }
        showOverflowMenu(this);
    }
    public void setTitleToolbar(String title) {
        mTitleText.setText(title);
    }
    private void showOverflowMenu(FragmentActivity activity) {
        try {
            ViewConfiguration config = ViewConfiguration.get(activity);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}