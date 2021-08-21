package com.example.chatpeki.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.chatpeki.Fragment.GruplarFragment;
import com.example.chatpeki.Fragment.SohbetlerFragment;

public class SohbetveGrupErisimSekmeleri extends FragmentPagerAdapter {

    public SohbetveGrupErisimSekmeleri(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
            SohbetlerFragment sohbetlerFragment=new SohbetlerFragment();
            return sohbetlerFragment;
            case 1:
                GruplarFragment gruplarFragment=new GruplarFragment();
                return gruplarFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
               return "Sohbetler";
            case 1:
                return "Gruplar";
            default:
                return null;
        }
    }
}
