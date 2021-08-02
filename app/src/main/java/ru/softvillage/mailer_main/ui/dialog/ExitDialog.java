package ru.softvillage.mailer_main.ui.dialog;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
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

import static android.view.Gravity.CENTER;

public class ExitDialog extends DialogFragment implements View.OnClickListener {

    ConstraintLayout root;

    private TextView title;
    private TextView cancel;
    private TextView close;


    public ExitDialog() {
    }

    public static ExitDialog newInstance() {
        Bundle args = new Bundle();

        ExitDialog fragment = new ExitDialog();
        fragment.setArguments(args);
        return fragment;
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

            if (window != null) {
                Point size = new Point();
                getActivity().getWindowManager().getDefaultDisplay().getSize(size);

                window.setLayout(size.x - getResources().getDimensionPixelSize(R.dimen.margin_72) * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawableResource(android.R.color.transparent);
                window.setGravity(CENTER);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_exit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = getView().findViewById(R.id.root_exit);
        title = getView().findViewById(R.id.title);
        cancel = getView().findViewById(R.id.cancel);
        close = getView().findViewById(R.id.close);

        cancel.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUITheme();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.close:
                getActivity().finish();
                break;
        }
    }

    private void updateUITheme() {
        if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
            root.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.bg_dialog));
            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.fonts_lt));
        } else {
            root.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.bg_dialog_black));
            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.fonts_dt));
        }
    }
}
