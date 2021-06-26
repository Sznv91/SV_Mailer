package ru.softvillage.mailer_test.dataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.softvillage.mailer_test.dataBase.entity.AbstractEvoReceipt;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;

@Dao
public interface ReceiptDao {

    @Query("SELECT * FROM evo_receipt_all ORDER BY date_time DESC")
    LiveData<List<EvoReceipt>> getAllEvoReceiptLiveData();

    @Query("SELECT evo_uuid FROM evo_receipt_all ORDER BY date_time DESC")
    List<String> getAllEvoReceiptUuid();

    @Query("SELECT * FROM evo_receipt_all WHERE evo_uuid =:evoUuid")
    EvoReceipt getByUuid(String evoUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addEvoReceipt(EvoReceipt receipt);

    @Update(entity = EvoReceipt.class)
    void updateEvoReceipt(AbstractEvoReceipt receipt);

}
