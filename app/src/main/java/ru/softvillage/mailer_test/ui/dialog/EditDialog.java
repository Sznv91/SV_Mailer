package ru.softvillage.mailer_test.ui.dialog;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.presetner.SessionPresenter;


public class EditDialog extends DialogFragment {
    private static final String ARG_ID = "id";
    private static final String ARG_CONTENT = "content";
    private static final String ARG_POSITION = "adapter_position";

    private long id;
    private String contentText;
    private int position;
    private final IEditDialog callback;

    private EditText content_edit_dialog;
    private TextView title_edit_dialog,
            title_button_cancel,
            title_button_save;
    private View divider_edit_dialog_title,
            divider_edit_dialog_content;


    public EditDialog(IEditDialog callback) {
        this.callback = callback;
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

    public static EditDialog newInstance(long idInDb, String contentText, int adapterPosition, IEditDialog callBack) {
        EditDialog fragment = new EditDialog(callBack);
        Bundle args = new Bundle();
        args.putLong(ARG_ID, idInDb);
        args.putString(ARG_CONTENT, contentText);
        args.putInt(ARG_POSITION, adapterPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getLong(ARG_ID);
            contentText = getArguments().getString(ARG_CONTENT);
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title_edit_dialog = view.findViewById(R.id.title_edit_dialog);
        content_edit_dialog = view.findViewById(R.id.content_edit_dialog);
        title_button_cancel = view.findViewById(R.id.title_button_cancel);
        title_button_save = view.findViewById(R.id.title_button_save);

        divider_edit_dialog_title = view.findViewById(R.id.divider_edit_dialog_title);
        divider_edit_dialog_content = view.findViewById(R.id.divider_edit_dialog_content);

        initColor();
        initContent();
        initButton();
    }

    private void initContent() {
        title_edit_dialog.setText(getText(R.string.edit_type_email));
        content_edit_dialog.setText(contentText);
    }

    private void initButton() {
        title_button_cancel.setOnClickListener(v -> dismiss());
        title_button_save.setOnClickListener(v -> {
            callback.saveEditedEmail(id, content_edit_dialog.getText().toString(), position);
            dismiss();
        });
    }

    private void initColor() {
        if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
            title_edit_dialog.setTextColor(ContextCompat.getColor(title_edit_dialog.getContext(), R.color.fonts_lt));
            content_edit_dialog.setBackground(ContextCompat.getDrawable(content_edit_dialog.getContext(), R.drawable.edit_text_background_light));
            content_edit_dialog.setTextColor(ContextCompat.getColor(content_edit_dialog.getContext(), R.color.fonts_lt));
            divider_edit_dialog_title.setBackground(ContextCompat.getDrawable(divider_edit_dialog_title.getContext(), R.color.divider_lt));
            divider_edit_dialog_content.setBackground(ContextCompat.getDrawable(divider_edit_dialog_content.getContext(), R.color.divider_lt));
        } else {
            title_edit_dialog.setTextColor(ContextCompat.getColor(title_edit_dialog.getContext(), R.color.fonts_dt));
            content_edit_dialog.setBackground(ContextCompat.getDrawable(content_edit_dialog.getContext(), R.drawable.edit_text_background_dark));
            content_edit_dialog.setTextColor(ContextCompat.getColor(content_edit_dialog.getContext(), R.color.fonts_dt));
            divider_edit_dialog_title.setBackground(ContextCompat.getDrawable(divider_edit_dialog_title.getContext(), R.color.divider_dt));
            divider_edit_dialog_content.setBackground(ContextCompat.getDrawable(divider_edit_dialog_content.getContext(), R.color.divider_dt));
        }
    }

    public interface IEditDialog {
        void saveEditedEmail(long id, String editedEmail, int adapterPosition);
    }
}