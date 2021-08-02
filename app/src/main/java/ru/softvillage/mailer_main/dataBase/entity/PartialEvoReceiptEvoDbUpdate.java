package ru.softvillage.mailer_main.dataBase.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import java.math.BigDecimal;

import ru.softvillage.mailer_main.dataBase.converters.BigDecimalConverter;

/**
 * Класс предназначен для обновления данных в нашей БД после считывания сведений о количестве
 * позиций и сумме чека
 */
@Entity
@TypeConverters({BigDecimalConverter.class})
public class PartialEvoReceiptEvoDbUpdate extends AbstractEvoReceipt {
    @ColumnInfo(name = "count_of_position")
    private int countOfPosition; // количество позиций
    @ColumnInfo(name = "price")
    private BigDecimal price; // сумма чека

    public PartialEvoReceiptEvoDbUpdate(String evo_uuid) {
        super(evo_uuid);
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
