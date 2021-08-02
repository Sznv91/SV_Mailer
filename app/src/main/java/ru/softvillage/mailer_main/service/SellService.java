package ru.softvillage.mailer_main.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEvent;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEventProcessor;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.softvillage.mailer_main.App;
import ru.softvillage.mailer_main.ui.SellActivity;

public class SellService extends IntegrationService {
    public final static String RECEIPT_UUID = "receipt_uuid";


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(ReceiptDiscountEvent.NAME_SELL_RECEIPT, new ReceiptDiscountEventProcessor() {
            @SuppressLint("LongLogTag")
            @Override
            public void call(@NonNull String action, @NonNull ReceiptDiscountEvent event, @NonNull Callback callback) {
                Log.d(App.TAG + "_SellService",
                        "action: " + action + "; event.toString: " + event.toString());
                try {
                    // запускаем активити с детальной инф. о чеке и ввода пользовательских данных для отправки чека
                    Intent intent = new Intent(SellService.this, SellActivity.class);
                    intent.putExtra(RECEIPT_UUID, event.getReceiptUuid());

                    callback.startActivity(intent);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.d(App.TAG + "_SellService", "exception on start activity: " + e.toString());
                }
            }
        });
        return map;
    }
}