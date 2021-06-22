package ru.softvillage.mailer_test.ui.fragmet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ru.softvillage.mailer_test.R;

public class SendReceipt extends Fragment {

    private static final String ARG_PARAM1 = "startPositionOnRecyclerView";
    private int startPositionOnRecyclerView;

    public SendReceipt() {
        // Required empty public constructor
    }

    public static SendReceipt newInstance(int startPositionOnRecyclerView) {
        SendReceipt fragment = new SendReceipt();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, startPositionOnRecyclerView);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            startPositionOnRecyclerView = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_receipt, container, false);
    }
}


