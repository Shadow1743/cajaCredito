package com.cooperativa.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class DateUtil{

    public final class DateUtil {

        private DateUtil() {}

            public static long calcularDiasMora (LocalDate fechaVencimiento, LocalDate fechaPago){
                if (fechaVencimiento == null || fechaPago == null || !fechaPago.isAfter(fechaVencimiento)) {
                    return 0L;
                }
                return ChronoUnit.DAYS.between(fechaVencimiento, fechaPago);
        }
    }
