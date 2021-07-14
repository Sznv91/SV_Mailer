package ru.softvillage.mailer_test.ui.fragmet;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.presetner.SessionPresenter;
import ru.softvillage.mailer_test.ui.recyclerView.ReceiptItemAdapter;
import ru.softvillage.mailer_test.ui.viewModel.AllReceiptViewModel;

public class SendReceipt extends Fragment implements IFragmentWithList {

    private AllReceiptViewModel mViewModel;
    private FloatingActionButton fab;
    private ConstraintLayout receipt_all_layout_fragment;
    private FrameLayout layout_empty_receipt_list;
    private TextView title_empty_receipt_list;

    public static SendReceipt newInstance() {
        return new SendReceipt();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(SendReceipt.this).get(AllReceiptViewModel.class);
        mViewModel.setSvProcessed(true);
        mViewModel.setFragmentWithList(this);
        App.getInstance().getDbHelper().getUniqueDate().size(); // Первичная инициализация доступных дат

        return inflater.inflate(R.layout.fragment_send_receipt, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.setFragmentWithList(null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SessionPresenter.getInstance().getDrawerManager().showUpButton(false);
        layout_empty_receipt_list = view.findViewById(R.id.layout_empty_receipt_list);
        title_empty_receipt_list = view.findViewById(R.id.title_empty_receipt_list);
        receipt_all_layout_fragment = view.findViewById(R.id.receipt_all_layout_fragment);
        fab = view.findViewById(R.id.fab_up);
        fab.hide();
        SessionPresenter.getInstance().getCurrentThemeLiveData().observe(getViewLifecycleOwner(), currentTheme -> {
            if (currentTheme == SessionPresenter.THEME_LIGHT) {
                layout_empty_receipt_list.setBackgroundColor(ContextCompat.getColor(receipt_all_layout_fragment.getContext(), R.color.main_lt));
                title_empty_receipt_list.setTextColor(ContextCompat.getColor(title_empty_receipt_list.getContext(), R.color.active_fonts_lt));
                receipt_all_layout_fragment.setBackgroundColor(ContextCompat.getColor(receipt_all_layout_fragment.getContext(), R.color.divider_lt));
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(receipt_all_layout_fragment.getContext(), R.color.header_lt)));
            } else {
                layout_empty_receipt_list.setBackgroundColor(ContextCompat.getColor(receipt_all_layout_fragment.getContext(), R.color.main_dt));
                title_empty_receipt_list.setTextColor(ContextCompat.getColor(title_empty_receipt_list.getContext(), R.color.active_fonts_dt));
                receipt_all_layout_fragment.setBackgroundColor(ContextCompat.getColor(receipt_all_layout_fragment.getContext(), R.color.divider_dt));
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(receipt_all_layout_fragment.getContext(), R.color.switcher_dt)));
            }
        });
        initRecyclerView();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (mViewModel.getAdapter().getItemCount() > 0) {
            hideEmptyListStab();
        }
        SessionPresenter.getInstance().getDrawerManager().showUpButton(false);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecyclerView() {
        RecyclerView recycler = getView().findViewById(R.id.receipt_list_all_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        mViewModel.setLinearLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getDrawable(R.drawable.line_divider));
        recycler.addItemDecoration(divider);

        ReceiptItemAdapter adapter = mViewModel.getAdapter();
        recycler.setAdapter(adapter);


        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //https://stackoverflow.com/questions/33208613/hide-floatingactionbutton-on-scroll-of-recyclerview
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager.findFirstVisibleItemPosition() != 0) {
                    fab.show();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        fab.setOnClickListener(view -> {
            layoutManager.smoothScrollToPosition(recycler, new RecyclerView.State(), 0);
            fab.hide();
        });
    }

    @Override
    public void hideEmptyListStab() {
        layout_empty_receipt_list.setVisibility(View.INVISIBLE);
    }
}


