package ru.softvillage.mailer_test.dataBase.entity;

import androidx.annotation.NonNull;
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
    @NonNull
    private String evo_uuid; //UUID чека хранящийся в БД эвотор
    @ColumnInfo(name = "evo_receipt_number")
    private String evo_receipt_number; // Номер чека печатаемый в верхней части документа
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

    public EvoReceipt(@NonNull String evo_uuid) {
        this.evo_uuid = evo_uuid;
    }

    public EvoReceipt() {
    }

    public String getEvo_uuid() {
        return evo_uuid;
    }

    public void setEvo_uuid(String evo_uuid) {
        this.evo_uuid = evo_uuid;
    }

    public String getEvo_receipt_number() {
        return evo_receipt_number;
    }

    public void setEvo_receipt_number(String evo_receipt_number) {
        this.evo_receipt_number = evo_receipt_number;
    }

    public String getEvo_type() {
        return evo_type;
    }

    public void setEvo_type(String evo_type) {
        this.evo_type = evo_type;
    }

    public LocalDateTime getDate_time() {
        return date_time;
    }

    public void setDate_time(LocalDateTime date_time) {
        this.date_time = date_time;
    }

    public int getCountOfPosition() {
        return countOfPosition;
    }

    public void setCountOfPosition(int countOfPosition) {
        this.countOfPosition = countOfPosition;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
