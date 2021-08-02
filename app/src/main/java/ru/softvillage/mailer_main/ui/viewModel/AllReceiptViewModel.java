package ru.softvillage.mailer_main.ui.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import ru.softvillage.mailer_main.App;
import ru.softvillage.mailer_main.R;
import ru.softvillage.mailer_main.dataBase.entity.EvoReceipt;
import ru.softvillage.mailer_main.presetner.SessionPresenter;
import ru.softvillage.mailer_main.ui.fragmet.IFragmentWithList;
import ru.softvillage.mailer_main.ui.fragmet.ReceiptDetailFragment;
import ru.softvillage.mailer_main.ui.recyclerView.ReceiptItemAdapter;

/**
 * https://question-it.com/questions/835512/android-studio-otkljuchit-dni-ot-vybora-daty
 * Ограничение выбора дат в календаре
 */
public class AllReceiptViewModel extends ViewModel {
    private IFragmentWithList fragmentWithList;
    private LinearLayoutManager layoutManager;
    Application app = App.getInstance();
    ReceiptItemAdapter adapter;
    List<EvoReceipt> localCopyReceiptEntityListWithDate = new ArrayList<>();
    private boolean isSvProcessed = false;

    public void setSvProcessed(boolean isProcessed){
        isSvProcessed = isProcessed;
    }


    @SuppressLint("LongLogTag")
    Observer<List<EvoReceipt>> observer = receiptEntities -> {
        List<EvoReceipt> goodEntityListWithDate = injectDateEntity(receiptEntities);
        localCopyReceiptEntityListWithDate = goodEntityListWithDate;
        adapter.setItems(goodEntityListWithDate);
        if (receiptEntities.size() > 0 && fragmentWithList != null) {
            fragmentWithList.hideEmptyListStab();
        }
    };

    public void setFragmentWithList(IFragmentWithList fragment) {
        this.fragmentWithList = fragment;
    }

