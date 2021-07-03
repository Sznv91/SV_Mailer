package ru.softvillage.mailer_test.ui.fragmet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.presetner.SessionPresenter;

//https://medium.com/swlh/alertdialog-and-customdialog-in-android-with-kotlin-f42a168c1936
//https://stackoverflow.com/questions/22726408/switch-button-thumb-gets-skewed SwitchCompat like IOS
//https://github.com/Angads25/android-toggle Библиотека для SwitchCompat с возможностью перекраски элементов, и доб. текст
//https://github.com/BelkaLab/Android-Toggle-Switch больше 2х вариантов выбора в SwitchCompat
//https://stackoverflow.com/questions/11134144/android-edittext-onchange-listener EditText действие по оканчаию ввода
public class SendDialog extends DialogFragment {

    private TextView title_send_dialog,
            subtitle_send_dialog,
            title_save_contact,
            title_send_email,
            title_send_sms,
            not_found;
    private View divider_send_dialog_title,
            divider_send_dialog_sms_email,
            divider_dialog_switch_module;
    private EditText edit_text_send_sms,
            edit_text_send_email;

    private SwitchCompat dialog_save_switch,
            dialog_send_email_switch,
            dialog_send_sms_switch;
    private boolean needSendEmail = false,
            needSendSms = false;
    private View button_cancel,
            button_send;
    private ConstraintLayout dialog_switch_module;
    private FrameLayout holder_find_result;

    private static final String RECEIPT_NUMBER = "receipt_number";
    private static final String RECEIPT_DATE = "receipt_date";

    private String receiptNumber;
    private String receiptDate;

    public SendDialog() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        int width = Double.valueOf(getResources().getDisplayMetrics().widthPixels * 0.90).intValue();
        int height = Double.valueOf(getResources().getDisplayMetrics().heightPixels * 0.50).intValue();
        getDialog().getWindow().setLayout(width, height);
        if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog);
        } else {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_black);
        }
