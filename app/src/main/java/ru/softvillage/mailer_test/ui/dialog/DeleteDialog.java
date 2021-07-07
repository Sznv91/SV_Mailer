package ru.softvillage.mailer_test.ui.dialog;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.presetner.SessionPresenter;


public class DeleteDialog extends DialogFragment {
    public static final String TYPE_PHONE = "phone";
    public static final String TYPE_EMAIL = "email";

    private static final String ARG_TYPE = "type";
    private static final String ARG_CONTENT = "content";

    private String type;
    private String contentText;

    private TextView title_delete_dialog,
            content_delete_dialog,
            title_button_cancel,
            title_button_delete;
    private View divider_delete_dialog_title,
            divider_delete_dialog_content;


    public DeleteDialog() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int width = Double.valueOf(getResources().getDisplayMetrics().widthPixels * 0.7).intValue();
            int height = Double.valueOf(getResources().getDisplayMetrics().heightPixels * 0.26).intValue();
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
    }

    public static DeleteDialog newInstance(String type, String contentText) {
        DeleteDialog fragment = new DeleteDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putString(ARG_CONTENT, contentText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
            contentText = getArguments().getString(ARG_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_delete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title_delete_dialog = view.findViewById(R.id.title_delete_dialog);
        content_delete_dialog = view.findViewById(R.id.content_delete_dialog);
        title_button_cancel = view.findViewById(R.id.title_button_cancel);
        title_button_delete = view.findViewById(R.id.title_button_delete);

        divider_delete_dialog_title = view.findViewById(R.id.divider_delete_dialog_title);
        divider_delete_dialog_content = view.findViewById(R.id.divider_delete_dialog_content);

        initColor();
        initContent();
    }

    private void initContent() {
        if (type.equals(TYPE_PHONE)) {
            title_delete_dialog.setText(getText(R.string.delete_type_phone));
            content_delete_dialog.setText(String.format("+7 (%s) %s-%s-%s", contentText.substring(0, 3), contentText.substring(3, 6), contentText.substring(6, 8), contentText.substring(8, 10)));
            Drawable phone = ContextCompat.getDrawable(content_delete_dialog.getContext(), R.drawable.ic_mailer_sended_sms);
            content_delete_dialog.setCompoundDrawablesRelativeWithIntrinsicBounds(phone, null, null, null);
            content_delete_dialog.setCompoundDrawablePadding(10);
        } else title_delete_dialog.setText(getText(R.string.delete_type_email));


    }

    private void initColor() {
        if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
            title_delete_dialog.setTextColor(ContextCompat.getColor(title_delete_dialog.getContext(), R.color.fonts_lt));
            content_delete_dialog.setTextColor(ContextCompat.getColor(title_delete_dialog.getContext(), R.color.active_fonts_lt));
            divider_delete_dialog_title.setBackground(ContextCompat.getDrawable(divider_delete_dialog_title.getContext(), R.color.divider_lt));
            divider_delete_dialog_content.setBackground(ContextCompat.getDrawable(divider_delete_dialog_content.getContext(), R.color.divider_lt));
        } else {
            title_delete_dialog.setTextColor(ContextCompat.getColor(title_delete_dialog.getContext(), R.color.fonts_dt));
            content_delete_dialog.setTextColor(ContextCompat.getColor(title_delete_dialog.getContext(), R.color.active_fonts_dt));
            divider_delete_dialog_title.setBackground(ContextCompat.getDrawable(divider_delete_dialog_title.getContext(), R.color.divider_dt));
            divider_delete_dialog_content.setBackground(ContextCompat.getDrawable(divider_delete_dialog_content.getContext(), R.color.divider_dt));
        }
    }
}