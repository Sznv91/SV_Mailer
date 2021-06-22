package ru.softvillage.mailer_test.presetner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import ru.softvillage.mailer_test.ui.IMainView;
import ru.softvillage.mailer_test.ui.left_menu.DrawerMenuManager;
import ru.softvillage.mailer_test.util.Prefs;

public class SessionPresenter {
    private static SessionPresenter instance;
    private IMainView iMainView; // исп для динамического обновления темы в левом меню.
    private DrawerMenuManager drawerMenuManager;

    /**
     * Блок определения констант
     */
    private static final String CURRENT_THEME = "current_theme";
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;

    /**
     * Блок определения переменных
     */
    private int currentTheme;
    private MutableLiveData<Integer> currentThemeLiveData;

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
        if (tCurrentTheme < 0){
            tCurrentTheme = THEME_LIGHT;
        }
        currentThemeLiveData = new MutableLiveData<>(tCurrentTheme);
        setCurrentTheme(tCurrentTheme);
    }

    public void setiMainView1(IMainView iMainView) {
        this.iMainView = iMainView;
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
}
