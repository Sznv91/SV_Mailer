package ru.softvillage.mailer_test.dataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;
import ru.softvillage.mailer_test.dataBase.entity.PartialEvoReceiptEvoDbUpdate;
import ru.softvillage.mailer_test.dataBase.entity.PartialEvoReceiptSvDbUpdate;
import ru.softvillage.mailer_test.dataBase.entity.PhoneNumber;

@Database(entities = {EvoReceipt.class, PartialEvoReceiptEvoDbUpdate.class, PartialEvoReceiptSvDbUpdate.class, PhoneNumber.class}, version = 1, exportSchema = false)
public abstract class LocalDataBase extends RoomDatabase {

    public abstract ReceiptDao receiptDao();

    private static volatile LocalDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static LocalDataBase getDataBase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocalDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            LocalDataBase.class,
                            "soft_village_data_base")
                            /*.addMigrations(MIGRATION_1_2, MIGRATION_2_3)*/
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
