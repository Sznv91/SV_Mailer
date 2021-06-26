package ru.softvillage.mailer_test.ui.fragmet;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import ru.softvillage.mailer_test.R;
import ru.softvillage.mailer_test.presetner.SessionPresenter;
import ru.softvillage.mailer_test.ui.recyclerView.ReceiptItemAdapter;
import ru.softvillage.mailer_test.ui.viewModel.AllReceiptViewModel;

public class AllReceipt extends Fragment {
    private AllReceiptViewModel mViewModel;
    private FloatingActionButton fab;
    private ConstraintLayout receipt_all_layout_fragment;

    public static AllReceipt newInstance() {
        return new AllReceipt();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(AllReceiptViewModel.class);
        mViewModel.setContext(getContext());

        return inflater.inflate(R.layout.fragment_all_receipt, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.setContext(null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        SessionPresenter.getInstance().getDrawerManager().showUpButton(false);
        receipt_all_layout_fragment = view.findViewById(R.id.receipt_all_layout_fragment);
        fab = view.findViewById(R.id.fab_up);
        fab.hide();
        SessionPresenter.getInstance().getCurrentThemeLiveData().observe(getViewLifecycleOwner(), currentTheme -> {
            if (currentTheme == SessionPresenter.THEME_LIGHT) {
                receipt_all_layout_fragment.setBackgroundColor(ContextCompat.getColor(receipt_all_layout_fragment.getContext(), R.color.divider_lt));
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(receipt_all_layout_fragment.getContext(), R.color.header_lt)));
            } else {
                receipt_all_layout_fragment.setBackgroundColor(ContextCompat.getColor(receipt_all_layout_fragment.getContext(), R.color.divider_dt));
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(receipt_all_layout_fragment.getContext(), R.color.switcher_dt)));
            }
        });
        initRecyclerView();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mViewModel.setContext(getContext());
//        SessionPresenter.getInstance().getDrawerManager().showUpButton(false);
    }

    ///////////////////////////////////////////////////////////////////////////////////

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
}