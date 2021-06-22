package ru.softvillage.mailer_test.dataBase.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.joda.time.LocalDateTime;

import java.math.BigDecimal;

import lombok.Data;
import ru.softvillage.mailer_test.dataBase.converters.BigDecimalConverter;
import ru.softvillage.mailer_test.dataBase.converters.DateTimeConverter;

@Entity(tableName = "evo_receipt_all")
@Data
@TypeConverters({DateTimeConverter.class, BigDecimalConverter.class})
public class EvoReceipt {
    /**
     * Данные получаемые из курсора при выборке всех чеков
     */
    @PrimaryKey
    private String evo_uuid; //UUID чека хранящийся в БД эвотор
    @ColumnInfo(name = "evo_receipt_number")
    private long evo_receipt_number; // Номер чека печатаемый в верхней части документа
    @ColumnInfo(name = "evo_type")
    private String evo_type; // Тип SELL / BUY / PAYBACK / BUYBACK
    @ColumnInfo(name = "date_time")
    private LocalDateTime date_time; // преобразованный тип данных

    /**
     * Для получения этих данных необходимо используя ReceiptApi открыть чек и сгрузить данные
     */
    @ColumnInfo(name = "countOfPosition")
    private int countOfPosition; // количество позиций
    @ColumnInfo(name = "price")
    private BigDecimal price; // сумма чека


}
