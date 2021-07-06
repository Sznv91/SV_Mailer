package ru.softvillage.mailer_test.dataBase;

import androidx.lifecycle.LiveData;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;
import ru.softvillage.mailer_test.dataBase.entity.PhoneNumber;

public class DbHelper {
    List<LocalDate> localDateList = new ArrayList<>();

    @Getter
    private final LocalDataBase dataBase;

    public DbHelper(LocalDataBase dataBase) {
        this.dataBase = dataBase;
    }

    public LiveData<List<EvoReceipt>> getAllEvoReceiptLiveData() {
        return dataBase.receiptDao().getAllEvoReceiptLiveData();
    }

    public List<LocalDate> getUniqueDate() {
        LocalDataBase.databaseWriteExecutor.execute(() -> {
            List<LocalDateTime> dateTimeList = dataBase.receiptDao().getAvailableDateTime();
            for (LocalDateTime dateTime : dateTimeList) {
                LocalDate tempLocalDate = dateTime.toLocalDate();
                if (!localDateList.contains(tempLocalDate)) {
                    localDateList.add(tempLocalDate);
                }
            }
        });
        return localDateList;
    }

    public List<PhoneNumber> getPhoneNumberList(String partialNumber) {
        String arg = partialNumber + "%";
        return dataBase.receiptDao().getPhoneNumberList(arg);
    }

    public void createOrReplacePhone(PhoneNumber phoneNumber) {
        phoneNumber.setCountSend(phoneNumber.getCountSend() + 1);
        LocalDataBase.databaseWriteExecutor.execute(() -> {
            dataBase.receiptDao().createPhoneNumber(phoneNumber);
        });
    }
}
