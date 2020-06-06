package app.kevs.biyang.game.libs.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import app.kevs.biyang.game.fragments.MonsterCards

class Tab_adapter(val fm : FragmentManager, val tabCount : Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return MonsterCards()
        }
        return MonsterCards()
    }

    override fun getCount(): Int {
        return tabCount
    }
}