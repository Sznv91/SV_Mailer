package ru.softvillage.mailer_main.ui.recyclerView;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.TaxNumber;
import ru.softvillage.mailer_main.App;
import ru.softvillage.mailer_main.R;
import ru.softvillage.mailer_main.presetner.SessionPresenter;

public class PositionGoodsItemHolder extends RecyclerView.ViewHolder {
    private final TextView position_name,
            receipt_item_single_position,
            receipt_item_position_discount,
            receipt_item_position_cost,
            receipt_item_nds;

    public PositionGoodsItemHolder(@NonNull View itemView) {
        super(itemView);
        position_name = itemView.findViewById(R.id.position_name);
        receipt_item_single_position = itemView.findViewById(R.id.receipt_item_single_position);
        receipt_item_position_discount = itemView.findViewById(R.id.receipt_item_position_discount);
        receipt_item_position_cost = itemView.findViewById(R.id.receipt_item_position_cost);

        receipt_item_nds = itemView.findViewById(R.id.receipt_item_nds);
        initColor(SessionPresenter.getInstance().getCurrentTheme());
    }

    @SuppressLint("LongLogTag")
    public void bind(Position position, Receipt receipt) {
        position_name.setText(position.getName());
        BigDecimal totalDigit = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalPricePositionWithDiscount = BigDecimal.ZERO; //A

        for (Position position1 : receipt.getPositions()) {
            totalDigit = totalDigit.add(position1.getTotalWithoutDiscounts());
            totalDiscount = totalDiscount.add(position1.getDiscountPositionSum());
            totalPricePositionWithDiscount = totalPricePositionWithDiscount.add(position1.getTotal(BigDecimal.ZERO));
        }
        receipt_item_position_cost.setText(
                String.format(
                        App.getInstance().getString(R.string.receipt_item_position_cost)
                        , position.getTotalWithoutDiscounts())
                        .replace(",", ".")
        );

        if (!receipt.getDiscount().equals(BigDecimal.ZERO)) {
            /**
             * Для расчета процена скидки на весь чек:
             * сложить цену всех позийи с учетом скидки на позцию (A) ->
             * вычесть сумму итогового платежа (B) ->
             * цену всех позийи разедлить по получившееся значение (A/B)
             */
            Log.d(App.TAG + "position_discount", "цена всех позиций с учетом скидки на каждую позицию: " + totalPricePositionWithDiscount.toPlainString());
            BigDecimal onePercentTotalPrice = totalPricePositionWithDiscount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);


            BigDecimal percent = receipt.getDiscount().divide(onePercentTotalPrice, 2, RoundingMode.HALF_UP);
            // A/B
            Log.d(App.TAG + "position_discount", "процент скидки: " + percent.toPlainString());

            /**
             * Для расчета цены одной позици с учетом общей скидки:
             * Цена со скидкой на позицию (A)
             * Общая скидка на чек в % (B)
             * С = A-(A/100*B)
             */
            BigDecimal priceTotalPosition = position.getTotal(BigDecimal.ZERO).subtract(position.getTotal(BigDecimal.ZERO).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP).multiply(percent));
            BigDecimal tPriceTotalPosition = priceTotalPosition.setScale(2, RoundingMode.DOWN);
            Log.d(App.TAG + "position_discount", "цена одной позции с общей скидкой: " + priceTotalPosition.toPlainString());


            /**
             * Цена одной единицы товара
             * Количество товара (D)
             * Цена с учетом скидки на позицию и скидки по чеку (I)
             * F = I / D
             */
            BigDecimal pricePerOnePosition = priceTotalPosition.divide(position.getQuantity(), 6, RoundingMode.HALF_UP);
            BigDecimal tPricePerOnePosition = pricePerOnePosition.setScale(2, RoundingMode.DOWN);
            receipt_item_single_position.setText(String.format(App.getInstance().getString(R.string.receipt_item_single_position)
                    , position.getQuantity()
                    , tPricePerOnePosition
                    , tPriceTotalPosition)
                    .replace(",", "."));

            /**
             * Расчет скидки позции + общей скидки для отображения
             * От цены со скидкой отнимаем общий процент
             */
            BigDecimal discountBigDec = position.getPriceWithDiscountPosition().multiply(position.getQuantity()).subtract(tPriceTotalPosition).add(position.getDiscountPositionSum());
            receipt_item_position_discount.setText(String.format(App.getInstance().getString(R.string.receipt_item_position_discount), discountBigDec).replace(",", "."));

