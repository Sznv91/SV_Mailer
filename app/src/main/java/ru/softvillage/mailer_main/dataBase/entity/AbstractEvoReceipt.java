package ru.softvillage.mailer_main.dataBase.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity
public abstract class AbstractEvoReceipt {

    @PrimaryKey
    @NonNull
    private String evo_uuid; //UUID чека хранящийся в БД эвотор

    @NonNull
    public String getEvo_uuid() {
        return evo_uuid;
    }

    public void setEvo_uuid(@NonNull String evo_uuid) {
        this.evo_uuid = evo_uuid;
    }

    public AbstractEvoReceipt() {
    }

    public AbstractEvoReceipt(@NonNull String evo_uuid) {
        this.evo_uuid = evo_uuid;
    }
}
