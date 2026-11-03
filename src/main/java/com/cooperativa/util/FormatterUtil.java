package com.cooperativa.util;

import com.cooperativa.config.AppConstants;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class formatterUtil{

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyIntance(Locale.US);

    private FormatterUtil() {

        public static  BigDecimal roundBanking(BigDecimal value){
            if (value == null) {
                return BigDecimal.ZERO.setScale(AppConstants.DECIMAL_SCALE, AppConstants.BANKING_ROUDING);
            }
            return value.setScale(AppContants.DECIMNAL_SCALE, appConstans.BANKING_ROUNDING);
        }

        public static  String formatUSD(BigDecimal value) {
            return CURRENCY_FORMAT.format(roundBanking(value));
    }
}