package ru.softvillage.mailer_test.network.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ReceivedEntity {

    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("evo_uuid")
    @Expose
    private String evo_uuid;
}
