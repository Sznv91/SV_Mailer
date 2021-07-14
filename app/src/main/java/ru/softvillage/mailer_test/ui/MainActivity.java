package ru.softvillage.mailer_test.ui;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.presetner.SessionPresenter;
import ru.softvillage.mailer_test.service.EvoReceiptAdderService;
import ru.softvillage.mailer_test.service.SendToBackendService;
import ru.softvillage.mailer_test.ui.left_menu.DrawerMenuManager;
import ru.softvillage.mailer_test.ui.tabs.TabLayoutFragment;
import ru.softvillage.mailer_test.ui.tabs.TabLayoutFragmentLandscape;

import static ru.softvillage.mailer_test.App.isMyServiceRunning;

/**
 * SplashScreen выполнен по статье
 * https://habr.com/ru/post/345380/
 * <p>
 * Tabs:
 * https://abhiandroid.com/materialdesign/tablayout-example-android-studio.html
 * --> Example 2 of TabLayout Using ViewPager
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Для запрета возврата из ReceiptDetailFragment до окончания загрузки данных на UI
     */
    @SuppressLint("LongLogTag")
    @Override
    public void onBackPressed() {
        if (App.getInstance().getFragmentDispatcher().isAllowBack()) {
            super.onBackPressed();
        } else {
            Log.d(App.TAG + "_MainActivity", "onBackPressed false");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SWMailerTest);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.getInstance().getFragmentDispatcher().setActivity(this);
        DrawerMenuManager<MainActivity> manager = new DrawerMenuManager<>(this); // Инициализация бокового меню

        if (savedInstanceState == null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new TabLayoutFragment()).commit();
            else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new TabLayoutFragmentLandscape()).commit();
            }
        }

        startAdderService();
        startSenderService();
    }

    private void startAdderService() {
        if (isMyServiceRunning(EvoReceiptAdderService.class)) {
            Toast.makeText(getApplicationContext(), "Service adder already running", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent startIntent = new Intent(this, EvoReceiptAdderService.class);
        startIntent.setAction("start");
        startService(startIntent);
    }

    private void startSenderService(){
        if (isMyServiceRunning(SendToBackendService.class)) {
            Toast.makeText(getApplicationContext(), "Service sender already running", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent startIntent = new Intent(this, SendToBackendService.class);
        startIntent.setAction("start");
        startService(startIntent);
    }

    @Override
    protected void onDestroy() {
        App.getInstance().getFragmentDispatcher().setActivity(null);
        SessionPresenter.getInstance().setiMainView1(null);
        SessionPresenter.getInstance().setDrawerMenuManager(null);
        super.onDestroy();
    }
}