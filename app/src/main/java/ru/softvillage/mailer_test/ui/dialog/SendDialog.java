package ru.softvillage.mailer_test.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.os.Build;
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
import android.widget.ImageView;
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
import ru.softvillage.mailer_test.dataBase.entity.NotFiscalizedReceipt;
import ru.softvillage.mailer_test.dataBase.entity.PartialEvoReceiptSvDbUpdate;
import ru.softvillage.mailer_test.dataBase.entity.PhoneNumber;
import ru.softvillage.mailer_test.network.entity.SentEntity;
import ru.softvillage.mailer_test.presetner.SessionPresenter;
import ru.softvillage.mailer_test.service.SendToBackendService;
import ru.softvillage.mailer_test.ui.dialog.sendAdapter.EmailFoundAdapter;
import ru.softvillage.mailer_test.ui.dialog.sendAdapter.EntityType;
import ru.softvillage.mailer_test.ui.dialog.sendAdapter.ISelectCallback;
import ru.softvillage.mailer_test.ui.dialog.sendAdapter.PhoneFoundAdapter;
import ru.softvillage.mailer_test.ui.fragmet.ReceiptDetailFragment;

import static ru.softvillage.mailer_test.App.isMyServiceRunning;

//https://medium.com/swlh/alertdialog-and-customdialog-in-android-with-kotlin-f42a168c1936
//https://stackoverflow.com/questions/22726408/switch-button-thumb-gets-skewed SwitchCompat like IOS
//https://github.com/Angads25/android-toggle Библиотека для SwitchCompat с возможностью перекраски элементов, и доб. текст
//https://github.com/BelkaLab/Android-Toggle-Switch больше 2х вариантов выбора в SwitchCompat
//https://stackoverflow.com/questions/11134144/android-edittext-onchange-listener EditText действие по оканчаию ввода
public class SendDialog extends DialogFragment implements
        DeleteDialog.IDeleteDialog,
        EditDialog.IEditDialog,
        ISelectCallback {

    private TextView title_send_dialog,
            subtitle_send_dialog,
            title_save_contact,
            title_send_email,
            title_send_sms,
            title_button_send;
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
    private ImageView icon_send_sms,
            icon_send_email;
    private PhoneFoundAdapter phoneAdapter;
    private EmailFoundAdapter emailAdapter;

    private static final String RECEIPT_NUMBER = "receipt_number";
    private static final String RECEIPT_DATE = "receipt_date";
    private static final String RECEIPT_UUID = "receipt_uuid";

    private String receiptNumber;
    private String receiptDate;
    private String evoUuid;

    private boolean canProcessWithETSendSms = true;
    private int lastEmailLength = 0;
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


    public static SendDialog newInstance(String receiptNumber, String receiptDate, String evoUuid) {
        SendDialog fragment = new SendDialog();
        Bundle args = new Bundle();
        args.putString(RECEIPT_NUMBER, receiptNumber);
        args.putString(RECEIPT_DATE, receiptDate);
        args.putString(RECEIPT_UUID, evoUuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receiptNumber = getArguments().getString(RECEIPT_NUMBER);
            receiptDate = getArguments().getString(RECEIPT_DATE);
            evoUuid = getArguments().getString(RECEIPT_UUID);
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
        title_button_send = view.findViewById(R.id.title_button_send);
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

        icon_send_sms = view.findViewById(R.id.icon_send_sms);
        icon_send_email = view.findViewById(R.id.icon_send_email);


        initPhoneRecyclerView();
        initEmailRecyclerView();
        initField();
        initColor();
    }

    @SuppressLint("StringFormatMatches")
    private void initField() {
        if (TextUtils.isEmpty(receiptNumber)) {
            title_send_dialog.setText("Введите данные для отправки чека");
            subtitle_send_dialog.setVisibility(View.GONE);
        } else {
            title_send_dialog.setText(String.format(getString(R.string.title_send_dialog), receiptNumber));
            subtitle_send_dialog.setText(String.format(getString(R.string.subtitle_send_dialog), receiptDate));
        }


        dialog_save_switch.setChecked(SessionPresenter.getInstance().isSaveContact());
        dialog_save_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SessionPresenter.getInstance().setSaveContact(isChecked);
        });
        dialog_send_email_switch.setChecked(needSendEmail);
        dialog_send_sms_switch.setChecked(needSendSms);
        dialog_send_email_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            needSendEmail = isChecked;
            checkPossibilitySending();
        });
        dialog_send_sms_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            needSendSms = isChecked;
            checkPossibilitySending();
        });

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
                Log.d(App.TAG + "_SendDialog", "EditText -> afterTextChanged: .getRawText():" + edit_text_send_sms.getRawText());
               /* Log.d(App.TAG + "_SendDialog", "EditText -> afterTextChanged: s.length() = " + s.length());
                Log.d(App.TAG + "_SendDialog", "EditText -> afterTextChanged: Editable s " + s.toString());
                Log.d(App.TAG + "_SendDialog", "EditText -> afterTextChanged: .getRawText():" + edit_text_send_sms.getRawText());*/
                if (canProcessWithETSendSms) {
                    Log.d(App.TAG + "_SendDialog", "EditText CAN TOUCH-> afterTextChanged: .getRawText():" + edit_text_send_sms.getRawText());

                    String enteredText = edit_text_send_sms.getRawText();
                    if (enteredText.length() >= 2) {
                        canProcessWithETSendSms = false;
                        findPhoneNumber(enteredText, true);
                    }
                    if (enteredText.length() == 0) {
                        selectedPhoneNumber = null;
                        phoneNumberList.clear();
                        phoneAdapter.setItems(phoneNumberList);
                        selectedEmail = null;
                        emailList.clear();
                        emailAdapter.setItems(emailList);
                        clearIconColorFilter(true, true);
                        showControlModule();
                    }
                    if (enteredText.length() == 10) {
                        phoneSetter(enteredText);
                        findEmails();
                    }
                    if (enteredText.length() < 10) {
                        selectedPhoneNumber = null;
                        dialog_send_sms_switch.setChecked(false);
                        checkPossibilitySending();
                    }
                }
            }
        });

        edit_text_send_sms.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showPhoneFoundResultModule();
            }
        });

        edit_text_send_email.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(App.TAG + "_SendDialog", "edit_text_send_email.afterTextChanged s->" + s.toString() + " s.length() = " + s.length());
                if (s.length() != 0) {
                    if (lastEmailLength != s.length() && s.length() >= 2) {
                        findPartialEmails(s.toString(), true);
                    }
                }

                if (s.length() == 0) {
                    selectedEmail = null;
                    emailList.clear();
                    emailAdapter.setItems(emailList);
                    clearIconColorFilter(false, true);
                    showControlModule();
                }

                if (s.length() >= 5) {
                    emailCorrectlyChecker(s.toString(), true);
                }
            }
        });

        edit_text_send_email.setOnClickListener(v -> showEmailFoundResultModule());

        edit_text_send_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showEmailFoundResultModule();
                    Log.d(App.TAG + "_SendDialog", "edit_text_send_email.setOnFocusChangeListener");
                }
            }
        });

        button_cancel.setOnClickListener(v -> Objects.requireNonNull(getDialog()).cancel());


        icon_send_sms.setOnClickListener(v -> {
            if (holder_phone_find_result.getVisibility() == View.VISIBLE) {
                showControlModule();
            } else {
                showPhoneFoundResultModule();
            }
        });
        icon_send_email.setOnClickListener(v -> {
            if (holder_email_find_result.getVisibility() == View.VISIBLE) {
                showControlModule();
            } else {
                showEmailFoundResultModule();
            }
        });
    }

    private String emailCorrectlyChecker(String email, boolean needMoveSwitch) {
        email = email.replaceAll(" ", "");
        if (email.contains("@") && email.contains(".")) {
            if (needMoveSwitch) dialog_send_email_switch.setChecked(true);
            return email;
        }
        if (needMoveSwitch) dialog_send_email_switch.setChecked(false);
        return null;
    }

    private void showControlModule() {
        dialog_switch_module.setVisibility(View.VISIBLE);
        holder_phone_find_result.setVisibility(View.GONE);
        holder_email_find_result.setVisibility(View.GONE);
    }

    private void showPhoneFoundResultModule() {
        if (phoneNumberList.isEmpty()) {
            showControlModule();
        } else {
            dialog_switch_module.setVisibility(View.GONE);
            holder_phone_find_result.setVisibility(View.VISIBLE);
            holder_email_find_result.setVisibility(View.GONE);
        }
    }

    private void showEmailFoundResultModule() {
        if (emailList.isEmpty()) {
            showControlModule();
        } else {
            dialog_switch_module.setVisibility(View.GONE);
            holder_phone_find_result.setVisibility(View.GONE);
            holder_email_find_result.setVisibility(View.VISIBLE);
        }
    }

    private void findPhoneNumber(String phoneNumberPart, boolean needShowFoundModule) {
        Thread searchThread = new Thread(() -> {
            phoneNumberList.clear();
            phoneNumberList.addAll(App.getInstance().getDbHelper().getPhoneNumberList(phoneNumberPart));
            requireActivity().runOnUiThread(() -> {
                phoneAdapter.setItems(phoneNumberList);
                setIconColorFilter(true, false);
                if (needShowFoundModule) showPhoneFoundResultModule();
            });
            canProcessWithETSendSms = true;
            Log.d(App.TAG + "_SendDialog", "findPhoneNumber Thread. Выполнили поиск по БД, результат: " + phoneNumberList.toString());
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
                emailList.clear();
                emailList.addAll(App.getInstance().getDbHelper().getEmailListByPhoneNumber(selectedPhoneNumber.getNumber()));
                canProcessWithETSendSms = true;
                getActivity().runOnUiThread(() -> {
                    emailAdapter.setItems(emailList);
                    if (!emailList.isEmpty()) {
                        setIconColorFilter(false, true);
                    }
                });
            }
        });

        if (searchThread.isAlive()) {
            searchThread.interrupt();
        }
        searchThread.start();
    }

    private void findPartialEmails(String partEmail, boolean needShowFoundModule) {
        Thread searchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                emailList.clear();
                emailList.addAll(App.getInstance().getDbHelper().getEmailListByPartialMail(partEmail));
                getActivity().runOnUiThread(() -> {
                    emailAdapter.setItems(emailList);
                    if (!emailList.isEmpty()) setIconColorFilter(false, true);
                    if (needShowFoundModule) showEmailFoundResultModule();
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
                dialog_send_sms_switch.setChecked(true);
                checkPossibilitySending();
                break;
            }
            dialog_send_sms_switch.setChecked(false);
            checkPossibilitySending();
        }
        if (selectedPhoneNumber == null || !selectedPhoneNumber.getNumber().equals(phoneNumberDigit)) {
            selectedPhoneNumber = new PhoneNumber(phoneNumberDigit);
            dialog_send_sms_switch.setChecked(true);
            checkPossibilitySending();
        }
    }


    private void initPhoneRecyclerView() {
        phoneAdapter = new PhoneFoundAdapter(LayoutInflater.from(getContext()),
                this,
                this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dialog_recycler_view_phone_found_item.setLayoutManager(layoutManager);
        dialog_recycler_view_phone_found_item.setAdapter(phoneAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(getContext(), R.drawable.line_divider)));
        dialog_recycler_view_phone_found_item.addItemDecoration(divider);

    }

    private void initEmailRecyclerView() {
        emailAdapter = new EmailFoundAdapter(LayoutInflater.from(getContext()),
                this, this, this::saveEditedEmail);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dialog_recycler_view_email_found_item.setLayoutManager(layoutManager);
        dialog_recycler_view_email_found_item.setAdapter(emailAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(getContext(), R.drawable.line_divider)));
        dialog_recycler_view_email_found_item.addItemDecoration(divider);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void clickOnItem(Object content, EntityType type) {
        switch (type) {
            case EMAIL:
                Email email = (Email) content;
                lastEmailLength = email.getEmailAddress().length();
                edit_text_send_email.setText(email.getEmailAddress());
                selectedEmail = email;

                if (email.getLinkedPhoneNumber() != null && email.getLinkedPhoneNumber().toString().length() == 10) {
                    findPhoneNumber(email.getLinkedPhoneNumber().toString(), false);
                }
                Log.d(App.TAG + "_SendDialog", "_initEmailRecyclerView -> callBackEmail: " + email.toString());
                break;

            case PHONE_NUMBER:
                PhoneNumber number = (PhoneNumber) content;
                edit_text_send_sms.setText(number.getNumber().toString());
                Log.d(App.TAG + "_SendDialog", "_initPhoneRecyclerView -> callBackPhone: " + number.toString());
                break;
        }
    }

    @Override
    public void delete(String phoneOrNumber, int adapterPosition, EntityType type) {
        switch (type) {
            case PHONE_NUMBER:
                Long phoneNumber = Long.parseLong(phoneOrNumber);
                new Thread(() -> {
                    App.getInstance().getDbHelper().getDataBase().receiptDao().deletePhoneNumber(phoneNumber);
                    for (PhoneNumber currentPhoneNumber : phoneNumberList) {
                        if (currentPhoneNumber.getNumber().equals(phoneNumber)) {
                            phoneNumberList.remove(currentPhoneNumber);
                            break;
                        }
                    }
                    requireActivity().runOnUiThread(() -> {
                        phoneAdapter.setItems(phoneNumberList, false);
                        phoneAdapter.notifyItemRemoved(adapterPosition);
                        updatePhoneList();
                    });
                    App.getInstance().getDbHelper().getDataBase().receiptDao().deleteEmailAssociatedWithPhone(phoneNumber);
                }).start();
                break;

            case EMAIL:
                new Thread(() -> {
                    App.getInstance().getDbHelper().getDataBase().receiptDao().deleteEmail(phoneOrNumber);
                    for (Email email : emailList) {
                        if (email.getEmailAddress().equals(phoneOrNumber)) {
                            emailList.remove(email);
                            break;
                        }
                    }
                    requireActivity().runOnUiThread(() -> {
                        emailAdapter.setItems(emailList, false);
                        emailAdapter.notifyItemRemoved(adapterPosition);
                        updateEmailList();
                    });
                }).start();
                break;
        }
    }

    @Override
    public void saveEditedEmail(long id, String editedEmail, int adapterPosition) {
        String correctlyEmail = null;
        correctlyEmail = emailCorrectlyChecker(editedEmail, false);
        String finalCorrectlyEmail = correctlyEmail;
        new Thread(() -> {
            if (finalCorrectlyEmail != null) {
                Email email = App.getInstance().getDbHelper().getDataBase().receiptDao().getEmailById(id);
                email.setEmailAddress(editedEmail);
                for (Email currentEmail : emailList) {
                    if (currentEmail.getId().equals(id)) {
                        currentEmail.setEmailAddress(editedEmail);
                    }
                }
                App.getInstance().getDbHelper().getDataBase().receiptDao().createEmail(email);
                requireActivity().runOnUiThread(() -> {
                    emailAdapter.setItems(emailList, false);
                    emailAdapter.notifyItemChanged(adapterPosition);
                });
            }
        }).start();
    }

    private void updatePhoneList() {
        if (phoneNumberList.isEmpty()) {
            showControlModule();
        }
    }

    private void updateEmailList() {
        if (emailList.isEmpty()) {
            showControlModule();
        }
    }

    private void initColor() {
        clearIconColorFilter(true, true);
        int currentTheme = SessionPresenter.getInstance().getCurrentTheme();
        if (currentTheme == SessionPresenter.THEME_LIGHT) {
            title_send_dialog.setTextColor(ContextCompat.getColor(title_send_dialog.getContext(), R.color.fonts_lt));
            subtitle_send_dialog.setTextColor(ContextCompat.getColor(subtitle_send_dialog.getContext(), R.color.active_fonts_lt));
            title_save_contact.setTextColor(ContextCompat.getColor(title_save_contact.getContext(), R.color.fonts_lt));
            title_send_email.setTextColor(ContextCompat.getColor(title_send_email.getContext(), R.color.fonts_lt));
            title_send_sms.setTextColor(ContextCompat.getColor(title_send_sms.getContext(), R.color.fonts_lt));
            title_button_send.setTextColor(ContextCompat.getColor(title_button_send.getContext(), R.color.active_fonts_lt));
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
            title_button_send.setTextColor(ContextCompat.getColor(title_button_send.getContext(), R.color.active_fonts_dt));
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

    private void clearIconColorFilter(boolean phoneNumber, boolean email) {
        int currentTheme = SessionPresenter.getInstance().getCurrentTheme();
        int iconColor;
        if (currentTheme == SessionPresenter.THEME_LIGHT) {
            iconColor = ContextCompat.getColor(icon_send_sms.getContext(), R.color.active_fonts_lt);
        } else {
            iconColor = ContextCompat.getColor(icon_send_sms.getContext(), R.color.active_fonts_dt);
        }

        if (phoneNumber) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                icon_send_sms.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));
            } else {
                icon_send_sms.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            }
        }

        if (email) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                icon_send_email.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));
            } else {
                icon_send_email.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            }
        }

    }

    private void setIconColorFilter(boolean phoneNumber, boolean email) {
        int currentTheme = SessionPresenter.getInstance().getCurrentTheme();
        int iconColor;
        if (currentTheme == SessionPresenter.THEME_LIGHT) {
            iconColor = ContextCompat.getColor(icon_send_sms.getContext(), R.color.icon_lt);
        } else {
            iconColor = ContextCompat.getColor(icon_send_sms.getContext(), R.color.icon_dt);
        }

        if (phoneNumber) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                icon_send_sms.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));
            } else {
                icon_send_sms.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            }
        }

        if (email) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                icon_send_email.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));
            } else {
                icon_send_email.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            }
        }

    }

    private void checkPossibilitySending() {
        // перекршиваем кнопку убираем листенер
        int currentTheme = SessionPresenter.getInstance().getCurrentTheme();
        int textColor;
        if (currentTheme == SessionPresenter.THEME_LIGHT) {
            textColor = ContextCompat.getColor(title_button_send.getContext(), R.color.active_fonts_lt);
        } else {
            textColor = ContextCompat.getColor(title_button_send.getContext(), R.color.active_fonts_dt);
        }
        title_button_send.setTextColor(textColor);
        button_send.setOnClickListener(v -> {
        });
        int activeButtonTextColor = ContextCompat.getColor(title_button_send.getContext(), R.color.icon_lt);
        if (selectedPhoneNumber != null && dialog_send_sms_switch.isChecked()) {
            //перекрашиваем кнопку вешаем листенер
            title_button_send.setTextColor(activeButtonTextColor);
            setSendButtonListener();
            return;
        }
        if (emailCorrectlyChecker(edit_text_send_email.getText().toString(), false) != null && dialog_send_email_switch.isChecked()) {
            //перекрашиваем кнопку вешаем листенер
            title_button_send.setTextColor(activeButtonTextColor);
            setSendButtonListener();
            return;
        }
    }

    private void setSendButtonListener() {
        button_send.setOnClickListener(v -> {
            PartialEvoReceiptSvDbUpdate receipt = new PartialEvoReceiptSvDbUpdate(evoUuid);
            /**
             * Шаг 1. формируем selectedPhoneNumber and selectedEmail
             */
            if (edit_text_send_sms.getRawText().length() == 10) {
                if (selectedPhoneNumber != null) {
                    receipt.setSv_sent_sms(true);
                    receipt.setSoft_village_processed(true);
                    if (!selectedPhoneNumber.getNumber().equals(Long.valueOf(edit_text_send_sms.getRawText()))) {
                        selectedPhoneNumber = null;
                        receipt.setSv_sent_sms(false);
                    }
                } else {
                    phoneSetter(edit_text_send_sms.getRawText());
                    receipt.setSv_sent_sms(true);
                    receipt.setSoft_village_processed(true);
                }
            }

            if (!TextUtils.isEmpty(edit_text_send_email.getText())) {
                if (selectedEmail != null) {
                    receipt.setSv_sent_email(true);
                    receipt.setSoft_village_processed(true);
                    if (!selectedEmail.getEmailAddress().equals(edit_text_send_email.getText().toString())) {
                        selectedEmail = null;
                        receipt.setSv_sent_email(false);
                    }
                } else {
                    String emailAddress = emailCorrectlyChecker(edit_text_send_email.getText().toString(), false);
                    if (emailAddress != null) {
                        Email newEmail = new Email();
                        newEmail.setEmailAddress(emailAddress);
                        if (selectedPhoneNumber != null)
                            newEmail.setLinkedPhoneNumber(selectedPhoneNumber.getNumber());
                        selectedEmail = newEmail;
                        receipt.setSv_sent_email(true);
                        receipt.setSoft_village_processed(true);
                    }
                }
            }

            /**
             * Шаг 2. Выполняем проверку переключателей
             */
            if (dialog_save_switch.isChecked()) {
                if (selectedPhoneNumber != null) {
                    App.getInstance().getDbHelper().createOrReplacePhone(selectedPhoneNumber);
                }
                if (selectedEmail != null) {
                    App.getInstance().getDbHelper().createOrUpdateEmail(selectedEmail);
                }
            }


            /**
             * Шаг 3. Отправляем
             */
            SentEntity entity = new SentEntity();
            entity.setEvoUuid(evoUuid);
            if (needSendSms && selectedPhoneNumber != null) {
                entity.setPhoneNumber(selectedPhoneNumber.getNumber());
            }
            if (needSendEmail && selectedEmail != null) {
                entity.setEmail(selectedEmail.getEmailAddress());
            }

            /**
             * Если чек не фискализирован (окно продажи)
             */
            // Добавление в очередь ожидания фискализации.
            if (TextUtils.isEmpty(receiptNumber) && ReceiptDetailFragment.NOT_FISCALIZED_RECEIPT_DATE.toString("dd.MM.yyyy HH:mm:ss").equals(receiptDate)) {
                new Thread(() -> {
                    NotFiscalizedReceipt notFiscalizedEntity = new NotFiscalizedReceipt(evoUuid);
                    notFiscalizedEntity.setPhoneNumber(entity.getPhoneNumber() != null ? entity.getPhoneNumber().toString() : null);
                    notFiscalizedEntity.setEmail(entity.getEmail());
                    App.getInstance().getDbHelper().getDataBase().receiptDao().addNotFiscalizedReceipt(notFiscalizedEntity);
                }).start();

                /**
                 * Если чек фискализирован (отправка из приложения mailer)
                 */
            } else { // отправка фискализированного чека

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * Выполняем сохранение сущьности в БД для отпрвки на backend.
                         */
                        App.getInstance().getDbHelper().getDataBase().receiptDao().addToQueueToSend(entity);


                        /**
                         * Запуск фоновой службы формирования очередей и отправки на сервер.
                         */
                        if (!isMyServiceRunning(SendToBackendService.class)) {
                            Intent startIntent = new Intent(App.getInstance().getApplicationContext(), SendToBackendService.class);
                            startIntent.setAction("start");
                            App.getInstance().startService(startIntent);

                        /*Toast.makeText(getApplicationContext(), "Service sender already running", Toast.LENGTH_SHORT).show();
                        return;*/
                        }

                        App.getInstance().getDbHelper().getDataBase().receiptDao().updateEvoReceipt(receipt);

                        /**
                         * Прерываем тред.
                         */
//                    Thread.currentThread().interrupt();
                    }
                }).start();
            }

            /**
             * Шаг 4. Закрываем окно.
             */
            dismiss(); //Закрываем диалог
            requireActivity().onBackPressed(); //Закрываем ReceiptDetail
        });
    }
}