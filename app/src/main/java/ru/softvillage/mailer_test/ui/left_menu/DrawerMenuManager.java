package ru.softvillage.mailer_test.ui.left_menu;

import android.annotation.SuppressLint;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import ru.softvillage.mailer_test.ui.IUpdateCountersLeftMenu;
import ru.softvillage.mailer_test.ui.dialog.ExitDialog;

public class DrawerMenuManager<T extends AppCompatActivity>
        implements IMainView,
        View.OnClickListener,
        IUpdateCountersLeftMenu {

    private final AppCompatActivity activity;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ConstraintLayout drawerMenu;
    private ImageView changeTheme,
            icon_receipts_count,
            icon_send_sms,
            icon_send_email,
            icon_sim,
            icon_about;

    private Drawable dIconReceiptsCount,
            dIconSendSms,
            dIconSendEmail,
            dIconSim,
            dIconAbout;

    private View divider_left_menu_title,
            divider_left_menu_values_module,
            dividerExit;

    private FrameLayout changeThemeBottom;
    private TextView titleParams,
            title_receipts_count,
            value_receipts_count,
            title_send_sms,
            value_send_sms,
            title_send_email,
            value_send_email,
            title_sim,
            title_about,
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
        SessionPresenter.getInstance().setUpdateCounterOnUi(this);
    }

    private void initMenu() {
        toolbar = activity.findViewById(R.id.toolbar);
        drawer = activity.findViewById(R.id.drawer);
        drawerMenu = activity.findViewById(R.id.drawer_menu);
        changeTheme = activity.findViewById(R.id.changeTheme);
        icon_receipts_count = activity.findViewById(R.id.icon_receipts_count);
        icon_send_sms = activity.findViewById(R.id.icon_send_sms);
        icon_send_email = activity.findViewById(R.id.icon_send_email);
        icon_sim = activity.findViewById(R.id.icon_sim);
        icon_about = activity.findViewById(R.id.icon_about);
        divider_left_menu_title = activity.findViewById(R.id.divider_left_menu_title);
        divider_left_menu_values_module = activity.findViewById(R.id.divider_left_menu_values_module);
        dividerExit = activity.findViewById(R.id.dividerExit);
        changeThemeBottom = activity.findViewById(R.id.changeThemeBottom);
        titleParams = activity.findViewById(R.id.titleParams);
        title_receipts_count = activity.findViewById(R.id.title_receipts_count);
        value_receipts_count = activity.findViewById(R.id.value_receipts_count);
        title_send_sms = activity.findViewById(R.id.title_send_sms);
        value_send_sms = activity.findViewById(R.id.value_send_sms);
        title_send_email = activity.findViewById(R.id.title_send_email);
        value_send_email = activity.findViewById(R.id.value_send_email);
        title_sim = activity.findViewById(R.id.title_sim);
        title_about = activity.findViewById(R.id.title_about);
        titleExit = activity.findViewById(R.id.titleExit);
        version = activity.findViewById(R.id.version);

        dIconReceiptsCount = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_left_menu_receipt_count);
        dIconSendSms = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_left_menu_send_sms);
        dIconSendEmail = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_left_menu_send_email);
        dIconSim = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_left_menu_sim);
        dIconAbout = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_left_menu_about);

        updateVersion();
        updateUITheme();
        updateValue_receipts_count(SessionPresenter.getInstance().getCountAllReceipt());
        updateValue_send_sms(SessionPresenter.getInstance().getCountSendSms());
        updateValue_send_email(SessionPresenter.getInstance().getCountSendEmail());

        titleExit.setOnClickListener(this);
        changeThemeBottom.setOnClickListener(this);
        title_about.setOnClickListener(this);

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
        changeIconColor(themeStyle);
        if (themeStyle == SessionPresenter.THEME_LIGHT) {
            toolbar.setBackgroundColor(ContextCompat.getColor(toolbar.getContext(), R.color.header_lt));
            drawer.setBackgroundColor(ContextCompat.getColor(drawer.getContext(), R.color.background_lt));
            drawerMenu.setBackgroundColor(ContextCompat.getColor(drawerMenu.getContext(), R.color.background_lt));
            divider_left_menu_title.setBackgroundColor(ContextCompat.getColor(divider_left_menu_title.getContext(), R.color.divider_lt));
            divider_left_menu_values_module.setBackgroundColor(ContextCompat.getColor(divider_left_menu_values_module.getContext(), R.color.divider_lt));
            dividerExit.setBackgroundColor(ContextCompat.getColor(dividerExit.getContext(), R.color.divider_lt));
            changeTheme.setImageResource(R.drawable.ic_moon);
            titleParams.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));
            title_receipts_count.setTextColor(ContextCompat.getColor(title_receipts_count.getContext(), R.color.fonts_lt));
            value_receipts_count.setTextColor(ContextCompat.getColor(value_receipts_count.getContext(), R.color.fonts_lt));
            title_send_sms.setTextColor(ContextCompat.getColor(title_send_sms.getContext(), R.color.fonts_lt));
            value_send_sms.setTextColor(ContextCompat.getColor(value_send_sms.getContext(), R.color.fonts_lt));
            title_send_email.setTextColor(ContextCompat.getColor(title_send_email.getContext(), R.color.fonts_lt));
            value_send_email.setTextColor(ContextCompat.getColor(value_send_email.getContext(), R.color.fonts_lt));
            title_sim.setTextColor(ContextCompat.getColor(title_sim.getContext(), R.color.fonts_lt));
            title_about.setTextColor(ContextCompat.getColor(title_about.getContext(), R.color.fonts_lt));
            titleExit.setTextColor(ContextCompat.getColor(titleExit.getContext(), R.color.fonts_lt));
            version.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_lt));
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(toolbar.getContext(), R.color.background_dt));
            drawer.setBackgroundColor(ContextCompat.getColor(drawer.getContext(), R.color.background_dt));
            drawerMenu.setBackgroundColor(ContextCompat.getColor(drawerMenu.getContext(), R.color.background_dt));
            divider_left_menu_title.setBackgroundColor(ContextCompat.getColor(divider_left_menu_title.getContext(), R.color.divider_dt));
            divider_left_menu_values_module.setBackgroundColor(ContextCompat.getColor(divider_left_menu_values_module.getContext(), R.color.divider_dt));
            dividerExit.setBackgroundColor(ContextCompat.getColor(dividerExit.getContext(), R.color.divider_dt));
            changeTheme.setImageResource(R.drawable.ic_sun);
            titleParams.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));
            title_receipts_count.setTextColor(ContextCompat.getColor(title_receipts_count.getContext(), R.color.fonts_dt));
            value_receipts_count.setTextColor(ContextCompat.getColor(value_receipts_count.getContext(), R.color.fonts_dt));
            title_send_sms.setTextColor(ContextCompat.getColor(title_send_sms.getContext(), R.color.fonts_dt));
            value_send_sms.setTextColor(ContextCompat.getColor(value_send_sms.getContext(), R.color.fonts_dt));
            title_send_email.setTextColor(ContextCompat.getColor(title_send_email.getContext(), R.color.fonts_dt));
            value_send_email.setTextColor(ContextCompat.getColor(value_send_email.getContext(), R.color.fonts_dt));
            title_sim.setTextColor(ContextCompat.getColor(title_sim.getContext(), R.color.fonts_dt));
            title_about.setTextColor(ContextCompat.getColor(title_about.getContext(), R.color.fonts_dt));
            titleExit.setTextColor(ContextCompat.getColor(titleExit.getContext(), R.color.fonts_dt));
            version.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_dt));
        }
        icon_receipts_count.setImageDrawable(dIconReceiptsCount);
        icon_send_sms.setImageDrawable(dIconSendSms);
        icon_send_email.setImageDrawable(dIconSendEmail);
        icon_sim.setImageDrawable(dIconSim);
        icon_about.setImageDrawable(dIconAbout);
    }

    private void changeIconColor(int themeStyle) {
        int tabIconColor = ContextCompat.getColor(activity.getApplicationContext(), R.color.icon_dt);
        if (themeStyle == SessionPresenter.THEME_LIGHT) {
            tabIconColor = ContextCompat.getColor(activity.getApplicationContext(), R.color.fonts_lt);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dIconReceiptsCount.setColorFilter(new BlendModeColorFilter(tabIconColor, BlendMode.SRC_IN));
            dIconSendSms.setColorFilter(new BlendModeColorFilter(tabIconColor, BlendMode.SRC_IN));
            dIconSendEmail.setColorFilter(new BlendModeColorFilter(tabIconColor, BlendMode.SRC_IN));
            dIconSim.setColorFilter(new BlendModeColorFilter(tabIconColor, BlendMode.SRC_IN));
            dIconAbout.setColorFilter(new BlendModeColorFilter(tabIconColor, BlendMode.SRC_IN));
        } else {
            dIconReceiptsCount.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            dIconSendSms.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            dIconSendEmail.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            dIconSim.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            dIconAbout.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
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
            case R.id.title_about:
//                activity.getSupportFragmentManager().beginTransaction().replace(R.id.drawer, new AboutFragment()).addToBackStack("about_fragment").commit();
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

    @Override
    public void updateValue_receipts_count(int allReceiptCount) {
        activity.runOnUiThread(() -> value_receipts_count.setText(String.format(activity.getString(R.string.value_left_menu), allReceiptCount)));
    }

    @Override
    public void updateValue_send_sms(int countSendSms) {
        activity.runOnUiThread(() ->value_send_sms.setText(String.format(activity.getString(R.string.value_left_menu), countSendSms)));
    }

    @Override
    public void updateValue_send_email(int countSendEmail) {
        activity.runOnUiThread(() ->value_send_email.setText(String.format(activity.getString(R.string.value_left_menu), countSendEmail)));
    }
}
