package ru.softvillage.mailer_test.dataBase;

import androidx.lifecycle.LiveData;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import ru.softvillage.mailer_test.dataBase.entity.Email;
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
        List<PhoneNumber> founded = dataBase.receiptDao().getPhoneNumberList(arg);
        List<PhoneNumber> result = new ArrayList<>();
        int LIMIT_LIST = founded.size();
        if (founded.size() >= 5) {
            LIMIT_LIST = 5;
        }
        for (int i = 0; i < LIMIT_LIST; i++) {
            result.add(founded.get(i));
        }
        return result;
    }

    public void createOrReplacePhone(PhoneNumber phoneNumber) {
        phoneNumber.setCountSend(phoneNumber.getCountSend() + 1);
        LocalDataBase.databaseWriteExecutor.execute(() -> {
            dataBase.receiptDao().createPhoneNumber(phoneNumber);
        });
    }

    public List<Email> getEmailListByPhoneNumber(long phoneNumber) {
        return dataBase.receiptDao().getEmailListByPhone(phoneNumber);
    }

    public void createOrUpdateEmail(Email email) {
        email.setSendCount(email.getSendCount() + 1);
        LocalDataBase.databaseWriteExecutor.execute(() -> {
            dataBase.receiptDao().createEmail(email);
        });
    }
}
