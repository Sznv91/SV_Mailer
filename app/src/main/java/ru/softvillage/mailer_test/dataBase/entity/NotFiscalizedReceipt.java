package ru.softvillage.mailer_test.dataBase.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "not_fiscalized_receipt",
        indices = {@Index("evo_uuid")})
public class NotFiscalizedReceipt extends AbstractEvoReceipt {

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "email")
    private String email;

    public NotFiscalizedReceipt(@NonNull @NotNull String evo_uuid) {
        super(evo_uuid);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
