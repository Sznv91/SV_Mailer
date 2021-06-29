package ru.softvillage.mailer_test.ui.viewModel;

import androidx.lifecycle.ViewModel;


public class ReceiptDetailViewModel extends ViewModel {
    /*private String receiptCloudId;
    private PositionGoodsItemAdapter adapter;
    @Getter
    Receipt receipt = null;

    Observer<ReceiptWithGoodEntity> observer = receipt -> {
        String receiptUuid = receipt.getReceiptEntity().getUuid();
        this.receipt = ReceiptApi.getReceipt(EvoApp.getInstance().getApplicationContext(), receiptUuid);
        updateAdapter();
    };

    public void setReceiptCloudId(String receiptCloudId) {
        this.receiptCloudId = receiptCloudId;
        EvoApp.getInstance().getDbHelper().getReceiptWithGoodEntity(Long.parseLong(receiptCloudId)).observeForever(observer);
    }

    private void updateAdapter() {
        adapter.setItems(receipt);
    }


    public PositionGoodsItemAdapter getAdapter() {
        adapter = new PositionGoodsItemAdapter(LayoutInflater.from(EvoApp.getInstance().getApplicationContext()));
        return adapter;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        EvoApp.getInstance().getDbHelper().getReceiptWithGoodEntity(Long.parseLong(receiptCloudId)).removeObserver(observer);
    }*/
}
