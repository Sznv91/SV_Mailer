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
import ru.softvillage.mailer_test.dataBase.entity.PhoneNumber;
import ru.softvillage.mailer_test.ui.dialog.DeleteDialog;

@RequiredArgsConstructor
public class PhoneFoundAdapter extends RecyclerView.Adapter<PhoneItemHolder> {

    private final List<PhoneNumber> itemList = new ArrayList<>();
    private final LayoutInflater inflater;
    public final ISelectCallback selectCallback;
    private final DeleteDialog.IDeleteDialog deleteCallback;

    protected int lastSelectedPosition = -1;


    @NonNull
    @Override
    public PhoneItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PhoneItemHolder(inflater.inflate(R.layout.item_dialog_found_result, parent, false));
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull PhoneItemHolder holder, int position) {
        holder.bind(itemList.get(position), this, deleteCallback);
        ((RadioButton) holder.itemView.findViewById(R.id.found_item_radio_selector)).setChecked(lastSelectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItems(List<PhoneNumber> entityList) {
        setItems(entityList, true);
    }

    public void setItems(List<PhoneNumber> entityList, boolean needNotify){
        itemList.clear();
        itemList.addAll(entityList);
        lastSelectedPosition = -1;
        if (needNotify){
            notifyDataSetChanged();
        }
    }
}
