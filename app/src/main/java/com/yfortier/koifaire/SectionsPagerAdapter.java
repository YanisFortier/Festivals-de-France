package com.yfortier.koifaire;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yfortier.koifaire.ListeFragment.ListeFragment;
import com.yfortier.koifaire.model.Festival;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    private ArrayList<Festival> favoris;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;

        loadData();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new MapFragment();
                loadData();
                break;
            case 1:
                fragment = new ListeFragment();
                loadData();
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        loadData();
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    public void loadData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("SHARED", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favoris", null);
        Type type = new TypeToken<ArrayList<Festival>>() {
        }.getType();
        favoris = gson.fromJson(json, type);

        if (favoris == null)
            favoris = new ArrayList<>();
    }
}