package ru.softvillage.mailer_test.ui.dialog.sendAdapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.dataBase.entity.PhoneNumber;
import ru.softvillage.mailer_test.presetner.SessionPresenter;
import ru.softvillage.mailer_test.ui.dialog.DeleteDialog;

public class PhoneItemHolder extends RecyclerView.ViewHolder {
    private final RadioButton found_item_radio_selector;
    private PhoneNumber number;

    public PhoneItemHolder(@NonNull View itemView) {
        super(itemView);
        found_item_radio_selector = itemView.findViewById(R.id.found_item_radio_selector);
        initColor();
    }

    @SuppressLint("SetTextI18n")
    public void bind(PhoneNumber entity, PhoneFoundAdapter adapter) {
        number = entity;
        found_item_radio_selector.setOnClickListener(v -> {
            adapter.lastSelectedPosition = getAdapterPosition();
            adapter.notifyDataSetChanged();
            adapter.callback.click(entity);
        });
        String toDisplay = entity.getNumber().toString();
        found_item_radio_selector.setText(String.format("+7 (%s) %s-%s-%s", toDisplay.substring(0, 3), toDisplay.substring(3, 6), toDisplay.substring(6, 8), toDisplay.substring(8, 10)));
        found_item_radio_selector.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v);
                return false;
            }
        });
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(found_item_radio_selector.getContext(), v);
        popupMenu.inflate(R.menu.phone_popup_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            DeleteDialog deleteDialog = DeleteDialog.newInstance(DeleteDialog.TYPE_PHONE, number.getNumber().toString());
            deleteDialog.show(App.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager(), "delete_dialog");
            Log.d(App.TAG + "_PhoneItemHolder", "showPopupMenu -> Delete: " + number.toString());
            return false;
        });


        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(found_item_radio_selector.getContext(), "onDismiss",
                        Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }

    private void initColor() {
        if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
            found_item_radio_selector.setTextColor(ContextCompat.getColor(found_item_radio_selector.getContext(), R.color.fonts_lt));
        } else {
            found_item_radio_selector.setTextColor(ContextCompat.getColor(found_item_radio_selector.getContext(), R.color.fonts_dt));
        }
    }
}
