package com.yfortier.koifaire.ListeFragment;

public class RecyclerViewItem {
    private String mTitre;
    private String mDates;
    private String mDomaine;
    private int mDistance;
    private String mVille;

    public RecyclerViewItem(String mTitre, String mDates, String mDomaine, int mDistance, String mVille) {
        this.mTitre = mTitre;
        this.mDates = mDates;
        this.mDomaine = mDomaine;
        this.mDistance = mDistance;
        this.mVille = mVille;
    }

    public String getmTitre() {
        return mTitre;
    }

    public String getmDates() {
        return mDates;
    }

    public String getmDomaine() {
        return mDomaine;
    }

    public int getmDistance() {
        return mDistance;
    }

    public String getmVille() {
        return mVille;
    }
}
