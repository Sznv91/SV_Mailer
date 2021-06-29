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
import ru.softvillage.mailer_test.ui.dialog.ExitDialog;

public class DrawerMenuManager<T extends AppCompatActivity> implements IMainView, View.OnClickListener {

    private final AppCompatActivity activity;

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
    private boolean mToolBarNavigationListenerIsRegistered = false;


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
        SessionPresenter.getInstance().setDrawerMenuManager(this);
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
                ExitDialog exitDialog = ExitDialog.newInstance();
                exitDialog.setCancelable(false);
                exitDialog.show(activity.getSupportFragmentManager(), ExitDialog.class.getSimpleName());
                break;
            case R.id.changeThemeBottom:
                int currentTheme = SessionPresenter.getInstance().getCurrentTheme();
                SessionPresenter.getInstance().setCurrentTheme(currentTheme + 1);
                break;
        }
    }

    public void showUpButton(boolean show) {
        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (show) {
            //Запрещаяем выезжание меню справа
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            // Remove hamburger
            toggle.setDrawerIndicatorEnabled(false);
            // Show back button
            mActionBar.setDisplayHomeAsUpEnabled(true);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.onBackPressed();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
            //Разрешаем выезжание меню справа
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            // Remove back button
            mActionBar.setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            toggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            toggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }

        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }
}
