package ru.softvillage.mailer_test.ui.recyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;

@RequiredArgsConstructor
public class ReceiptItemAdapter extends RecyclerView.Adapter<AbstractReceiptViewHolder> {

    public static final String DATE_SPLITTER_NAME = App.TAG + "_Date_Splitter_NAME";
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_DATA_SPLITTER = 1;
    private static final int TYPE_NOT_FISCALIZED = 2;


    private final LayoutInflater inflater;
    private final List<EvoReceipt> itemList = new ArrayList<>();
    private final itemClickInterface callback;

    @NonNull
    @Override
    public AbstractReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_DATA_SPLITTER:
                return new ReceiptDateSplitHolder(inflater.inflate(R.layout.item_good_date, parent, false));
            case TYPE_NORMAL:
            case TYPE_NOT_FISCALIZED:
                return new ReceiptItemViewHolder(inflater.inflate(R.layout.item_receipt_all_tab, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractReceiptViewHolder holder, int position) {

        holder.bind(itemList.get(holder.getAdapterPosition()));
        switch (holder.getItemViewType()) {
            case TYPE_DATA_SPLITTER:
                TextView dateTextView = holder.itemView.findViewById(R.id.item_date_splitter);
                dateTextView.setOnClickListener(v -> {
                    callback.pushOnDate(itemList.get(position).getDate_time());
                });
                break;
            case TYPE_NORMAL:
                holder.itemView.setOnClickListener(v -> callback.clickClick(itemList.get(position)));
                break;
            case TYPE_NOT_FISCALIZED:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItems(List<EvoReceipt> entityList) {
        itemList.clear();
        itemList.addAll(entityList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position).getEvo_uuid() != null && itemList.get(position).getEvo_uuid().equals(DATE_SPLITTER_NAME)) {
            return TYPE_DATA_SPLITTER;
        }
        if (TextUtils.isEmpty(itemList.get(position).getEvo_uuid())) {
            return TYPE_NOT_FISCALIZED;
        }
        return TYPE_NORMAL;
    }

    public interface itemClickInterface {
        void clickClick(EvoReceipt recipientEntity);

        void pushOnDate(LocalDateTime date);
    }


}
