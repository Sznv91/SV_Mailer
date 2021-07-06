package ru.softvillage.mailer_test.ui.dialog;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.dataBase.entity.Email;
import ru.softvillage.mailer_test.dataBase.entity.PhoneNumber;
import ru.softvillage.mailer_test.presetner.SessionPresenter;
import ru.softvillage.mailer_test.ui.dialog.sendAdapter.EmailFoundAdapter;
import ru.softvillage.mailer_test.ui.dialog.sendAdapter.PhoneFoundAdapter;

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
            title_send_sms;
    private View divider_send_dialog_title,
            divider_send_dialog_sms_email,
            divider_dialog_switch_module;
    private MaskedEditText edit_text_send_sms;
    private EditText edit_text_send_email;

    private SwitchCompat dialog_save_switch,
            dialog_send_email_switch,
            dialog_send_sms_switch;
    private boolean needSendEmail = false,
            needSendSms = false;
    private View button_cancel,
            button_send;
    private ConstraintLayout dialog_switch_module;
    private FrameLayout holder_phone_find_result;
    private FrameLayout holder_email_find_result;
    private RecyclerView dialog_recycler_view_phone_found_item;
    private RecyclerView dialog_recycler_view_email_found_item;
    private PhoneFoundAdapter phoneAdapter;
    private EmailFoundAdapter emailAdapter;

    private static final String RECEIPT_NUMBER = "receipt_number";
    private static final String RECEIPT_DATE = "receipt_date";

    private String receiptNumber;
    private String receiptDate;

    private PhoneNumber selectedPhoneNumber;
    private Email selectedEmail;
    private List<PhoneNumber> phoneNumberList = new ArrayList<>();
    private List<Email> emailList = new ArrayList<>();

    public SendDialog() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int width = Double.valueOf(getResources().getDisplayMetrics().widthPixels * 0.90).intValue();
            int height = Double.valueOf(getResources().getDisplayMetrics().heightPixels * 0.50).intValue();
            getDialog().getWindow().setLayout(width, height);
        } else {
            int width = Double.valueOf(getResources().getDisplayMetrics().widthPixels * 0.50).intValue();
            int height = Double.valueOf(getResources().getDisplayMetrics().heightPixels * 0.498).intValue();
            getDialog().getWindow().setLayout(width, height);
        }

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
        holder_phone_find_result = view.findViewById(R.id.holder_phone_find_result);
        holder_email_find_result = view.findViewById(R.id.holder_email_find_result);
        dialog_recycler_view_phone_found_item = view.findViewById(R.id.dialog_recycler_view_phone_found_item);
        dialog_recycler_view_email_found_item = view.findViewById(R.id.dialog_recycler_view_email_found_item);


        initPhoneRecyclerView();
        initEmailRecyclerView();
        initField();
        initColor();
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
//                Log.d(App.TAG + "_SendDialog", "EditText -> afterTextChanged: s.length() = " + s.length());
//                Log.d(App.TAG + "_SendDialog", "EditText -> afterTextChanged: Editable s " + s.toString());
//                Log.d(App.TAG + "_SendDialog", "EditText -> afterTextChanged: .getRawText():" + edit_text_send_sms.getRawText());
                String enteredText = edit_text_send_sms.getRawText();
                if (enteredText.length() >= 4) {
                    findPhoneNumber(enteredText);
                } else if (enteredText.length() == 0) showControlModule();

