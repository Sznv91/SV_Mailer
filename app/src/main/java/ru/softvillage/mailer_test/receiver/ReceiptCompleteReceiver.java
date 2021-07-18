package ru.softvillage.mailer_test.receiver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.event.ReceiptCompletedEvent;
import ru.evotor.framework.receipt.event.ReceiptDeletedEvent;
import ru.evotor.framework.receipt.position.event.PositionAddedEvent;
import ru.evotor.framework.receipt.position.event.PositionRemovedEvent;
import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;
import ru.softvillage.mailer_test.dataBase.entity.NotFiscalizedReceipt;
import ru.softvillage.mailer_test.network.entity.SentEntity;
import ru.softvillage.mailer_test.presetner.SessionPresenter;
import ru.softvillage.mailer_test.service.SendToBackendService;

/**
 * Вызывается после закрытия чека продажи.
 */
public class ReceiptCompleteReceiver extends ru.evotor.framework.receipt.event.handler.receiver.SellReceiptBroadcastReceiver {



    @SuppressLint("LongLogTag")
    @Override
    protected void handleReceiptDeletedEvent(@NotNull Context context, @NotNull ReceiptDeletedEvent event) {
        super.handleReceiptDeletedEvent(context, event);

        Log.d(App.TAG + "_ReceiptCompleteReceiver", "handleReceiptDeletedEvent event.getReceiptUuid(): " + event.getReceiptUuid());

        new Thread(() -> {
            App.getInstance().getDbHelper().getDataBase().receiptDao().removeAllNotFiscalizedReceipt();
            Log.d(App.TAG + "_ReceiptCompleteReceiver", "Удалили все entity из таб. not_fiscalized_receipt " + event.getReceiptUuid());
        }).start();
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void handleReceiptCompletedEvent(@NotNull Context context, @NotNull ReceiptCompletedEvent event) {
        super.handleReceiptCompletedEvent(context, event);

        Log.d(App.TAG + "_ReceiptCompleteReceiver", "handleReceiptCompletedEvent event.getReceiptUuid(): " + event.getReceiptUuid());

        new Thread(() -> {
            String receiveUuid = event.getReceiptUuid();
            NotFiscalizedReceipt notFiscalizedReceipt = App.getInstance().getDbHelper().getDataBase().receiptDao().getNotFiscalizedReceiptById(receiveUuid);
            if (notFiscalizedReceipt != null) {
                Receipt tempReceipt = ReceiptApi.getReceipt(context.getApplicationContext(), receiveUuid);

                EvoReceipt receipt = new EvoReceipt(tempReceipt.getHeader().getUuid());
                receipt.setEvo_receipt_number(tempReceipt.getHeader().getNumber());
                receipt.setEvo_type(tempReceipt.getHeader().getType().toString());
                receipt.setDate_time(LocalDateTime.fromDateFields(tempReceipt.getHeader().getDate()));
                receipt.setCountOfPosition(tempReceipt.getPositions().size());
                receipt.setPrice(tempReceipt.getPayments().get(0).getValue());

                receipt.setSoft_village_processed(true);
                receipt.setSv_sent_sms(notFiscalizedReceipt.getPhoneNumber() != null);
                receipt.setSv_sent_email(notFiscalizedReceipt.getEmail() != null);

                Log.d(App.TAG + "_ReceiptCompleteReceiver -> new Thread", "Выполняем запись в БД: " + receipt.toString());
                App.getInstance().getDbHelper().getDataBase().receiptDao().addEvoReceipt(receipt);
                SessionPresenter.getInstance().setCountAllReceipt();

                SentEntity sentEntity = new SentEntity();
                sentEntity.setEvoUuid(receiveUuid);
                if (!TextUtils.isEmpty(notFiscalizedReceipt.getPhoneNumber())) {
                    sentEntity.setPhoneNumber(Long.parseLong(notFiscalizedReceipt.getPhoneNumber()));
                }
                if (!TextUtils.isEmpty(notFiscalizedReceipt.getEmail())) {
                    sentEntity.setEmail(notFiscalizedReceipt.getEmail());
                }
                App.getInstance().getDbHelper().getDataBase().receiptDao().removeNotFiscalizedReceiptById(receiveUuid);
                App.getInstance().getDbHelper().getDataBase().receiptDao().addToQueueToSend(sentEntity);
                if (!App.isMyServiceRunning(SendToBackendService.class)) {
                    Intent startIntent = new Intent(App.getInstance().getApplicationContext(), SendToBackendService.class);
                    startIntent.setAction("start");
                    App.getInstance().startService(startIntent);
                }
            }
        }).start();
    }
}