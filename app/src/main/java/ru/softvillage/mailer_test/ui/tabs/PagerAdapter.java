package ru.softvillage.mailer_test.ui.tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ru.softvillage.mailer_test.ui.fragmet.AllReceipt;
import ru.softvillage.mailer_test.ui.fragmet.SendReceipt;

public class PagerAdapter extends FragmentStateAdapter {
    private final int numOfTab;

    public PagerAdapter(@NonNull Fragment fragment, int numOfTab) {
        super(fragment);
        this.numOfTab = numOfTab;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return AllReceipt.newInstance();
            case 1:
                return SendReceipt.newInstance(0);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return numOfTab;
    }
}
