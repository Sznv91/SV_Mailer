package ru.softvillage.mailer_main.ui.recyclerView;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.softvillage.mailer_main.dataBase.entity.EvoReceipt;

public abstract class AbstractReceiptViewHolder extends RecyclerView.ViewHolder {

    public AbstractReceiptViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(EvoReceipt entity);
}
