package ru.softvillage.mailer_test.ui.left_menu;

import android.annotation.SuppressLint;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.Locale;

import ru.softvillage.mailer_test.BuildConfig;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.presetner.SessionPresenter;
import ru.softvillage.mailer_test.ui.IMainView;

public class DrawerMenuManager<T extends AppCompatActivity> implements IMainView, View.OnClickListener {

    private AppCompatActivity activity;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ConstraintLayout drawerMenu;
    private ImageView changeTheme;
    private View dividerExit;
    private FrameLayout changeThemeBottom;
    private TextView titleParams,
            titleExit,
            version;

    private ActionBarDrawerToggle toggle;
    private ActionBar mActionBar;


    public DrawerMenuManager(T activity) {
        this.activity = activity;
        initMenu();
        SessionPresenter.getInstance().setiMainView1(this);
        SessionPresenter.getInstance().setDrawerMenuManager(this);
    }

    private void initMenu() {
        toolbar = activity.findViewById(R.id.toolbar);
        drawer = activity.findViewById(R.id.drawer);
        drawerMenu = activity.findViewById(R.id.drawer_menu);
        changeTheme = activity.findViewById(R.id.changeTheme);
        dividerExit = activity.findViewById(R.id.dividerExit);
        changeThemeBottom = activity.findViewById(R.id.changeThemeBottom);
        titleParams = activity.findViewById(R.id.titleParams);
        titleExit = activity.findViewById(R.id.titleExit);
        version = activity.findViewById(R.id.version);

        updateVersion();
        updateUITheme();

        titleExit.setOnClickListener(this);
        changeThemeBottom.setOnClickListener(this);

        toolbar.setTitle(activity.getString(R.string.app_name));
        activity.setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActionBar = activity.getSupportActionBar();
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawerMenu.post(new Runnable() {
            @Override
            public void run() {
                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerMenu.getLayoutParams();

                DisplayMetrics displaymetrics = activity.getResources().getDisplayMetrics();
                if (displaymetrics.widthPixels == 1280 && displaymetrics.heightPixels == 740) {
                    params.width = Double.valueOf(activity.getResources().getDisplayMetrics().widthPixels * 0.3).intValue();
                } else {
                    params.width = Double.valueOf(activity.getResources().getDisplayMetrics().widthPixels * 0.87).intValue();

                }
                drawerMenu.setLayoutParams(params);
            }
        });
    }

    private void updateUITheme() {
        themeChange(SessionPresenter.getInstance().getCurrentTheme());
    }

    private void updateVersion() {
        version.setText(String.format(Locale.getDefault(), "v %s (%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
    }

    @Override
    public void themeChange(int themeStyle) {
        if (themeStyle == SessionPresenter.THEME_LIGHT) {
            toolbar.setBackgroundColor(ContextCompat.getColor(toolbar.getContext(), R.color.header_lt));
            drawer.setBackgroundColor(ContextCompat.getColor(drawer.getContext(), R.color.background_lt));
            drawerMenu.setBackgroundColor(ContextCompat.getColor(drawerMenu.getContext(), R.color.background_lt));
            dividerExit.setBackgroundColor(ContextCompat.getColor(dividerExit.getContext(), R.color.divider_lt));
            changeTheme.setImageResource(R.drawable.ic_moon);
            titleParams.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));
            titleExit.setTextColor(ContextCompat.getColor(titleExit.getContext(), R.color.fonts_lt));
            version.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_lt));
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(toolbar.getContext(), R.color.background_dt));
            drawer.setBackgroundColor(ContextCompat.getColor(drawer.getContext(), R.color.background_dt));
            drawerMenu.setBackgroundColor(ContextCompat.getColor(drawerMenu.getContext(), R.color.background_dt));
            dividerExit.setBackgroundColor(ContextCompat.getColor(dividerExit.getContext(), R.color.divider_dt));
            changeTheme.setImageResource(R.drawable.ic_sun);
            titleParams.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));
            titleExit.setTextColor(ContextCompat.getColor(titleExit.getContext(), R.color.fonts_dt));
            version.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_dt));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleExit:
                break;
            case R.id.changeThemeBottom:
                int currentTheme = SessionPresenter.getInstance().getCurrentTheme();
                SessionPresenter.getInstance().setCurrentTheme(currentTheme + 1);
                break;
        }
    }
}
