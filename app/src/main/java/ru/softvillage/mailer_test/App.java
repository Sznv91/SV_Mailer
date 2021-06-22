package ru.softvillage.mailer_test;

import android.app.Application;

import lombok.Getter;
import ru.softvillage.mailer_test.dataBase.DbHelper;
import ru.softvillage.mailer_test.dataBase.LocalDataBase;

public class App extends Application {
    public static final String TAG = "ru.softvillage.mailer_test";

    @Getter
    private static App instance;
    @Getter
    private DbHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDbHelper();
    }

    private void initDbHelper() {
        LocalDataBase db = LocalDataBase.getDataBase(this);
        dbHelper = new DbHelper(db);
    }
}
