package ru.softvillage.mailer_test.ui.recyclerView;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;
import ru.softvillage.mailer_test.presetner.SessionPresenter;

public class ReceiptItemViewHolder extends AbstractReceiptViewHolder {
    protected final TextView title_receipt_sale;
    protected final TextView tv_static_summ;
    protected final TextView title_receipt_time;
    protected final ImageView iv_static_check_list, sv_send_email, sv_send_sms;

    private final ConstraintLayout item_receipt_layout;

    private final Observer<Integer> observer = new Observer<Integer>() {

        @SuppressLint("LongLogTag")
        @Override
        public void onChanged(Integer integer) {
            if (integer == SessionPresenter.THEME_LIGHT) {
                item_receipt_layout.setBackgroundColor(ContextCompat.getColor(item_receipt_layout.getContext(), R.color.background_lt));
                title_receipt_sale.setTextColor(ContextCompat.getColor(title_receipt_sale.getContext(), R.color.fonts_lt));
                tv_static_summ.setTextColor(ContextCompat.getColor(tv_static_summ.getContext(), R.color.active_fonts_lt));
                title_receipt_time.setTextColor(ContextCompat.getColor(title_receipt_time.getContext(), R.color.active_fonts_lt));
            } else {
                item_receipt_layout.setBackgroundColor(ContextCompat.getColor(item_receipt_layout.getContext(), R.color.background_dt));
                title_receipt_sale.setTextColor(ContextCompat.getColor(title_receipt_sale.getContext(), R.color.fonts_dt));
                tv_static_summ.setTextColor(ContextCompat.getColor(tv_static_summ.getContext(), R.color.active_fonts_dt));
                title_receipt_time.setTextColor(ContextCompat.getColor(title_receipt_time.getContext(), R.color.active_fonts_dt));
            }
        }
    };

    public ReceiptItemViewHolder(@NonNull View itemView) {
        super(itemView);
        item_receipt_layout = itemView.findViewById(R.id.item_receipt_layout);
        title_receipt_sale = itemView.findViewById(R.id.title_receipt_sale);
        tv_static_summ = itemView.findViewById(R.id.tv_static_summ);
        title_receipt_time = itemView.findViewById(R.id.title_receipt_time);
        iv_static_check_list = itemView.findViewById(R.id.iv_static_check_list);
        sv_send_email = itemView.findViewById(R.id.sv_send_email);
        sv_send_sms = itemView.findViewById(R.id.sv_send_sms);
        SessionPresenter.getInstance().getCurrentThemeLiveData().observeForever(observer);
    }

    public void bind(EvoReceipt entity) {
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

        tv_static_summ.setText(String.format(App.getInstance().getString(R.string.tv_static_summ), entity.getPrice()));
        title_receipt_time.setText(entity.getDate_time().toString("HH:mm"));
    }

}
