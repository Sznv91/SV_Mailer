package ru.softvillage.mailer_test.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.joda.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.List;

import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.query.Cursor;
import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;
import ru.softvillage.mailer_test.ui.MainActivity;

public class EvoReceiptAdderService extends Service {
    private Thread syncThread;
    private NotificationManager notificationManager;
    public static final int DEFAULT_NOTIFICATION_ID = 101;
    private static String INFO = "Выполняется добавление чеков в приложения";
    private static String TITLE = "Мэйлер";

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification("Ticker","Title","Text");
//        return super.onStartCommand(intent, flags, startId);
        Log.d(App.TAG + "_AddService", "Служба запущенна");
        if (syncThread == null) {
            syncThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Cursor<Receipt.Header> cursor = ReceiptApi.getReceiptHeaders(getApplicationContext(), Receipt.Type.SELL);
                    List<String> existInDbUuidList = App.getInstance().getDbHelper().getDataBase().receiptDao().getAllEvoReceiptUuid();
                    while (cursor != null && cursor.moveToNext()) {
                        if (!existInDbUuidList.contains(cursor.getValue().getUuid())) {
                            EvoReceipt receipt = new EvoReceipt(cursor.getValue().getUuid());
                            receipt.setEvo_receipt_number(cursor.getValue().getNumber());
                            receipt.setEvo_type(Receipt.Type.SELL.name());
                            receipt.setDate_time(LocalDateTime.fromDateFields(cursor.getValue().getDate()));

                            Receipt tempReceipt = ReceiptApi.getReceipt(getApplicationContext(), receipt.getEvo_uuid());
                            receipt.setCountOfPosition(tempReceipt.getPositions().size());
                            receipt.setPrice(tempReceipt.getPayments().get(0).getValue());
                            Log.d("ru.softvillage.mailer_test" + "_AdderService -> Receipt.Header", "Выполняем запись в БД: " + receipt.toString());

                            App.getInstance().getDbHelper().getDataBase().receiptDao().addEvoReceipt(receipt);
                        }
                    }
                    if (cursor != null) cursor.close();
                    stopSelf();
                }
            });
            syncThread.start();
        }

        return Service.START_STICKY;
    }

    public void sendNotification(String Ticker,String Title,String Text) {

        //These three lines makes Notification to open main activity after clicking on it
        Intent notificationIntent = new Intent(this, MainActivity.class);
        /*notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);*/

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        Notification notification;
        if (android.os.Build.VERSION.SDK_INT<=15) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(contentIntent)
                    .setOngoing(true)   //Can't be swiped out
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.large))   // большая картинка
                    .setTicker(Ticker)
                    .setContentTitle(Title) //Заголовок
                    .setContentText(Text) // Текст уведомления
                    .setWhen(System.currentTimeMillis());
            notification = builder.getNotification(); // API-15 and lower
        }else{
            String CHANNEL_ID = "alex_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "SoftVillageChanel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("SV channel description");
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setContentIntent(contentIntent)
                    .setContentTitle(TITLE)
                    .setTicker(INFO)
                    .setContentText(INFO)
                    .setSmallIcon(R.drawable.ic_tab_receipt_send)
                    .setOngoing(true).build();
            notification = builder.build();
        }

        startForeground(DEFAULT_NOTIFICATION_ID, notification);
    }


    @Override
    public void onDestroy() {
        Log.d(App.TAG + "_AddService", "Служба остановлена");
        super.onDestroy();
        notificationManager.cancel(DEFAULT_NOTIFICATION_ID);
    }
}