package com.example.test.medicalert.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.test.medicalert.R;

public class PatientMenuActivity extends AppCompatActivity{
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_menu);

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new TraitementPatientTabFragment(), getString(R.string.tab_patient_1));
        adapter.addFragment(new RendezVousAideSoignantTabFragment(), getString(R.string.tab_patient_2));
        viewPager.setAdapter(adapter);
    }
}
