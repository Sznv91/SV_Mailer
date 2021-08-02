package ru.softvillage.mailer_main.dataBase.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import lombok.Data;

@Entity(tableName = "user_phone_number")
@Data
public class PhoneNumber {
    @PrimaryKey
    @NotNull
    private final Long number;

    @ColumnInfo(name = "count_send", defaultValue = "0")
    private int countSend;

    public PhoneNumber(@NotNull Long number) {
        this.number = number;
    }

    @NotNull
    public Long getNumber() {
        return number;
    }

    public int getCountSend() {
        return countSend;
    }

    public void setCountSend(int countSend) {
        this.countSend = countSend;
    }
}
