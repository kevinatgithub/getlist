package app.kevs.biyang.game

import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.kevs.biyang.game.libs.adapters.Tab_adapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.game_activity_dashboard.*

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity_dashboard)

        setCustomFontAndStyle(tablayout, 0)
        tablayout.setTabGravity(TabLayout.GRAVITY_FILL)


        tablayout.addTab(tablayout.newTab().setText("Cards"))
        tablayout.addTab(tablayout.newTab().setText("Spells"))
        tablayout.addTab(tablayout.newTab().setText("Duels"))

        val mTypeface = Typeface.createFromAsset(assets, "font/roboto_regular.ttf")
        val vg = tablayout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildsCount = vgTab.childCount
            for (i in 0 until tabChildsCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.setTypeface(mTypeface, Typeface.NORMAL)
                }
            }
        }


        val adapter = Tab_adapter(supportFragmentManager, tablayout.getTabCount())
        viewPager.setAdapter(adapter)
        viewPager.setOffscreenPageLimit(5)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.setCurrentItem(tab.getPosition())
                val tabLayout =
                    (tablayout.getChildAt(0) as ViewGroup).getChildAt(tab.getPosition()) as LinearLayout
                val tabTextView = tabLayout.getChildAt(1) as TextView
                tabTextView.setTypeface(tabTextView.typeface, Typeface.BOLD)
                val mTypeface =
                    Typeface.createFromAsset(assets, "font/roboto_regular.ttf")
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabLayout =
                    (tablayout.getChildAt(0) as ViewGroup).getChildAt(tab.getPosition()) as LinearLayout
                val tabTextView = tabLayout.getChildAt(1) as TextView
                tabTextView.setTypeface(tabTextView.typeface, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setCustomFontAndStyle(tabLayout: TabLayout, position: Int) {
        val mTypeface = Typeface.createFromAsset(assets, "font/sfpro_display_bold.otf")
        val mTypefaceBold = Typeface.createFromAsset(assets, "font/Roboto-Regular.ttf")
        val vg = tabLayout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildsCount = vgTab.childCount
            for (i in 0 until tabChildsCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    if (j == position) {
                        tabViewChild.setTypeface(mTypefaceBold, Typeface.BOLD)
                    } else {
                        tabViewChild.setTypeface(mTypeface, Typeface.NORMAL)
                    }
                }
            }
        }
    }
}