    public void setLinearLayoutManager(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @SuppressLint("LongLogTag")
    public ReceiptItemAdapter getAdapter() {
        if (adapter == null) {
            adapter = new ReceiptItemAdapter(LayoutInflater.from(app.getApplicationContext()),
                    new ReceiptItemAdapter.itemClickInterface() {
                        @Override
                        public void clickClick(EvoReceipt recipientEntity) {
                            /**
                             * Проверка свободен ли FragmentManager
                             */
                            if (!SessionPresenter.getInstance().isFragmentBusy()) {
                                Log.d(App.TAG + "_Recycler", "click - click " + recipientEntity.getEvo_receipt_number());
                                Fragment fragment = ReceiptDetailFragment.newInstance(String.valueOf(recipientEntity.getEvo_uuid()));
                                if (App.getInstance().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    App.getInstance().getFragmentDispatcher().replaceFragment(fragment);
                                } else if (App.getInstance().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    TextView title_receipts = fragmentWithList.getActivity().findViewById(R.id.title_receipts);
                                    title_receipts.setText("Детали чека");
                                    int count = App.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager().getBackStackEntryCount();
                                    Log.d(App.TAG + "_backStack", "Count back stack is: " + count);
                                    if (count == 0) {
                                        App.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.holder_receipts, fragment).addToBackStack(String.valueOf(fragment.getId())).commit();
                                    } else {
                                        App.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.holder_receipts, fragment).commit();
                                    }
                                }
                            }
                        }

                        @Override
                        public void pushOnDate(LocalDateTime date) {
                            Log.d(App.TAG + "_date_splitter", "Нажали на разделитель даты. Дата: " + date.toString());
                            MaterialPickerOnPositiveButtonClickListener<Long> selectListener = new MaterialPickerOnPositiveButtonClickListener<Long>() {
                                @Override
                                public void onPositiveButtonClick(Long selection) {
                                    DateTime dateTime = new DateTime(selection);
                                    LocalDateTime pickDate = dateTime.toLocalDateTime();
                                    pickDate.withTime(0, 0, 0, 0);
                                    int position = positionSearcher(localCopyReceiptEntityListWithDate, pickDate);
                                    if (position != -1) {
                                        Log.d(App.TAG + "_date_splitter", "Перемещаемся к позиции: " + position);
                                        layoutManager.scrollToPositionWithOffset(position, 0);
                                    }
                                }
                            };
                            getDatePickerDialog(selectListener, date);
                        }
                    });
            observeOnChangeDb();
        }
        return adapter;
    }

    private void observeOnChangeDb() {
        if (isSvProcessed){
            App.getInstance().getDbHelper().getAllEvoReceiptSendLiveData().observeForever(observer);
        } else {
            App.getInstance().getDbHelper().getAllEvoReceiptLiveData().observeForever(observer);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCleared() {
        Log.d(App.TAG + "_LifeCycle", "Отписались от LiveData");
        App.getInstance().getDbHelper().getAllEvoReceiptLiveData().removeObserver(observer);
        super.onCleared();
    }


    private List<EvoReceipt> injectDateEntity(List<EvoReceipt> receiptEntities) {
        if (receiptEntities.size() == 0) {
            return receiptEntities;
        }
        List<EvoReceipt> result = new ArrayList<>();
        LocalDate lastProcessedDate = null;
        for (int i = 0; i < receiptEntities.size(); i++) {
            if (receiptEntities.get(i).getDate_time().toLocalDate().equals(lastProcessedDate)) {
                EvoReceipt toAdd = receiptEntities.get(i);
                if (!isSvProcessed) toAdd.setSoft_village_processed(false);
                result.add(toAdd);
            } else {
                lastProcessedDate = receiptEntities.get(i).getDate_time().toLocalDate();
                EvoReceipt dateSplitter = new EvoReceipt();
                dateSplitter.setEvo_uuid(ReceiptItemAdapter.DATE_SPLITTER_NAME);
                LocalDateTime splitDate = receiptEntities.get(i).getDate_time().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
                dateSplitter.setDate_time(splitDate);
                result.add(dateSplitter);
                EvoReceipt toAdd = receiptEntities.get(i);
                if (!isSvProcessed) toAdd.setSoft_village_processed(false);
                result.add(toAdd);
            }
        }
        return result;
    }

    private int positionSearcher(List<EvoReceipt> localCopyReceiptEntityListWithDate, LocalDateTime pickDate) {
        int result = -1;
        for (int i = 0; i < localCopyReceiptEntityListWithDate.size(); i++) {
            if (localCopyReceiptEntityListWithDate.get(i).getDate_time().toLocalDate()
                    .equals(pickDate.toLocalDate())) {
                result = i;
                break;
            }
        }
        return result;
    }

    private void getDatePickerDialog(MaterialPickerOnPositiveButtonClickListener<Long> listener, LocalDateTime selectedDate) {
        MaterialDatePicker.Builder<Long> calendarDatePicker = MaterialDatePicker.Builder.datePicker();
        calendarDatePicker.setCalendarConstraints(calendarDisableConstraints().build());
        calendarDatePicker.setTitleText("Даты печати чеков");
        LocalDateTime ldt = selectedDate.plusDays(1);
        calendarDatePicker.setSelection(ldt.toDateTime().getMillis());
        MaterialDatePicker<Long> pickerRange = calendarDatePicker.build();
        pickerRange.addOnPositiveButtonClickListener(listener);
        pickerRange.show(App.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager(), pickerRange.toString());
    }

    private CalendarConstraints.Builder calendarDisableConstraints() {
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
        constraintsBuilderRange.setValidator(new EnableDateValidator());
        return constraintsBuilderRange;
    }

    @SuppressLint("ParcelCreator")
    static class EnableDateValidator implements CalendarConstraints.DateValidator {

        @Override
        public boolean isValid(long date) {
            DateTime dateTime = new DateTime(date);
            LocalDate localDate = LocalDate.fromDateFields(dateTime.toDate());
            return App.getInstance().getDbHelper().getUniqueDate().contains(localDate);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }
    }
}
