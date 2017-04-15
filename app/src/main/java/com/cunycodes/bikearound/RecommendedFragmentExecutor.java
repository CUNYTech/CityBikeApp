package com.cunycodes.bikearound;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class RecommendedFragmentExecutor extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execute_fragments);
        setTitle("Recommended Places");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(), RecommendedFragmentExecutor.this);
        viewPager.setAdapter(pageAdapter);

        final TabLayout layout = (TabLayout) findViewById(R.id.tab_layout);
        layout.setupWithViewPager(viewPager);

        for (int i =0; i<layout.getTabCount(); i++){
            TabLayout.Tab tab = layout.getTabAt(i);
            tab.setCustomView(pageAdapter.getTabview(i));
            tab.select();
        }

    }

    class PageAdapter extends FragmentPagerAdapter {

        String[] tabs = {"New York", "Queens", "Bronx"};
        Context context;

        public PageAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new NewYorkFragment();
                case 1:
                    return new QueensFragment();
                case 2:
                    return new BronxFragment();
              /*  case 3:
                    return new NewYorkPathFragment();
                case 4:
                    return new NewYorkPathFragment(); */
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        public View getTabview(int i){
            View tab = LayoutInflater.from(RecommendedFragmentExecutor.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabs[i]);
            return tab;
        }

    }
}
