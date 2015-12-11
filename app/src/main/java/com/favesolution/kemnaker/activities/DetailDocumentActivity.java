package com.favesolution.kemnaker.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.favesolution.kemnaker.fragments.DetailDocumentFragment;
import com.favesolution.kemnaker.models.User;

/**
 * Created by Daniel on 12/6/2015 for Kemnaker project.
 */
public class DetailDocumentActivity extends SingleFragmentActivity {
    private final static String EXTRA_USER = "extra_user";
    private final static String EXTRA_DOKUMENT_ID = "extra_dokument_id";
    private final static String EXTRA_MENU = "extra_menu";
    @Override
    public Fragment createFragment() {
        User user = getIntent().getParcelableExtra(EXTRA_USER);
        String id = getIntent().getStringExtra(EXTRA_DOKUMENT_ID);
        String menu = getIntent().getStringExtra(EXTRA_MENU);
        return DetailDocumentFragment.newInstance(user,id,menu);
    }


    public static Intent newIntent(Context context,User user,String id,String menu) {
        Intent i = new Intent(context,DetailDocumentActivity.class);
        i.putExtra(EXTRA_USER,user);
        i.putExtra(EXTRA_DOKUMENT_ID,id);
        i.putExtra(EXTRA_MENU,menu);
        return i;
    }
}
