package ru.softvillage.mailer_test.dataBase;

import androidx.lifecycle.LiveData;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collection;
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

    public LiveData<List<EvoReceipt>> getAllEvoReceiptSendLiveData() {
        return dataBase.receiptDao().getAllEvoReceiptSendLiveData();
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
        return listLimiter(dataBase.receiptDao().getPhoneNumberList(arg));
    }

    public void createOrReplacePhone(PhoneNumber phoneNumber) {
        phoneNumber.setCountSend(phoneNumber.getCountSend() + 1);
        LocalDataBase.databaseWriteExecutor.execute(() -> {
            dataBase.receiptDao().createPhoneNumber(phoneNumber);
        });
    }

    public List<Email> getEmailListByPhoneNumber(long phoneNumber) {
        return listLimiter(dataBase.receiptDao().getEmailListByPhone(phoneNumber));
    }

    public List<Email> getEmailListByPartialMail(String partialMail) {
        String arg = partialMail + "%";
        List<Email> founded = dataBase.receiptDao().getEmailListByPartialMail(arg);
        return listLimiter(founded);
    }

    private <T> List<T> listLimiter(Collection<T> founded) {
        List<T> result = new ArrayList<>();
        int LIMIT_LIST = founded.size();
        if (founded.size() >= 5) {
            LIMIT_LIST = 5;
        }
        int count = 0;
        for (T element : founded) {
            if (count < LIMIT_LIST) {
                result.add(element);
                count++;
            }
        }
        return result;
    }

    public void createOrUpdateEmail(Email email) {
        email.setSendCount(email.getSendCount() + 1);
        LocalDataBase.databaseWriteExecutor.execute(() -> {
            dataBase.receiptDao().createEmail(email);
        });
    }
}
