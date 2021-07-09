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
import ru.softvillage.mailer_test.dataBase.entity.Email;
import ru.softvillage.mailer_test.presetner.SessionPresenter;
import ru.softvillage.mailer_test.ui.dialog.DeleteDialog;

public class EmailItemHolder extends RecyclerView.ViewHolder {
    private final RadioButton found_item_radio_selector;
    private Email email;
    private DeleteDialog.IDeleteDialog deleteCallback;

    public EmailItemHolder(@NonNull View itemView) {
        super(itemView);
        found_item_radio_selector = itemView.findViewById(R.id.found_item_radio_selector);
        initColor();
    }

    @SuppressLint("SetTextI18n")
    public void bind(Email entity, EmailFoundAdapter adapter, DeleteDialog.IDeleteDialog callback) {
        this.deleteCallback = callback;
        this.email = entity;
        found_item_radio_selector.setOnClickListener(v -> {
            adapter.lastSelectedPosition = getAdapterPosition();
            adapter.notifyDataSetChanged();
            adapter.selectCallback.clickOnItem(entity, EntityType.EMAIL);
        });
        String toDisplay = entity.getEmailAddress();
        found_item_radio_selector.setText(toDisplay);
        found_item_radio_selector.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v);
                return false;
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(found_item_radio_selector.getContext(), v);
        popupMenu.inflate(R.menu.email_popup_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            DeleteDialog deleteDialog = DeleteDialog.newInstance(EntityType.EMAIL, email.getEmailAddress(), getAdapterPosition(), deleteCallback);
            deleteDialog.setCancelable(false);
            deleteDialog.show(App.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager(), "delete_dialog");
            Log.d(App.TAG + "_EmailItemHolder", "showPopupMenu -> Delete: " + email.toString());
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
