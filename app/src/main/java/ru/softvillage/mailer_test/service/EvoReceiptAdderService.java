package ru.softvillage.mailer_test.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import lombok.Builder;
import lombok.Data;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.query.Cursor;
import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.dataBase.entity.Email;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;
import ru.softvillage.mailer_test.dataBase.entity.PhoneNumber;
import ru.softvillage.mailer_test.ui.MainActivity;

/**
 * https://www.tutlane.com/tutorial/android/android-progress-notification-with-examples
 * Добовление прогресс бара
 */
public class EvoReceiptAdderService extends Service {
    private Thread syncThread;
    private NotificationManager notificationManager;
    public static final int DEFAULT_NOTIFICATION_ID = 101;
    private static final String INFO = "Выполняется синхронизация чеков";
    private static final String TITLE = "Мэйлер";

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
        sendNotification(INFO, TITLE, INFO);
//        return super.onStartCommand(intent, flags, startId);
        Log.d(App.TAG + "_AddService", "Служба запущенна");
        if (syncThread == null) {
            syncThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    /**
                     * Популяция
                     */
                    /*int count = 5000;
                    List<Long> writedValue = new ArrayList<>();
                    String numberPrefix = "928";
                    while (count > 0) {
                        long phoneGeneratedValue = ThreadLocalRandom.current().nextLong(Long.parseLong("1111111"), Long.parseLong("9999999"));
                        String phoneGeneratedString = Long.toString(phoneGeneratedValue);
                        String phoneResultString = numberPrefix + phoneGeneratedString;
                        Long phoneResultLong = Long.parseLong(phoneResultString);
                        if (!writedValue.contains(phoneResultLong)) {
                            writedValue.add(phoneResultLong);
                            count--;
                        }
                    }

                    for (Long phoneNumberLong : writedValue) {
                        App.getInstance().getDbHelper().getDataBase().receiptDao().createPhoneNumber(new PhoneNumber(phoneNumberLong));
                    }


                    String[] litterArray = new String[]{"q", "w", "e", "r", "t", "y", "u", "i", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
                    List<String> wordSet = new ArrayList<>();
                    int wordCount = 5000;
                    while (wordCount >= 0){
                        int wordLength = ThreadLocalRandom.current().nextInt(3, 13);
                        String word = "";
                        for (int i = 0; i <= wordLength; i++) {
                            int randomLitter = ThreadLocalRandom.current().nextInt(0, litterArray.length);
                            word += litterArray[randomLitter];
                        }
                        if (!wordSet.contains(word)){
                            wordSet.add(word);
                            wordCount--;
                        }
                    }

                    for (int i = 0; i < wordSet.size()-1; i ++){
                        Email email = new Email();
                        email.setLinkedPhoneNumber(writedValue.get(i));
                        email.setEmailAddress(wordSet.get(i));
                        App.getInstance().getDbHelper().getDataBase().receiptDao().createEmail(email);
                    }*/



                    Cursor<Receipt.Header> cursor = ReceiptApi.getReceiptHeaders(getApplicationContext(), Receipt.Type.SELL);

                    List<EvoReceiptTemp> tempList = new ArrayList<>();
                    while (cursor != null && cursor.moveToNext()) {
                        tempList.add(EvoReceiptTemp.builder()
                                .uuid(cursor.getValue().getUuid())
                                .number(cursor.getValue().getNumber())
                                .type(cursor.getValue().getType().toString())
                                .date(cursor.getValue().getDate())
                                .build());
                    }
                    if (cursor != null) cursor.close();
                    Collections.reverse(tempList);

                    List<String> existInDbUuidList = App.getInstance().getDbHelper().getDataBase().receiptDao().getAllEvoReceiptUuid();
                    for (EvoReceiptTemp tempReceipt : tempList) {
                        if (!existInDbUuidList.contains(tempReceipt.getUuid())) {
                            EvoReceipt receipt = new EvoReceipt(tempReceipt.getUuid());
                            receipt.setEvo_receipt_number(tempReceipt.getNumber());
                            receipt.setEvo_type(tempReceipt.getType());
                            receipt.setDate_time(LocalDateTime.fromDateFields(tempReceipt.getDate()));

                            Receipt extractedFromEvoApiReceipt = ReceiptApi.getReceipt(getApplicationContext(), receipt.getEvo_uuid());
                            receipt.setCountOfPosition(extractedFromEvoApiReceipt.getPositions().size());
                            receipt.setPrice(extractedFromEvoApiReceipt.getPayments().get(0).getValue());
                            Log.d(App.TAG + "_AdderService -> Receipt.Header", "Выполняем запись в БД: " + receipt.toString());

                            App.getInstance().getDbHelper().getDataBase().receiptDao().addEvoReceipt(receipt);
                        } else {
                            existInDbUuidList.remove(tempReceipt.getUuid());
                        }
                    }
                    stopSelf();
                }
            });
            syncThread.start();
        }

        return Service.START_STICKY;
    }

    public void sendNotification(String Ticker, String Title, String Text) {

        //These three lines makes Notification to open main activity after clicking on it
        Intent notificationIntent = new Intent(this, MainActivity.class);
        /*notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);*/

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        Notification notification;
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setContentIntent(contentIntent)
                    .setOngoing(true)   //Can't be swiped out
                    .setSmallIcon(R.drawable.ic_tab_receipt_send)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sv_big_logo))   // большая картинка
                    .setTicker(Ticker)
                    .setContentTitle(Title) //Заголовок
                    .setContentText(Text) // Текст уведомления
                    .setWhen(System.currentTimeMillis())
                    .setProgress(100, 50, true);
            notification = builder.getNotification(); // API-15 and lower
        } else {
            String CHANNEL_ID = "soft_village_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "SoftVillageChanel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("SV mailer notify channel");
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setContentIntent(contentIntent)
                    .setContentTitle(TITLE)
                    .setTicker(INFO)
                    .setContentText(INFO)
                    .setSmallIcon(R.drawable.ic_tab_receipt_send)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sv_big_logo))
                    .setOngoing(true)
                    .setProgress(100, 50, true)
                    .build();
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

    @Data
    @Builder
    private static class EvoReceiptTemp {
        String uuid;
        String number;
        String type;
        Date date;
    }
}