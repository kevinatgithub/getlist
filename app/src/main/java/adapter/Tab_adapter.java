package adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import fragement.Bus;
import fragement.Cab;
import fragement.Flight;
import fragement.Hotel;
import fragement.Train;

public class Tab_adapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public Tab_adapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Hotel tab1 = new Hotel();
                return tab1;
            case 1:
                Cab tab2 = new Cab();
                return tab2;
            case 2:
                Flight tab3 = new Flight();
                return tab3;
            case 3:
                Bus tab4 = new Bus();
                return tab4;
            case 4:
                Train tab5 = new Train();
                return tab5;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