//        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    public static SendDialog newInstance(String receiptNumber, String receiptDate) {
        SendDialog fragment = new SendDialog();
        Bundle args = new Bundle();
        args.putString(RECEIPT_NUMBER, receiptNumber);
        args.putString(RECEIPT_DATE, receiptDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receiptNumber = getArguments().getString(RECEIPT_NUMBER);
            receiptDate = getArguments().getString(RECEIPT_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title_send_dialog = view.findViewById(R.id.title_send_dialog);
        subtitle_send_dialog = view.findViewById(R.id.subtitle_send_dialog);
        title_save_contact = view.findViewById(R.id.title_save_contact);
        title_send_email = view.findViewById(R.id.title_send_email);
        title_send_sms = view.findViewById(R.id.title_send_sms);
        not_found = view.findViewById(R.id.not_found);
        divider_send_dialog_title = view.findViewById(R.id.divider_send_dialog_title);
        divider_send_dialog_sms_email = view.findViewById(R.id.divider_send_dialog_sms_email);
        divider_dialog_switch_module = view.findViewById(R.id.divider_dialog_switch_module);
        edit_text_send_sms = view.findViewById(R.id.edit_text_send_sms);
        edit_text_send_email = view.findViewById(R.id.edit_text_send_email);

        dialog_save_switch = view.findViewById(R.id.dialog_save_switch);
        dialog_send_email_switch = view.findViewById(R.id.dialog_send_email_switch);
        dialog_send_sms_switch = view.findViewById(R.id.dialog_send_sms_switch);

        button_cancel = view.findViewById(R.id.button_cancel);
        button_send = view.findViewById(R.id.button_send);

        dialog_switch_module = view.findViewById(R.id.dialog_switch_module);
        holder_find_result = view.findViewById(R.id.holder_find_result);

        initColor();
        initField();
    }

    @SuppressLint("StringFormatMatches")
    private void initField() {
        title_send_dialog.setText(String.format(getString(R.string.title_send_dialog), receiptNumber));
        subtitle_send_dialog.setText(String.format(getString(R.string.subtitle_send_dialog), receiptDate));

        dialog_save_switch.setChecked(SessionPresenter.getInstance().isSaveContact());
        dialog_save_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SessionPresenter.getInstance().setSaveContact(isChecked);
        });
        dialog_send_email_switch.setChecked(needSendEmail);
        dialog_send_sms_switch.setChecked(needSendSms);
        dialog_send_email_switch.setOnCheckedChangeListener((buttonView, isChecked) -> needSendEmail = isChecked);
        dialog_send_sms_switch.setOnCheckedChangeListener((buttonView, isChecked) -> needSendSms = isChecked);

        edit_text_send_sms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.d(App.TAG + "_SendDialog", "EditText -> beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d(App.TAG + "_SendDialog", "EditText -> onTextChanged: count = " + count);

            }

            @SuppressLint("LongLogTag")
            @Override
            public void afterTextChanged(Editable s) {
                Log.d(App.TAG + "_SendDialog", "EditText -> afterTextChanged: s.length() = " + s.length());
                if (s.length() >= 3) {
                    findPhoneNumber(s.toString());
                } else if (s.length() == 0) showControlModule();
            }
        });

        button_cancel.setOnClickListener(v -> Objects.requireNonNull(getDialog()).cancel());
    }

    private void showControlModule() {
        holder_find_result.setVisibility(View.GONE);
        dialog_switch_module.setVisibility(View.VISIBLE);
    }

    private void findPhoneNumber(String phoneNumberPart) {
        dialog_switch_module.setVisibility(View.GONE);
        holder_find_result.setVisibility(View.VISIBLE);
    }

    private void initColor() {
        int currentTheme = SessionPresenter.getInstance().getCurrentTheme();
        if (currentTheme == SessionPresenter.THEME_LIGHT) {
            title_send_dialog.setTextColor(ContextCompat.getColor(title_send_dialog.getContext(), R.color.fonts_lt));
            subtitle_send_dialog.setTextColor(ContextCompat.getColor(subtitle_send_dialog.getContext(), R.color.active_fonts_lt));
            title_save_contact.setTextColor(ContextCompat.getColor(title_save_contact.getContext(), R.color.fonts_lt));
            title_send_email.setTextColor(ContextCompat.getColor(title_send_email.getContext(), R.color.fonts_lt));
            title_send_sms.setTextColor(ContextCompat.getColor(title_send_sms.getContext(), R.color.fonts_lt));
            not_found.setTextColor(ContextCompat.getColor(not_found.getContext(), R.color.active_fonts_lt));
            divider_send_dialog_title.setBackgroundColor(ContextCompat.getColor(divider_send_dialog_title.getContext(), R.color.divider_lt));
            divider_send_dialog_sms_email.setBackgroundColor(ContextCompat.getColor(divider_send_dialog_sms_email.getContext(), R.color.divider_lt));
            divider_dialog_switch_module.setBackgroundColor(ContextCompat.getColor(divider_send_dialog_sms_email.getContext(), R.color.divider_lt));
            edit_text_send_sms.setBackground(ContextCompat.getDrawable(edit_text_send_sms.getContext(), R.drawable.edit_text_background_light));
            edit_text_send_sms.setTextColor(ContextCompat.getColor(edit_text_send_sms.getContext(), R.color.fonts_lt));
            edit_text_send_sms.setHintTextColor(ContextCompat.getColor(edit_text_send_sms.getContext(), R.color.active_fonts_lt));
            edit_text_send_email.setBackground(ContextCompat.getDrawable(edit_text_send_email.getContext(), R.drawable.edit_text_background_light));
            edit_text_send_email.setTextColor(ContextCompat.getColor(edit_text_send_email.getContext(), R.color.fonts_lt));
            edit_text_send_email.setHintTextColor(ContextCompat.getColor(edit_text_send_email.getContext(), R.color.active_fonts_lt));
        } else {
            title_send_dialog.setTextColor(ContextCompat.getColor(title_send_dialog.getContext(), R.color.fonts_dt));
            subtitle_send_dialog.setTextColor(ContextCompat.getColor(subtitle_send_dialog.getContext(), R.color.active_fonts_dt));
            title_save_contact.setTextColor(ContextCompat.getColor(title_save_contact.getContext(), R.color.fonts_dt));
            title_send_email.setTextColor(ContextCompat.getColor(title_send_email.getContext(), R.color.fonts_dt));
            title_send_sms.setTextColor(ContextCompat.getColor(title_send_sms.getContext(), R.color.fonts_dt));
            not_found.setTextColor(ContextCompat.getColor(not_found.getContext(), R.color.active_fonts_dt));
            divider_send_dialog_title.setBackgroundColor(ContextCompat.getColor(divider_send_dialog_title.getContext(), R.color.divider_dt));
            divider_send_dialog_sms_email.setBackgroundColor(ContextCompat.getColor(divider_send_dialog_sms_email.getContext(), R.color.divider_dt));
            divider_dialog_switch_module.setBackgroundColor(ContextCompat.getColor(divider_send_dialog_sms_email.getContext(), R.color.divider_dt));
            edit_text_send_sms.setBackground(ContextCompat.getDrawable(edit_text_send_sms.getContext(), R.drawable.edit_text_background_dark));
            edit_text_send_sms.setTextColor(ContextCompat.getColor(edit_text_send_sms.getContext(), R.color.fonts_dt));
            edit_text_send_sms.setHintTextColor(ContextCompat.getColor(edit_text_send_sms.getContext(), R.color.active_fonts_dt));
            edit_text_send_email.setBackground(ContextCompat.getDrawable(edit_text_send_email.getContext(), R.drawable.edit_text_background_dark));
            edit_text_send_email.setTextColor(ContextCompat.getColor(edit_text_send_email.getContext(), R.color.fonts_dt));
            edit_text_send_email.setHintTextColor(ContextCompat.getColor(edit_text_send_email.getContext(), R.color.active_fonts_dt));
        }
    }
}