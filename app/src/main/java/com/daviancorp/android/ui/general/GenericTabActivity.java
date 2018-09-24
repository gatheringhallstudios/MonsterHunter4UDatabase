package com.daviancorp.android.ui.general;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.daviancorp.android.mh4udatabase.R;

/*
 * Any subclass needs to:
 *  - override onCreate() to set title
 */

public abstract class GenericTabActivity extends GenericActionBarActivity {

	protected Fragment detail;
    protected TabLayout mSlidingTabLayout;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tab);

        // Integrate Toolbar so sliding drawer can go over toolbar
        android.support.v7.widget.Toolbar mtoolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mtoolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Set up tabs
        mSlidingTabLayout = findViewById(R.id.sliding_tabs);

        ViewPager pager = findViewById(R.id.pager);
        mSlidingTabLayout.setupWithViewPager(pager);

        setTitle(R.string.app_name);
        super.setupDrawer(); // Needs to be called after setContentView
        // Disabled by request. Turns into BACK button
        //super.enableDrawerIndicator(); // Enable drawer toggle button
	}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mSlidingTabLayout.getTabCount() > 4) {
            mSlidingTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }
}
