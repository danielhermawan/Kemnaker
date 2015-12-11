package com.favesolution.kemnaker.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.favesolution.kemnaker.fragments.ListDocumentFragment;
import com.favesolution.kemnaker.models.User;

/**
 * Created by Daniel on 12/5/2015 for Kemnaker project.
 */
public class ListDocumentActivity extends SingleFragmentActivity {
    private final static String EXTRA_USER = "extra_user";
    private final static String EXTRA_MENU = "extra_menu";
    @Override
    public Fragment createFragment() {
        User user = getIntent().getParcelableExtra(EXTRA_USER);
        String menu = getIntent().getStringExtra(EXTRA_MENU);
        return ListDocumentFragment.newInstance(menu,user);
    }
    public static Intent newIntent(Context context,User user,String menu) {
        Intent i = new Intent(context,ListDocumentActivity.class);
        i.putExtra(EXTRA_MENU,menu);
        i.putExtra(EXTRA_USER, user);
        return i;
    }
}
