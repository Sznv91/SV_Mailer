package ru.softvillage.mailer_test.presetner;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.joda.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.network.entity.OrgInfo;
import ru.softvillage.mailer_test.ui.IMainView;
import ru.softvillage.mailer_test.ui.left_menu.DrawerMenuManager;
import ru.softvillage.mailer_test.util.Prefs;

import static ru.softvillage.mailer_test.App.TAG;

public class SessionPresenter {
    private static SessionPresenter instance;
    private IMainView iMainView; // исп для динамического обновления темы в левом меню.
    private DrawerMenuManager drawerMenuManager;

    /**
     * Блок определения констант
     */
    private static final String CURRENT_THEME = "current_theme";
    public static final String LAST_OPEN_RECEIPT_DETAIL_FRAGMENT = "lastOpenReceiptDetailFragment";
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public final static String SHOP_NAME = "shop_name";
    public final static String SHOP_ADDRESS = "shop_address";
    public final static String PAYMENT_PLACE_ADDRESS = "payment_place_address";
    public final static String SNO_TYPE = "sno_type";
    public final static String ORG_INN = "org_inn";
    public final static String NEED_SAVE_CONTACT = "need_save_contact";

    /**
     * Блок определения переменных
     */
    @Getter
    private String shop_name,
            address,
            payment_place,
            sno_type;
    @Getter
    private long org_inn;
    private LocalDateTime lastOpenReceiptDetailFragment;
    private int currentTheme;
    private MutableLiveData<Integer> currentThemeLiveData;
    @Getter
    @Setter
    boolean fragmentBusy = false; // false - свободен
    boolean saveContact;

    public static SessionPresenter getInstance() {
        if (instance == null) {
            instance = new SessionPresenter();
        }
        return instance;
    }

    private SessionPresenter() {
        initPresenter();
    }

    /**
     * Инициализация презентера
     */
    private void initPresenter() {
        int tCurrentTheme = Prefs.getInstance().loadInt(CURRENT_THEME);
        if (tCurrentTheme < 0) {
            tCurrentTheme = THEME_LIGHT;
        }
        currentThemeLiveData = new MutableLiveData<>(tCurrentTheme);
        setCurrentTheme(tCurrentTheme);

        String tLastOpenReceiptDetailFragment = Prefs.getInstance().loadString(LAST_OPEN_RECEIPT_DETAIL_FRAGMENT);
        if (TextUtils.isEmpty(tLastOpenReceiptDetailFragment)) {
            setLastOpenReceiptDetailFragment(LocalDateTime.now());
        } else {
            lastOpenReceiptDetailFragment = LocalDateTime.parse(tLastOpenReceiptDetailFragment);
        }

        shop_name = Prefs.getInstance().loadString(SHOP_NAME);
        address = Prefs.getInstance().loadString(SHOP_ADDRESS);
        payment_place = Prefs.getInstance().loadString(PAYMENT_PLACE_ADDRESS);
        sno_type = Prefs.getInstance().loadString(SNO_TYPE);
        org_inn = Prefs.getInstance().loadLong(ORG_INN);

        saveContact = Prefs.getInstance().loadBoolean(NEED_SAVE_CONTACT, true);

        initOrgInfo();
    }

    @SuppressLint("LongLogTag")
    private void initOrgInfo() {
        Log.d(TAG + "_org_info", "initOrgInfo");
        App.getInstance().getBackendInterface().getOrgInfo().enqueue(new Callback<OrgInfo>() {
            @Override
            public void onResponse(Call<OrgInfo> call, Response<OrgInfo> response) {
                OrgInfo info = response.body();
                if (info != null && !TextUtils.isEmpty(info.getName())) {
                    Log.d(TAG + "_org_info", info.toString());

                    setShopInfo(info.getName(),
                            info.getAddress(),
                            info.getPayment_place(),
                            info.getSno(),
                            info.getInn());
                }
            }

            @Override
            public void onFailure(Call<OrgInfo> call, Throwable t) {
                Log.d(TAG + "_org_info", t.getMessage());
                Handler retryTask = new Handler(Looper.getMainLooper());
                retryTask.postDelayed(() -> initOrgInfo(), 5000);
            }
        });
    }

    /**
     * Если не заданно "Место расчетов"(payment_place), то оно заполняется информаций о адрессе предприятния.
     *
     * @param shop_name     назвение предприятия (Не меняется пользователем)
     * @param address       адресс предприятния (Не меняется пользователем)
     * @param payment_place место расчетов
     * @param sno_type      СНО (УСН Доход - расход)
     * @param org_inn       ИНН предприятия (Указывается при регистрации)
     */
    public void setShopInfo(String shop_name, String address, String payment_place, String sno_type, long org_inn) {
        if (!this.shop_name.equals(shop_name)) {
            this.shop_name = shop_name;
            Prefs.getInstance().saveString(SHOP_NAME, shop_name);
        }
        if (!this.address.equals(address)) {
            this.address = address;
            Prefs.getInstance().saveString(SHOP_ADDRESS, address);
        }

        if (TextUtils.isEmpty(payment_place)) {
            this.payment_place = address;
            Prefs.getInstance().saveString(PAYMENT_PLACE_ADDRESS, this.payment_place);
        } else {
            if (!this.payment_place.equals(payment_place)) {
                this.payment_place = payment_place;
                Prefs.getInstance().saveString(PAYMENT_PLACE_ADDRESS, payment_place);
            }
        }

        if (!this.sno_type.equals(sno_type)) {
            this.sno_type = sno_type;
            Prefs.getInstance().saveString(SNO_TYPE, sno_type);
        }
        if (this.org_inn != org_inn) {
            this.org_inn = org_inn;
            Prefs.getInstance().saveLong(ORG_INN, org_inn);
        }
    }

    public void setiMainView1(IMainView iMainView) {
        this.iMainView = iMainView;
    }

    public DrawerMenuManager getDrawerManager() {
        return drawerMenuManager;
    }

    public void setDrawerMenuManager(DrawerMenuManager drawerMenuManager) {
        this.drawerMenuManager = drawerMenuManager;
    }

    public int getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(int currentTheme) {
        if (currentTheme < 0 || currentTheme > THEME_DARK) {
            currentTheme = THEME_LIGHT;
        }
        if (this.currentTheme != currentTheme) {
            Prefs.getInstance().saveInt(CURRENT_THEME, currentTheme);
            this.currentTheme = currentTheme;
            currentThemeLiveData.postValue(currentTheme);
            if (iMainView != null) iMainView.themeChange(currentTheme);
        }
    }

    public LiveData<Integer> getCurrentThemeLiveData() {
        return currentThemeLiveData;
    }

    public LocalDateTime getLastOpenReceiptDetailFragment() {
        return lastOpenReceiptDetailFragment;
    }

    public void setLastOpenReceiptDetailFragment(LocalDateTime lastOpenReceiptDetailFragment) {
        this.lastOpenReceiptDetailFragment = lastOpenReceiptDetailFragment;
        Prefs.getInstance().saveString(LAST_OPEN_RECEIPT_DETAIL_FRAGMENT, lastOpenReceiptDetailFragment.toString());
    }

    public boolean isSaveContact() {
        return saveContact;
    }

    public void setSaveContact(boolean saveContact) {
        if (this.saveContact != saveContact) {
            this.saveContact = saveContact;
            Prefs.getInstance().saveBoolean(NEED_SAVE_CONTACT, this.saveContact);
        }
    }
}
