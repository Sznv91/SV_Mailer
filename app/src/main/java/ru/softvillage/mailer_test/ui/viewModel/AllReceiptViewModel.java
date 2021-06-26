package ru.softvillage.mailer_test.ui.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.DatePicker;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import ru.softvillage.mailer_test.App;
import ru.softvillage.mailer_test.dataBase.entity.EvoReceipt;
import ru.softvillage.mailer_test.ui.fragmet.AllReceipt;
import ru.softvillage.mailer_test.ui.recyclerView.ReceiptItemAdapter;

public class AllReceiptViewModel extends ViewModel {
    private Context context;
    private AllReceipt allReceiptFragment;
    private LinearLayoutManager layoutManager;
    Application app = App.getInstance();
    ReceiptItemAdapter adapter;
    List<EvoReceipt> localCopyReceiptEntityListWithDate = new ArrayList<>();


    @SuppressLint("LongLogTag")
    Observer<List<EvoReceipt>> observer = receiptEntities -> {
        List<EvoReceipt> goodEntityListWithDate = injectDateEntity(receiptEntities);
        localCopyReceiptEntityListWithDate = goodEntityListWithDate;
        adapter.setItems(goodEntityListWithDate);
        if (receiptEntities.size() > 0 && allReceiptFragment != null){
            allReceiptFragment.hideEmptyListStab();
        }
//        Log.d(App.TAG + "_Db", receiptEntities.toString());
    };

    public void setContext(Context context) {
        this.context = context;
    }

    public void setAllReceiptFragment (AllReceipt fragment){
        this.allReceiptFragment = fragment;
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
                             * Расчет Duration с момента последнего открытия фрагмена.
                             */
                           /* Duration durationLastOpenReceiptDetailFragment = new Duration(SessionPresenter.getInstance().getLastOpenReceiptDetailFragment().toDateTime(), LocalDateTime.now().toDateTime());
                            if (ReceiptDetailFragment.LOADER_TIME_SCREEN <= durationLastOpenReceiptDetailFragment.getMillis()) {
                                Log.d(EvoApp.TAG + "_Recycler", "click - click " + recipientEntity.getReceiptNumber());
                                Fragment fragment = ReceiptDetailFragment.newInstance(String.valueOf(recipientEntity.getSv_id()));
                                if (EvoApp.getInstance().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    EvoApp.getInstance().getFragmentDispatcher().replaceFragment(fragment);
                                } else if (EvoApp.getInstance().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    int count = EvoApp.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager().getBackStackEntryCount();
                                    Log.d(EvoApp.TAG + "_backStack", "Count back stack is: " + count);
                                    if (count == 0) {
                                        EvoApp.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.holder_statistic_fragment, fragment).addToBackStack(String.valueOf(fragment.getId())).commit();
                                    } else {
                                        EvoApp.getInstance().getFragmentDispatcher().getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.holder_statistic_fragment, fragment).commit();
                                    }
                                }
                            }*/
                        }

                        @Override
                        public void pushOnDate(LocalDateTime date) {
                            Log.d(App.TAG + "_date_splitter", "Нажали на разделитель даты. Дата: " + date.toString());
                            DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
                                @Override
                                /**
                                 *К месяцу прибавляем единицу. В Android счет месяцев ведется с 0
                                 */
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    LocalDateTime pickDate = LocalDateTime.parse(String.valueOf(year + "-"
                                            + (month + 1) + "-" +
                                            +dayOfMonth + "T00:00:00"));
                                    int position = positionSearcher(localCopyReceiptEntityListWithDate, pickDate);
                                    if (position != -1) {
                                        Log.d(App.TAG + "_date_splitter", "Перемещаемся к позиции: " + position);
                                        layoutManager.scrollToPositionWithOffset(position, 0);
                                    }
                                }
                            };

                            getDatePickerDialog(d, date);
                        }
                    });
            observeOnChangeDb();
        }
        return adapter;
    }

    private void observeOnChangeDb() {
        App.getInstance().getDbHelper().getAllEvoReceiptLiveData().observeForever(observer);
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
                result.add(receiptEntities.get(i));
            } else {
                lastProcessedDate = receiptEntities.get(i).getDate_time().toLocalDate();
                EvoReceipt dateSplitter = new EvoReceipt();
                dateSplitter.setEvo_uuid(ReceiptItemAdapter.DATE_SPLITTER_NAME);
                LocalDateTime splitDate = receiptEntities.get(i).getDate_time().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
                dateSplitter.setDate_time(splitDate);
                result.add(dateSplitter);
                result.add(receiptEntities.get(i));
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

    private void getDatePickerDialog(DatePickerDialog.OnDateSetListener d, LocalDateTime selectedDate) {
        new DatePickerDialog(context, d,
                selectedDate.getYear(),
                selectedDate.getMonthOfYear() - 1,
                selectedDate.getDayOfMonth())
                .show();
    }
}
