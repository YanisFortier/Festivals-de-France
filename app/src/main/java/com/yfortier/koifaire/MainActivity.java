package com.yfortier.koifaire;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.yfortier.koifaire.model.Festival;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static List<Festival> festivals;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Menu
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        CustomViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


    }

    /* Menu avec Overflow */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings: {
                Toast.makeText(this, "Settings !", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.about: {
                Toast.makeText(this, "A propos !", Toast.LENGTH_LONG).show();
                break;
            }
        }
        return true;
    }


}