package ru.softvillage.mailer_test.ui.dialog.sendAdapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.dataBase.entity.PhoneNumber;
import ru.softvillage.mailer_test.presetner.SessionPresenter;

public class PhoneItemHolder extends RecyclerView.ViewHolder {
    private final RadioButton found_item_radio_selector;

    public PhoneItemHolder(@NonNull View itemView) {
        super(itemView);
        found_item_radio_selector = itemView.findViewById(R.id.found_item_radio_selector);
        initColor();
    }

    @SuppressLint("SetTextI18n")
    public void bind(PhoneNumber entity, PhoneFoundAdapter adapter) {
        found_item_radio_selector.setOnClickListener(v -> {
            adapter.lastSelectedPosition = getAdapterPosition();
            adapter.notifyDataSetChanged();
            adapter.callback.click(entity);
        });
        String toDisplay = entity.getNumber().toString();
        found_item_radio_selector.setText(String.format("+7 (%s) %s-%s-%s", toDisplay.substring(0, 3), toDisplay.substring(3, 6), toDisplay.substring(6, 8), toDisplay.substring(8, 10)));
    }

    private void initColor() {
        if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
            found_item_radio_selector.setTextColor(ContextCompat.getColor(found_item_radio_selector.getContext(), R.color.fonts_lt));
        } else {
            found_item_radio_selector.setTextColor(ContextCompat.getColor(found_item_radio_selector.getContext(), R.color.fonts_dt));
        }
    }
}
