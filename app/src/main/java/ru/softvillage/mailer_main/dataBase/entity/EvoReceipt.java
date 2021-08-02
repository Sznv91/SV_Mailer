package ru.softvillage.mailer_main.dataBase.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.TypeConverters;

import org.joda.time.LocalDateTime;

import java.math.BigDecimal;

import ru.softvillage.mailer_main.dataBase.converters.BigDecimalConverter;
import ru.softvillage.mailer_main.dataBase.converters.DateTimeConverter;

@Entity(tableName = "evo_receipt_all", indices = {
        @Index("evo_uuid"),
        @Index("soft_village_processed")
})
@TypeConverters({DateTimeConverter.class, BigDecimalConverter.class})
public class EvoReceipt extends AbstractEvoReceipt {
    @Ignore
    public static final String TYPE_SELL = "SELL";
    @Ignore
    public static final String TYPE_PAYBACK = "PAYBACK";
    /**
     * Данные получаемые из курсора при выборке всех чеков
     */
    @ColumnInfo(name = "evo_receipt_number")
    private String evo_receipt_number; // Номер чека печатаемый в верхней части документа
    @ColumnInfo(name = "evo_type")
    private String evo_type; // Тип SELL / BUY / PAYBACK / BUYBACK
    @ColumnInfo(name = "date_time")
    private LocalDateTime date_time; // преобразованный тип данных

    /**
     * Для получения этих данных необходимо используя ReceiptApi открыть чек и сгрузить данные
     */
    @ColumnInfo(name = "count_of_position")
    private int countOfPosition; // количество позиций
    @ColumnInfo(name = "price")
    private BigDecimal price; // сумма чека

    /**
     * Данные для инденификации типа записи (необходимость отображать на второй кладке)
     */

    @ColumnInfo(name = "soft_village_processed", defaultValue = "false")
    private boolean soft_village_processed;
    @ColumnInfo(name = "sv_sent_sms", defaultValue = "false")
    private boolean sv_sent_sms;
    @ColumnInfo(name = "sv_sent_email", defaultValue = "false")
    private boolean sv_sent_email;

    public EvoReceipt() {
    }

    public EvoReceipt(String evo_uuid) {
        super(evo_uuid);
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

    public boolean isSoft_village_processed() {
        return soft_village_processed;
    }

    public void setSoft_village_processed(boolean soft_village_processed) {
        this.soft_village_processed = soft_village_processed;
    }

    public boolean isSv_sent_sms() {
        return sv_sent_sms;
    }

    public void setSv_sent_sms(boolean sv_sent_sms) {
        this.sv_sent_sms = sv_sent_sms;
    }

    public boolean isSv_sent_email() {
        return sv_sent_email;
    }

    public void setSv_sent_email(boolean sv_sent_email) {
        this.sv_sent_email = sv_sent_email;
    }
}
