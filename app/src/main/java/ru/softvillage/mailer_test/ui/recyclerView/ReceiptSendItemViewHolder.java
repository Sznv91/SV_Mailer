package ru.softvillage.mailer_test.ui.recyclerView;

import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;

public class ReceiptSendItemViewHolder extends ReceiptItemViewHolder {

    public ReceiptSendItemViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(EvoReceipt entity) {
        Log.d(App.TAG + "_ReceiptSendItemViewHolder", "entity.toString: " + entity.toString());
        if (entity.getEvo_receipt_number().equals("0")) {
            if (entity.getEvo_type().equals(EvoReceipt.TYPE_SELL))
                title_receipt_sale.setText(String.format("Чек продажи на %d позиции", entity.getCountOfPosition()));
            if (entity.getEvo_type().equals(EvoReceipt.TYPE_PAYBACK))
                title_receipt_sale.setText(String.format("Чек возврата на %d позиции", entity.getCountOfPosition()));
            int tabIconColor = ContextCompat.getColor(App.getInstance(), R.color.active_fonts_lt);
            iv_static_check_list.getDrawable().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        } else {
            if (entity.getEvo_type().equals(EvoReceipt.TYPE_SELL))
                title_receipt_sale.setText(String.format(App.getInstance().getString(R.string.title_receipt_sale), entity.getEvo_receipt_number(), entity.getCountOfPosition()));
            if (entity.getEvo_type().equals(EvoReceipt.TYPE_PAYBACK))
                title_receipt_sale.setText(String.format(App.getInstance().getString(R.string.title_receipt_payback), entity.getEvo_receipt_number(), entity.getCountOfPosition()));
            iv_static_check_list.setImageDrawable(ContextCompat.getDrawable(iv_static_check_list.getContext(), R.drawable.ic_recipient_list));
        }

        if (entity.isSoft_village_processed()) {
            if (entity.isSv_sent_email()) sv_send_email.setVisibility(View.VISIBLE);
            if (entity.isSv_sent_sms()) sv_send_sms.setVisibility(View.VISIBLE);
        }
        tv_static_summ.setText(String.format(App.getInstance().getString(R.string.tv_static_summ), entity.getPrice()));
        title_receipt_time.setText(entity.getDate_time().toString("HH:mm"));
    }
}