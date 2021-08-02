package ru.softvillage.mailer_main.dataBase.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity
public class PartialEvoReceiptSvDbUpdate extends AbstractEvoReceipt {
    @ColumnInfo(name = "soft_village_processed", defaultValue = "false")
    private boolean soft_village_processed;
    @ColumnInfo(name = "sv_sent_sms", defaultValue = "false")
    private boolean sv_sent_sms;
    @ColumnInfo(name = "sv_sent_email", defaultValue = "false")
    private boolean sv_sent_email;

    public PartialEvoReceiptSvDbUpdate(String evo_uuid) {
        super(evo_uuid);
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
