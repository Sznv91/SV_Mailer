package ru.softvillage.mailer_main;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.softvillage.mailer_main.dataBase.DbHelper;
import ru.softvillage.mailer_main.dataBase.LocalDataBase;
import ru.softvillage.mailer_main.network.BackendInterface;

/**
 * Retrofit sync and async request
 * https://futurestud.io/tutorials/retrofit-synchronous-and-asynchronous-requests
 */
public class App extends Application {
    public static final String TAG = "ru.evotor." + BuildConfig.APPLICATION_ID;

    @Getter
    private static App instance;
    @Getter
    private DbHelper dbHelper;
    @Getter
    private FragmentDispatcher fragmentDispatcher;
    @Getter
    private BackendInterface backendInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initRetrofit();
        initDbHelper();
        initFragmentDispatcher();
    }

    private void initDbHelper() {
        LocalDataBase db = LocalDataBase.getDataBase(this);
        dbHelper = new DbHelper(db);
    }

    private void initFragmentDispatcher() {
        fragmentDispatcher = new FragmentDispatcher();
    }

    private void initRetrofit() {
        @SuppressLint("LongLogTag") HttpLoggingInterceptor logging = new HttpLoggingInterceptor(s -> Log.d(TAG + "_Network", s));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();


        backendInterface = new Retrofit.Builder()
                .baseUrl("https://ofd.soft-village.ru/")
                .client(getUnsafeOkHttpClient().addInterceptor(logging).hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                }).build())
//                .client(okHttpClient) // Необязательно
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build()
                .create(BackendInterface.class);
    }

    private static OkHttpClient.Builder getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) instance.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

