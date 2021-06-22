package ru.softvillage.mailer_test.dataBase.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.math.BigDecimal;

import lombok.Data;
import ru.softvillage.mailer_test.dataBase.converters.BigDecimalConverter;

/**
 * Класс предназначен для обновления данных в нашей БД после считывания сведений о количестве
 * позиций и сумме чека
 */
@Entity
@Data
@TypeConverters({BigDecimalConverter.class})
public class PartialEvoReceipt {
    @PrimaryKey
    private String evo_uuid; //UUID чека хранящийся в БД эвотор
    @ColumnInfo(name = "countOfPosition")
    private int countOfPosition; // количество позиций
    @ColumnInfo(name = "price")
    private BigDecimal price; // сумма чека
}
