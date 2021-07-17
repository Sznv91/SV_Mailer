package ru.softvillage.mailer_test;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.softvillage.mailer_test.service.SellService;
import ru.softvillage.mailer_test.ui.MainActivity;
import ru.softvillage.mailer_test.ui.fragmet.ReceiptDetailFragment;
import ru.softvillage.mailer_test.ui.left_menu.DrawerMenuManager;

public class SellActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        Intent intent = getIntent();
        String receiptUUID = intent.getStringExtra(SellService.RECEIPT_UUID);

        App.getInstance().getFragmentDispatcher().setActivity(this);
        DrawerMenuManager<SellActivity> manager = new DrawerMenuManager<>(this); // Инициализация бокового меню ||SellSell MainActivity

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, ReceiptDetailFragment.newInstance(receiptUUID)).commit(); // Вариант для вер. и гориз. ориентации
        /*if (savedInstanceState == null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new TabLayoutFragment()).commit();
            else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new TabLayoutFragmentLandscape()).commit();
            }
        }*/
    }
}