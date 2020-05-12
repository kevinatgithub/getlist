package app.kevs.biyang.getlist;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import adapter.Tab_adapter;

public class Home_pageActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tablayout;


    private Typeface mTypeface;
    private Typeface mTypefaceBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        viewPager=findViewById(R.id.viewpager);
        tablayout=findViewById(R.id.tablayout1);


        setCustomFontAndStyle(tablayout, 0);
        tablayout.setTabGravity(TabLayout.GRAVITY_FILL);


        tablayout.addTab(tablayout.newTab().setText("Hotel"));
        tablayout.addTab(tablayout.newTab().setText("Cab"));
        tablayout.addTab(tablayout.newTab().setText("Flight"));
        tablayout.addTab(tablayout.newTab().setText("Bus"));
        tablayout.addTab(tablayout.newTab().setText("Train"));

        Typeface mTypeface = Typeface.createFromAsset(getAssets(), "font/roboto_regular.ttf");
        ViewGroup vg = (ViewGroup) tablayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(mTypeface, Typeface.NORMAL);

                }
            }
        }


        Tab_adapter adapter = new Tab_adapter(getSupportFragmentManager(), tablayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                LinearLayout tabLayout = (LinearLayout)((ViewGroup) tablayout.getChildAt(0)).getChildAt(tab.getPosition());
                TextView tabTextView = (TextView) tabLayout.getChildAt(1);
                tabTextView.setTypeface(tabTextView.getTypeface(), Typeface.BOLD);
                Typeface mTypeface = Typeface.createFromAsset(getAssets(), "font/roboto_regular.ttf");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                LinearLayout tabLayout = (LinearLayout)((ViewGroup) tablayout.getChildAt(0)).getChildAt(tab.getPosition());
                TextView tabTextView = (TextView) tabLayout.getChildAt(1);
                tabTextView.setTypeface(tabTextView.getTypeface(), Typeface.NORMAL);


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setCustomFontAndStyle(TabLayout tabLayout, Integer position) {

        mTypeface = Typeface.createFromAsset(getAssets(), "font/sfpro_display_bold.otf");
        mTypefaceBold = Typeface.createFromAsset(getAssets(), "font/Roboto-Regular.ttf");
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    if (j == position) {
                        ((TextView) tabViewChild).setTypeface(mTypefaceBold, Typeface.BOLD);
                    } else {
                        ((TextView) tabViewChild).setTypeface(mTypeface, Typeface.NORMAL);
                    }
                }
            }
        }
    }
}