package ru.softvillage.mailer_test.network.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
@Entity(tableName = "queue_for_sending")
public class SentEntity {

    @SerializedName("evo_uuid")
    @Expose
    @PrimaryKey
    @ColumnInfo(name = "evo_uuid")
    @NonNull
    private String evoUuid;

    @SerializedName("phone_number")
    @Expose
    @ColumnInfo(name = "phone_number")
    private Long phoneNumber;

    @SerializedName("email")
    @Expose
    @ColumnInfo(name = "email")
    private String email;

    public SentEntity() {
        // need for roomDb
    }

    public String getEvoUuid() {
        return evoUuid;
    }

    public void setEvoUuid(String evoUuid) {
        this.evoUuid = evoUuid;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
