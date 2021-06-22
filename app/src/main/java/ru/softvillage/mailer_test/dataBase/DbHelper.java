package ru.softvillage.mailer_test.dataBase;

import androidx.lifecycle.LiveData;

import lombok.Getter;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;

public class DbHelper {

    @Getter
    private final LocalDataBase dataBase;

    public DbHelper(LocalDataBase dataBase) {
        this.dataBase = dataBase;
    }

    public LiveData<EvoReceipt> getAllEvoReceiptLiveData() {
        return dataBase.receiptDao().getAllEvoReceiptLiveData();
    }
}
