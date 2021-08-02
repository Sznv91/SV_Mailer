package ru.softvillage.mailer_main.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import ru.softvillage.mailer_main.App;
import ru.softvillage.mailer_main.R;
import ru.softvillage.mailer_main.network.entity.ReceivedEntity;
import ru.softvillage.mailer_main.network.entity.SentEntity;
import ru.softvillage.mailer_main.presetner.SessionPresenter;
import ru.softvillage.mailer_main.ui.MainActivity;

public class SendToBackendService extends Service {
    private Thread sendThread;
    private NotificationManager notificationManager;
    public static final int DEFAULT_NOTIFICATION_ID = 102;
    private static final String INFO = "Выполняется отравка чеков";
    private static final String TITLE = "Мэйлер";

    public SendToBackendService() {
    }

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
        Log.d(App.TAG + "_SendService", "Служба отправки запущенна");
        if (sendThread == null) {
            sendThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    List<SentEntity> list = App.getInstance().getDbHelper().getDataBase().receiptDao().getQueueSend();
                    while (!list.isEmpty()) {
                        try {
                            Response<ReceivedEntity> receivedEntity = App.getInstance().getBackendInterface().sendReceipt(list.get(0)).execute();
                            if (receivedEntity.isSuccessful()
                                    && receivedEntity.body().isSuccess()) {
                                SentEntity entity = list.get(0);                        //Обновление
                                if (entity.getPhoneNumber() != null) {                  //счетчиков
                                    SessionPresenter.getInstance().setCountSendSms();   //на
                                }                                                       //UI
                                if (!TextUtils.isEmpty(entity.getEmail())) {            //
                                    SessionPresenter.getInstance().setCountSendEmail(); //
                                }                                                       //

                                App.getInstance().getDbHelper().getDataBase().receiptDao().removeFromQueueSend(receivedEntity.body().getEvo_uuid());
                                list.clear();
                                list.addAll(App.getInstance().getDbHelper().getDataBase().receiptDao().getQueueSend());
                            }
                        } catch (IOException e) {
                            Log.d(App.TAG + "_SendService_.sendReceipt.execute", e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                    stopSelf();
                }
            });
            sendThread.start();
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
        Log.d(App.TAG + "_SendService", "Служба отправки остановлена");
        super.onDestroy();
        notificationManager.cancel(DEFAULT_NOTIFICATION_ID);
    }
}