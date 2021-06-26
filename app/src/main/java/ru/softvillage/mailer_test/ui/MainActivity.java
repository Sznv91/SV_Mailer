package ru.softvillage.mailer_test.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.query.Cursor;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.presetner.SessionPresenter;
import ru.softvillage.mailer_test.service.EvoReceiptAdderService;
import ru.softvillage.mailer_test.ui.left_menu.DrawerMenuManager;
import ru.softvillage.mailer_test.ui.tabs.TabLayoutFragment;

/**
 * SplashScreen выполнен по статье
 * https://habr.com/ru/post/345380/
 * <p>
 * Tabs:
 * https://abhiandroid.com/materialdesign/tablayout-example-android-studio.html
 * --> Example 2 of TabLayout Using ViewPager
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SWMailerTest);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerMenuManager<MainActivity> manager = new DrawerMenuManager<>(this); // Инициализация бокового меню

        if (savedInstanceState == null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new TabLayoutFragment()).commit();
            else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new TabLayoutFragment()).commit(); // заглушка переделать
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new LandscapeTabLayoutFragment()).commit();
            }
        }

        //////////////////////////////////////////////////////////////////////////////////
        /*Log.d("ru.softvillage.mailer_test" + "_Receipt.Header", "Start activity");

        Cursor<Receipt.Header> cursor = ReceiptApi.getReceiptHeaders(getApplicationContext(), Receipt.Type.SELL);
        Log.d("ru.softvillage.mailer_test" + "_Receipt.Header", "Cursor count (size): " + cursor.getCount());

        while (cursor != null && cursor.moveToNext()) {
            Log.d("ru.softvillage.mailer_test" + "_Receipt.Header", "cursor.getValue().toString(): " + cursor.getValue().toString());
        }*/
        startAdderService();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void startAdderService() {
        if (isMyServiceRunning(EvoReceiptAdderService.class)) {
            Toast.makeText(getApplicationContext(), "Service already running", Toast.LENGTH_LONG).show();
            return;
        }
        Intent startIntent = new Intent(this, EvoReceiptAdderService.class);
        startIntent.setAction("start");
        startService(startIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    //////////////////////////

    @Override
    protected void onDestroy() {
        SessionPresenter.getInstance().setiMainView1(null);
        super.onDestroy();
    }
}