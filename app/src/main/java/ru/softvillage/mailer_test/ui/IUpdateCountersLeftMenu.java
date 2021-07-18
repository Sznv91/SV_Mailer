package ru.softvillage.mailer_test.ui;

public interface IUpdateCountersLeftMenu {

    void updateValue_receipts_count(int allReceiptCount);

    void updateValue_send_sms(int countSendSms);

    void updateValue_send_email(int countSendEmail);
}
