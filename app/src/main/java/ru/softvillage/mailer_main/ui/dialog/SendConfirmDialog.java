package ru.softvillage.mailer_main.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import ru.softvillage.mailer_main.R;
import ru.softvillage.mailer_main.presetner.SessionPresenter;

public class SendConfirmDialog extends DialogFragment implements View.OnClickListener {

    ConstraintLayout confirm_layout;

    private TextView title_send_confirm_dialog,
            subtitle_send_confirm_dialog,
            title_phone_number_receiver,
            content_phone_number_receiver,
            title_email_receiver,
            content_email_receiver,
            button_ok;

    private static final String RECEIPT_NUMBER = "receipt_number";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String EMAIL = "email";
    private static final String IS_FISCALIZED = "is_new_receipt";


    private String receiptNumber,
            phoneNumber,
            email;
    private boolean isFiscalized = false;


    public SendConfirmDialog() {
    }

    public static SendConfirmDialog newInstance(String receiptNumber, String phoneNumber, String email, boolean isFiscalized) {
        Bundle args = new Bundle();
        args.putString(RECEIPT_NUMBER, receiptNumber);
        args.putString(PHONE_NUMBER, phoneNumber);
        args.putString(EMAIL, email);
        args.putBoolean(IS_FISCALIZED, isFiscalized);

        SendConfirmDialog fragment = new SendConfirmDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receiptNumber = getArguments().getString(RECEIPT_NUMBER);
            phoneNumber = getArguments().getString(PHONE_NUMBER);
            email = getArguments().getString(EMAIL);
            isFiscalized = getArguments().getBoolean(IS_FISCALIZED);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawableResource(android.R.color.transparent);

            int width = Double.valueOf(getResources().getDisplayMetrics().widthPixels * 0.7).intValue();
            int height = Double.valueOf(getResources().getDisplayMetrics().heightPixels * 0.35).intValue();
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_send_confirm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        confirm_layout = view.findViewById(R.id.confirm_layout);

        title_send_confirm_dialog = view.findViewById(R.id.title_send_confirm_dialog);
        subtitle_send_confirm_dialog = view.findViewById(R.id.subtitle_send_confirm_dialog);
        title_phone_number_receiver = view.findViewById(R.id.title_phone_number_receiver);
        content_phone_number_receiver = view.findViewById(R.id.content_phone_number_receiver);
        title_email_receiver = view.findViewById(R.id.title_email_receiver);
        content_email_receiver = view.findViewById(R.id.content_email_receiver);

        button_ok = view.findViewById(R.id.button_ok);
        button_ok.setOnClickListener(this);

        initFields();
    }

    private void initFields() {
        if (!isFiscalized) {
            if (!TextUtils.isEmpty(receiptNumber))
                subtitle_send_confirm_dialog.setText(String.format(getString(R.string.receipt_send_confirm_subtitle), receiptNumber));
            else
                subtitle_send_confirm_dialog.setText(getString(R.string.receipt_send_confirm_subtitle_without_number));
        }


        if (!TextUtils.isEmpty(phoneNumber)) {
            content_phone_number_receiver.setText(String.format("+7 (%s) %s-%s-%s", phoneNumber.substring(0, 3), phoneNumber.substring(3, 6), phoneNumber.substring(6, 8), phoneNumber.substring(8, 10)));
        } else {
            title_phone_number_receiver.setVisibility(View.GONE);
            content_phone_number_receiver.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(email)) {
            content_email_receiver.setText(email);
        } else {
            title_email_receiver.setVisibility(View.GONE);
            content_email_receiver.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUITheme();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                dismiss();
                requireActivity().onBackPressed();
                break;
        }
    }

    private void updateUITheme() {
        if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
            confirm_layout.setBackground(ContextCompat.getDrawable(confirm_layout.getContext(), R.drawable.bg_dialog));
            title_send_confirm_dialog.setTextColor(ContextCompat.getColor(title_send_confirm_dialog.getContext(), R.color.fonts_lt));
            subtitle_send_confirm_dialog.setTextColor(ContextCompat.getColor(subtitle_send_confirm_dialog.getContext(), R.color.active_fonts_lt));
            title_phone_number_receiver.setTextColor(ContextCompat.getColor(title_phone_number_receiver.getContext(), R.color.fonts_lt));
            content_phone_number_receiver.setTextColor(ContextCompat.getColor(content_phone_number_receiver.getContext(), R.color.fonts_lt));
            title_email_receiver.setTextColor(ContextCompat.getColor(title_email_receiver.getContext(), R.color.fonts_lt));
            content_email_receiver.setTextColor(ContextCompat.getColor(content_email_receiver.getContext(), R.color.fonts_lt));
        } else {
            confirm_layout.setBackground(ContextCompat.getDrawable(confirm_layout.getContext(), R.drawable.bg_dialog_black));
            title_send_confirm_dialog.setTextColor(ContextCompat.getColor(title_send_confirm_dialog.getContext(), R.color.fonts_dt));
            subtitle_send_confirm_dialog.setTextColor(ContextCompat.getColor(subtitle_send_confirm_dialog.getContext(), R.color.active_fonts_dt));
            title_phone_number_receiver.setTextColor(ContextCompat.getColor(title_phone_number_receiver.getContext(), R.color.fonts_dt));
            content_phone_number_receiver.setTextColor(ContextCompat.getColor(content_phone_number_receiver.getContext(), R.color.fonts_dt));
            title_email_receiver.setTextColor(ContextCompat.getColor(title_email_receiver.getContext(), R.color.fonts_dt));
            content_email_receiver.setTextColor(ContextCompat.getColor(content_email_receiver.getContext(), R.color.fonts_dt));
        }
    }
}
