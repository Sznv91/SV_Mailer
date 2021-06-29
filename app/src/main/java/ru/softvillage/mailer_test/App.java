package ru.softvillage.mailer_test;

import android.app.Application;

import lombok.Getter;
import ru.softvillage.mailer_test.dataBase.DbHelper;
import ru.softvillage.mailer_test.dataBase.LocalDataBase;

/**
 * Retrofit sync and async request
 * https://futurestud.io/tutorials/retrofit-synchronous-and-asynchronous-requests
 */
public class App extends Application {
    public static final String TAG = "ru.evotor.ru.softvillage.mailer_test";

    @Getter
    private static App instance;
    @Getter
    private DbHelper dbHelper;
    @Getter
    private FragmentDispatcher fragmentDispatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDbHelper();
        initFragmentDispatcher();
    }

    private void initDbHelper() {
        LocalDataBase db = LocalDataBase.getDataBase(this);
        dbHelper = new DbHelper(db);
    }

    private void initFragmentDispatcher() {
        fragmentDispatcher = new FragmentDispatcher();
    }
}
