package ru.softvillage.mailer_test.ui.tabs;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.presetner.SessionPresenter;

public class TabLayoutFragment extends Fragment {
    private TabLayout tab_layout;
    private ViewPager2 view_pager;
    private View tab_divider_horizontal;
    private Drawable all_receipt_icon,
            send_receipt_icon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_layout_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tab_layout = view.findViewById(R.id.tab_layout);
        view_pager = view.findViewById(R.id.view_pager);
        tab_divider_horizontal = view.findViewById(R.id.tab_divider_horizontal);
        all_receipt_icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_tab_receipt_all);
        send_receipt_icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_tab_receipt_send);

        initTabs();
        initColorTheme();
    }

    private void initTabs() {
        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.header_lt);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tab.getIcon().setColorFilter(new BlendModeColorFilter(tabIconColor, BlendMode.SRC_IN));
                } else {
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.active_fonts_lt);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    tab.getIcon().setColorFilter(new BlendModeColorFilter(tabIconColor, BlendMode.SRC_IN));
                } else {
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        PagerAdapter adapter = new PagerAdapter(this, 2);

        view_pager.setAdapter(adapter);
        new TabLayoutMediator(tab_layout, view_pager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getText(R.string.tab_all_receipt));
                    tab.setIcon(all_receipt_icon);
                    break;
                case 1:
                    tab.setText(getText(R.string.tab_send_receipt));
                    tab.setIcon(send_receipt_icon);
            }
            view_pager.setCurrentItem(tab.getPosition(), true);
        }).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SessionPresenter.getInstance().getCurrentThemeLiveData().removeObservers(getViewLifecycleOwner());
    }

    private void initColorTheme() {

        SessionPresenter.getInstance().getCurrentThemeLiveData().observe(getViewLifecycleOwner(), currentTheme -> {
            if (currentTheme == SessionPresenter.THEME_LIGHT) {
                tab_divider_horizontal.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_lt));
                tab_layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.main_lt));
                tab_layout.setTabTextColors(ContextCompat.getColor(
                        getContext(), R.color.active_fonts_lt),
                        ContextCompat.getColor(getContext(), R.color.fonts_lt));
                view_pager.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_lt));
            } else {
                tab_divider_horizontal.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_dt));
                tab_layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.main_dt));
                tab_layout.setTabTextColors(ContextCompat.getColor(
                        getContext(), R.color.active_fonts_dt),
                        ContextCompat.getColor(getContext(), R.color.fonts_dt));
                view_pager.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_dt));
            }
        });
    }
}
