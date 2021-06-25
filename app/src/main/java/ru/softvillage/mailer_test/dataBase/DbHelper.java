package ru.softvillage.mailer_test.dataBase;

import androidx.lifecycle.LiveData;

import java.util.List;

import lombok.Getter;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;

public class DbHelper {

    @Getter
    private final LocalDataBase dataBase;

    public DbHelper(LocalDataBase dataBase) {
        this.dataBase = dataBase;
    }

    public LiveData<List<EvoReceipt>> getAllEvoReceiptLiveData() {
        return dataBase.receiptDao().getAllEvoReceiptLiveData();
    }
}
