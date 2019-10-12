package com.prova.promemorialong;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class StatsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.stats));
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);

        if (findViewById(R.id.fragment_container) != null) {
            loadFragment(new GraphPieFragment());//per caricare inizialmente il primo fragment
        } else loadFragment(new GraphPieFragment(),new GraphHistogramFragment());

    }

    //metodo per caricare il fragment (che chiamo cliccando sulla barra)
    private boolean loadFragment (Fragment fragment) {
        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private boolean loadFragment (Fragment fragment1,Fragment fragment2) {
        if (fragment1 != null && fragment2 != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container1,fragment1)
                    .replace(R.id.fragment_container2,fragment2)
                    .commit();
            return true;
        }
        return false;
    }

    //riconosco il click sullo specifico elemento della navigation bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;

        switch (menuItem.getItemId()){
            case R.id.navigation_pie:
                fragment = new GraphPieFragment();
                break;
            case R.id.navigation_hist:
                fragment = new GraphHistogramFragment();
                break;
            case R.id.navigation_pie2:
                fragment = new GraphPie2Fragment();
                break;
            case R.id.navigation_line:
                fragment = new GraphLineFragment();
                break;
            case R.id.navigation_land1:
                return loadFragment(new GraphPieFragment(),new GraphHistogramFragment());
            case R.id.navigation_land2:
                return loadFragment(new GraphPie2Fragment(),new GraphLineFragment());
        }
        return loadFragment(fragment);
    }
}