            BigDecimal nds_20 = tPriceTotalPosition.divide(BigDecimal.valueOf(1.2), 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.2))
                    .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal nds_10 = tPriceTotalPosition.divide(BigDecimal.valueOf(1.1), 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.1))
                    .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (position.getTaxNumber() != null && !position.getTaxNumber().equals(TaxNumber.NO_VAT)) {
                receipt_item_nds.setVisibility(View.VISIBLE);
            }


            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_0)) {
                receipt_item_nds.setText(String.format(App.getInstance().getString(R.string.receipt_item_nds)
                        , "0%"
                        , BigDecimal.ZERO)
                        .replace(",", "."));
            }

            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_18)) {
                receipt_item_nds.setText(String.format(App.getInstance().getString(R.string.receipt_item_nds)
                        , "20%"
                        , nds_20)
                        .replace(",", "."));
            }

            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_10)) {
                receipt_item_nds.setText(String.format(App.getInstance().getString(R.string.receipt_item_nds)
                        , "10%"
                        , nds_10)
                        .replace(",", "."));
            }
            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_18_118)) {
                receipt_item_nds.setText(String.format(App.getInstance().getString(R.string.receipt_item_nds)
                        , "20/120"
                        , nds_20)
                        .replace(",", "."));
            }
            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_10_110)) {
                receipt_item_nds.setText(String.format(App.getInstance().getString(R.string.receipt_item_nds)
                        , "10/110"
                        , nds_10)
                        .replace(",", "."));
            }


        } else {
            receipt_item_single_position.setText(String.format(App.getInstance().getString(R.string.receipt_item_single_position)
                    , position.getQuantity()
                    , position.getPriceWithDiscountPosition().setScale(2, RoundingMode.DOWN)
                    , position.getTotal(BigDecimal.ZERO))
                    .replace(",", "."));
            receipt_item_position_discount.setText(String.format(App.getInstance().getString(R.string.receipt_item_position_discount), position.getDiscountPositionSum()).replace(",", "."));

            BigDecimal nds_20 = position.getTotal(BigDecimal.ZERO).divide(BigDecimal.valueOf(1.2), 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.2))
                    .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal nds_10 = position.getTotal(BigDecimal.ZERO).divide(BigDecimal.valueOf(1.1), 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.1))
                    .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (position.getTaxNumber() != null && !position.getTaxNumber().equals(TaxNumber.NO_VAT)) {
                receipt_item_nds.setVisibility(View.VISIBLE);
            }


            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_0)) {
                receipt_item_nds.setText(String.format(App.getInstance().getString(R.string.receipt_item_nds)
                        , "0%"
                        , BigDecimal.ZERO)
                        .replace(",", "."));
            }

            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_18)) {
                receipt_item_nds.setText(String.format(App.getInstance().getString(R.string.receipt_item_nds)
                        , "20%"
                        , nds_20)
                        .replace(",", "."));
            }

            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_10)) {
                receipt_item_nds.setText(String.format(App.getInstance().getString(R.string.receipt_item_nds)
                        , "10%"
                        , nds_10)
                        .replace(",", "."));
            }
            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_18_118)) {
                receipt_item_nds.setText(String.format(App.getInstance().getString(R.string.receipt_item_nds)
                        , "20/120"
                        , nds_20)
                        .replace(",", "."));
            }
            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_10_110)) {
                receipt_item_nds.setText(String.format(App.getInstance().getString(R.string.receipt_item_nds)
                        , "10/110"
                        , nds_10)
                        .replace(",", "."));
            }
        }

        Log.d(App.TAG + "_total_discount_receipt.toString()", receipt.toString());
    }

    private void initColor(int currentTheme) {
        Drawable good_icon = ContextCompat.getDrawable(App.getInstance().getApplicationContext(), R.drawable.ic_baseline_receipt_24);
        if (currentTheme == SessionPresenter.THEME_LIGHT) {
            position_name.setTextColor(ContextCompat.getColor(position_name.getContext(), R.color.fonts_lt));
            good_icon.setColorFilter(ContextCompat.getColor(position_name.getContext(), R.color.active_fonts_lt), PorterDuff.Mode.SRC_IN);
            receipt_item_single_position.setTextColor(ContextCompat.getColor(receipt_item_single_position.getContext(), R.color.active_fonts_lt));
            receipt_item_position_discount.setTextColor(ContextCompat.getColor(receipt_item_position_discount.getContext(), R.color.active_fonts_lt));
            receipt_item_position_cost.setTextColor(ContextCompat.getColor(receipt_item_position_cost.getContext(), R.color.active_fonts_lt));
            receipt_item_nds.setTextColor(ContextCompat.getColor(receipt_item_nds.getContext(), R.color.active_fonts_lt));
        } else {
            position_name.setTextColor(ContextCompat.getColor(position_name.getContext(), R.color.fonts_dt));
            good_icon.setColorFilter(ContextCompat.getColor(position_name.getContext(), R.color.active_fonts_dt), PorterDuff.Mode.SRC_IN);
            receipt_item_single_position.setTextColor(ContextCompat.getColor(receipt_item_single_position.getContext(), R.color.active_fonts_dt));
            receipt_item_position_discount.setTextColor(ContextCompat.getColor(receipt_item_position_discount.getContext(), R.color.active_fonts_dt));
            receipt_item_position_cost.setTextColor(ContextCompat.getColor(receipt_item_position_cost.getContext(), R.color.active_fonts_dt));
            receipt_item_nds.setTextColor(ContextCompat.getColor(receipt_item_nds.getContext(), R.color.active_fonts_dt));
        }
        position_name.setCompoundDrawablesRelativeWithIntrinsicBounds(good_icon, null, null, null);

    }
}
