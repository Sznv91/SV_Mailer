package ru.softvillage.mailer_test.dataBase.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "email", indices = {
        @Index(value = {"phone_number"}),
        @Index(value = {"email_address"}, unique = true)})

public class Email {

    @NotNull
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "email_address")
    private String emailAddress;

    @ColumnInfo(name = "phone_number")
    private Long linkedPhoneNumber;

    @ColumnInfo(name = "send_count", defaultValue = "0")
    private int sendCount;

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Long getLinkedPhoneNumber() {
        return linkedPhoneNumber;
    }

    public void setLinkedPhoneNumber(Long linkedPhoneNumber) {
        this.linkedPhoneNumber = linkedPhoneNumber;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", emailAddress='" + emailAddress + '\'' +
                ", linkedPhoneNumber=" + linkedPhoneNumber +
                ", sendCount=" + sendCount +
                '}';
    }
}
