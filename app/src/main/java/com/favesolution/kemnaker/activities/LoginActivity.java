package com.favesolution.kemnaker.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.favesolution.kemnaker.fragments.LoginFragment;

/**
 * Created by Daniel on 12/2/2015 for Kemnaker project.
 */
public class LoginActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return LoginFragment.newInstance();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().hide();
    }
}
