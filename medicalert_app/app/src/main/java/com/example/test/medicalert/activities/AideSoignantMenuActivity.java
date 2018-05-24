package com.example.test.medicalert.activities;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import com.example.test.medicalert.R;

public class AideSoignantMenuActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidesoignant_menu);

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), getString(R.string.tab_aidesoignant_1));
        adapter.addFragment(new Tab2Fragment(), getString(R.string.tab_aidesoignant_2));
        adapter.addFragment(new Tab3Fragment(), getString(R.string.tab_aidesoignant_3));
        viewPager.setAdapter(adapter);
    }
}