//                Log.d(App.TAG + "_SendDialog", "EditText -> afterTextChanged: .getRawText():" + enteredText + "_length: " + enteredText.length());
                if (enteredText.length() == 10) {
                    phoneSetter(enteredText);
                }
            }
        });


        edit_text_send_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showEmailFoundResult();
                    Log.d(App.TAG + "_SendDialog", "edit_text_send_email.setOnFocusChangeListener");
                }
            }
        });

        button_cancel.setOnClickListener(v -> Objects.requireNonNull(getDialog()).cancel());
        button_send.setOnClickListener(v -> {
            if (selectedPhoneNumber != null) {
                App.getInstance().getDbHelper().createOrReplacePhone(selectedPhoneNumber);
            }
            //todo переделать заглушку
            String textFieldEmail = edit_text_send_email.getText().toString();
            if (!TextUtils.isEmpty(textFieldEmail) && selectedEmail != null) {
                if (textFieldEmail.equals(selectedEmail.getEmailAddress())){
                    App.getInstance().getDbHelper().createOrUpdateEmail(selectedEmail);
                } else {
                    for (Email emailTemp: emailList){
                        if (emailTemp.getEmailAddress().equals(textFieldEmail)){
                            selectedEmail = emailTemp;
                            break;
                        }
                    } if (!textFieldEmail.equals(selectedEmail.getEmailAddress())){
                        selectedEmail = new Email();
                        selectedEmail.setEmailAddress(edit_text_send_email.getText().toString());
                        selectedEmail.setLinkedPhoneNumber(selectedPhoneNumber.getNumber());
                    }
                    App.getInstance().getDbHelper().createOrUpdateEmail(selectedEmail);

                }
            } else {
                for (Email emailTemp: emailList) {
                    if (emailTemp.getEmailAddress().equals(textFieldEmail)) {
                        selectedEmail = emailTemp;
                        break;
                    }
                }
                if (selectedEmail == null){
                    selectedEmail = new Email();
                    selectedEmail.setEmailAddress(textFieldEmail);
                    selectedEmail.setLinkedPhoneNumber(selectedPhoneNumber.getNumber());
                }
                App.getInstance().getDbHelper().createOrUpdateEmail(selectedEmail);

            }
        });
    }

    private void showControlModule() {
        holder_phone_find_result.setVisibility(View.GONE);
        holder_email_find_result.setVisibility(View.GONE);
        dialog_switch_module.setVisibility(View.VISIBLE);
    }

    private void showPhoneFoundResult() {
        holder_email_find_result.setVisibility(View.GONE);
        holder_phone_find_result.setVisibility(View.VISIBLE);
        dialog_switch_module.setVisibility(View.GONE);
    }

    private void showEmailFoundResult() {
        Log.d(App.TAG + "_SendDialog", "showEmailFoundResult -> emailList.size() " + emailList.size());
        if (emailList.size() > 0){
            holder_email_find_result.setVisibility(View.VISIBLE);
            holder_phone_find_result.setVisibility(View.GONE);
            dialog_switch_module.setVisibility(View.GONE);
        } else {
            showControlModule();
        }

    }

    private void findPhoneNumber(String phoneNumberPart) {
        Thread searchThread = new Thread(() -> {
            phoneNumberList = App.getInstance().getDbHelper().getPhoneNumberList(phoneNumberPart);
            Log.d(App.TAG + "_SendDialog", "findPhoneNumber Thread. Выполнили поиск по БД, результат: " + phoneNumberList.toString());

            if (phoneNumberList.size() > 0) {
                getActivity().runOnUiThread(() -> {
                    phoneAdapter.setItems(phoneNumberList);
                    showPhoneFoundResult();
                });
            } else {
                if (dialog_switch_module.getVisibility() != View.VISIBLE) {
                    requireActivity().runOnUiThread(this::showControlModule);
                }
            }
        });

        if (edit_text_send_sms.getRawText().length() < 10) {
            if (searchThread.isAlive()) {
                searchThread.interrupt();
            }
            searchThread.start();
        }
    }

    private void findEmails() {
        Thread searchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                emailList = App.getInstance().getDbHelper().getEmailListByPhoneNumber(selectedPhoneNumber.getNumber());
                getActivity().runOnUiThread(() -> {
                    emailAdapter.setItems(emailList);
                });
            }
        });

        if (searchThread.isAlive()) {
            searchThread.interrupt();
        }
        searchThread.start();
    }

    private void phoneSetter(String editTextSendSmsFieldContent) {
        Long phoneNumberDigit = Long.parseLong(editTextSendSmsFieldContent);
        for (PhoneNumber phoneNumber : phoneNumberList) {
            if (phoneNumber.getNumber().equals(phoneNumberDigit)) {
                selectedPhoneNumber = phoneNumber;
                break;
            }
        }
        if (selectedPhoneNumber == null || !selectedPhoneNumber.getNumber().equals(phoneNumberDigit)) {
            selectedPhoneNumber = new PhoneNumber(phoneNumberDigit);
        }
        findEmails();
    }

    private void initPhoneRecyclerView() {
        phoneAdapter = new PhoneFoundAdapter(LayoutInflater.from(getContext()), number -> {
            edit_text_send_sms.setText(number.getNumber().toString());
            Log.d(App.TAG + "_SendDialog", "_initPhoneRecyclerView -> callBack: " + number.toString());
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dialog_recycler_view_phone_found_item.setLayoutManager(layoutManager);
        dialog_recycler_view_phone_found_item.setAdapter(phoneAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(getContext(), R.drawable.line_divider)));
        dialog_recycler_view_phone_found_item.addItemDecoration(divider);

    }

    private void initEmailRecyclerView() {
        emailAdapter = new EmailFoundAdapter(LayoutInflater.from(getContext()), email -> {
            edit_text_send_email.setText(email.getEmailAddress());
            selectedEmail = email;
            Log.d(App.TAG + "_SendDialog", "_initEmailRecyclerView -> callBack: " + email.toString());
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dialog_recycler_view_email_found_item.setLayoutManager(layoutManager);
        dialog_recycler_view_email_found_item.setAdapter(emailAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(getContext(), R.drawable.line_divider)));
        dialog_recycler_view_email_found_item.addItemDecoration(divider);
    }

    private void initColor() {
        int currentTheme = SessionPresenter.getInstance().getCurrentTheme();
        if (currentTheme == SessionPresenter.THEME_LIGHT) {
            title_send_dialog.setTextColor(ContextCompat.getColor(title_send_dialog.getContext(), R.color.fonts_lt));
            subtitle_send_dialog.setTextColor(ContextCompat.getColor(subtitle_send_dialog.getContext(), R.color.active_fonts_lt));
            title_save_contact.setTextColor(ContextCompat.getColor(title_save_contact.getContext(), R.color.fonts_lt));
            title_send_email.setTextColor(ContextCompat.getColor(title_send_email.getContext(), R.color.fonts_lt));
            title_send_sms.setTextColor(ContextCompat.getColor(title_send_sms.getContext(), R.color.fonts_lt));
            divider_send_dialog_title.setBackgroundColor(ContextCompat.getColor(divider_send_dialog_title.getContext(), R.color.divider_lt));
            divider_send_dialog_sms_email.setBackgroundColor(ContextCompat.getColor(divider_send_dialog_sms_email.getContext(), R.color.divider_lt));
            divider_dialog_switch_module.setBackgroundColor(ContextCompat.getColor(divider_send_dialog_sms_email.getContext(), R.color.divider_lt));
            edit_text_send_sms.setBackground(ContextCompat.getDrawable(edit_text_send_sms.getContext(), R.drawable.edit_text_background_light));
            edit_text_send_sms.setTextColor(ContextCompat.getColor(edit_text_send_sms.getContext(), R.color.fonts_lt));
            edit_text_send_sms.setHintTextColor(ContextCompat.getColor(edit_text_send_sms.getContext(), R.color.active_fonts_lt));
            edit_text_send_sms.setDrawingCacheBackgroundColor(ContextCompat.getColor(edit_text_send_sms.getContext(), R.color.fonts_lt));
            edit_text_send_email.setBackground(ContextCompat.getDrawable(edit_text_send_email.getContext(), R.drawable.edit_text_background_light));
            edit_text_send_email.setTextColor(ContextCompat.getColor(edit_text_send_email.getContext(), R.color.fonts_lt));
            edit_text_send_email.setHintTextColor(ContextCompat.getColor(edit_text_send_email.getContext(), R.color.active_fonts_lt));
        } else {
            title_send_dialog.setTextColor(ContextCompat.getColor(title_send_dialog.getContext(), R.color.fonts_dt));
            subtitle_send_dialog.setTextColor(ContextCompat.getColor(subtitle_send_dialog.getContext(), R.color.active_fonts_dt));
            title_save_contact.setTextColor(ContextCompat.getColor(title_save_contact.getContext(), R.color.fonts_dt));
            title_send_email.setTextColor(ContextCompat.getColor(title_send_email.getContext(), R.color.fonts_dt));
            title_send_sms.setTextColor(ContextCompat.getColor(title_send_sms.getContext(), R.color.fonts_dt));
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