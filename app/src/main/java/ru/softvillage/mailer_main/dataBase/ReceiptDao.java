package ru.softvillage.mailer_main.dataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import org.joda.time.LocalDateTime;

import java.util.List;

import ru.softvillage.mailer_main.dataBase.converters.DateTimeConverter;
import ru.softvillage.mailer_main.dataBase.entity.Email;
import ru.softvillage.mailer_main.dataBase.entity.EvoReceipt;
import ru.softvillage.mailer_main.dataBase.entity.NotFiscalizedReceipt;
import ru.softvillage.mailer_main.dataBase.entity.PartialEvoReceiptSvDbUpdate;
import ru.softvillage.mailer_main.dataBase.entity.PhoneNumber;
import ru.softvillage.mailer_main.network.entity.SentEntity;

@Dao
public interface ReceiptDao {

    @Query("SELECT * FROM evo_receipt_all ORDER BY date_time DESC")
    LiveData<List<EvoReceipt>> getAllEvoReceiptLiveData();

    @Query("SELECT * FROM evo_receipt_all WHERE soft_village_processed = '1' ORDER BY date_time DESC")
    LiveData<List<EvoReceipt>> getAllEvoReceiptSendLiveData();

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
    void updateEvoReceipt(PartialEvoReceiptSvDbUpdate receipt); //Abstract

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

    @Query("SELECT * FROM email WHERE id =:id")
    Email getEmailById(long id);

    @Query("DELETE FROM user_phone_number WHERE number =:phoneNumber")
    void deletePhoneNumber(Long phoneNumber);

    @Query("DELETE FROM email WHERE phone_number =:phoneNumber")
    void deleteEmailAssociatedWithPhone(Long phoneNumber);

    @Query("DELETE FROM email WHERE email_address =:email")
    void deleteEmail(String email);

    @Insert
    void addToQueueToSend(SentEntity entity);

    @Query("SELECT * FROM queue_for_sending ORDER BY evo_uuid")
    List<SentEntity> getQueueSend();

    @Query("DELETE FROM queue_for_sending WHERE evo_uuid = :evoReceiptUuid")
    void removeFromQueueSend(String evoReceiptUuid);

    @Insert
    void addNotFiscalizedReceipt(NotFiscalizedReceipt receipt);

    @Query("SELECT * FROM not_fiscalized_receipt WHERE evo_uuid =:evoUuid")
    NotFiscalizedReceipt getNotFiscalizedReceiptById(String evoUuid);

    @Query("DELETE FROM not_fiscalized_receipt WHERE evo_uuid = :evoUuid")
    void removeNotFiscalizedReceiptById(String evoUuid);

    @Query("DELETE FROM not_fiscalized_receipt")
    void removeAllNotFiscalizedReceipt();
}
