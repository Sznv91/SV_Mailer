package ru.softvillage.mailer_test.dataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import org.joda.time.LocalDateTime;

import java.util.List;

import ru.softvillage.mailer_test.dataBase.converters.DateTimeConverter;
import ru.softvillage.mailer_test.dataBase.entity.AbstractEvoReceipt;
import ru.softvillage.mailer_test.dataBase.entity.Email;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;
import ru.softvillage.mailer_test.dataBase.entity.PhoneNumber;

@Dao
public interface ReceiptDao {

    @Query("SELECT * FROM evo_receipt_all ORDER BY date_time DESC")
    LiveData<List<EvoReceipt>> getAllEvoReceiptLiveData();

    @Query("SELECT evo_uuid FROM evo_receipt_all ORDER BY date_time DESC")
    List<String> getAllEvoReceiptUuid();

    @Query("SELECT date_time FROM evo_receipt_all")
    @TypeConverters(value = {DateTimeConverter.class})
    List<LocalDateTime> getAvailableDateTime();

    @Query("SELECT * FROM evo_receipt_all WHERE evo_uuid =:evoUuid")
    EvoReceipt getByUuid(String evoUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addEvoReceipt(EvoReceipt receipt);

    @Update(entity = EvoReceipt.class)
    void updateEvoReceipt(AbstractEvoReceipt receipt);

    @Query("SELECT * FROM user_phone_number WHERE number LIKE :partialNumber ORDER BY count_send DESC")
    List<PhoneNumber> getPhoneNumberList(String partialNumber);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createPhoneNumber(PhoneNumber entity);

    @Query("SELECT * FROM email WHERE phone_number =:phoneNumber ORDER BY send_count DESC")
    List<Email> getEmailListByPhone(long phoneNumber);

    @Query("SELECT * FROM email WHERE email_address LIKE :partialMail ORDER BY send_count DESC")
    List<Email> getEmailListByPartialMail(String partialMail);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createEmail(Email entity);
}
