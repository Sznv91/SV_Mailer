package ru.softvillage.mailer_test.ui.dialog.sendAdapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.dataBase.entity.Email;
import ru.softvillage.mailer_test.ui.dialog.DeleteDialog;
import ru.softvillage.mailer_test.ui.dialog.EditDialog;

@RequiredArgsConstructor
public class EmailFoundAdapter extends RecyclerView.Adapter<EmailItemHolder> {

    private final List<Email> itemList = new ArrayList<>();
    private final LayoutInflater inflater;
    public final ISelectCallback selectCallback;
    private final DeleteDialog.IDeleteDialog deleteCallback;
    private final EditDialog.IEditDialog editCallback;

    protected int lastSelectedPosition = -1;

    @NonNull
    @Override
    public EmailItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EmailItemHolder(inflater.inflate(R.layout.item_dialog_found_result, parent, false));
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull EmailItemHolder holder, int position) {
        holder.bind(itemList.get(position), this, deleteCallback, editCallback);
        ((RadioButton) holder.itemView.findViewById(R.id.found_item_radio_selector)).setChecked(lastSelectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItems(List<Email> entityList) {
        setItems(entityList, true);
    }

    public void setItems(List<Email> entityList, boolean needNotify) {
        itemList.clear();
        itemList.addAll(entityList);
        lastSelectedPosition = -1;
        if (needNotify) {
            notifyDataSetChanged();
        }
    }
}
