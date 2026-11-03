package com.cooperativa.config;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AppConstants {

    private AppConstants(){}

    public static final int DECIMAL_SCALE = 2;
    public  static final int CALCULATION_SCALE = 8;
    public static  final RoundingMode BANKING_ROUNDING = RoudingMode.HALF_EVEM;}

  //Reglas de negocio  Crediticias

    public static final BigDecimal MULTIPLICADOR_SALARIO_MAX = new BigDecimal("3.00"); // Tope 3x salario
    public static final BigDecimal LTV_HIPOTECA_MAX = new BigDecimal("0.60");          // 60% del valúo hipotecario

    // Tasas Escalonadas Anuales (según monto)
    public static final BigDecimal TASA_MICROCREDITO = new BigDecimal("0.1800");       // 18% hasta $3,000
    public static final BigDecimal TASA_CONSUMO = new BigDecimal("0.1400");            // 14% de $3,000.01 a $10,000
    public static final BigDecimal TASA_HIPOTECARIA = new BigDecimal("0.0950");        // 9.5% mayor a $10,000
    public static final BigDecimal TASA_MORA_ANUAL = new BigDecimal("0.0500");         // 5% recargo por mora

    // Prevención de Lavado de Dinero (UIF El Salvador)
    public static final BigDecimal UMBRAL_UIF_EFECTIVO = new BigDecimal("10000.00");   // >= $10,000.00 USD

    // Cuentas Contables Base INSAFOCOOP (IDs de referencia)

    public static final int CUENTA_CAJA_GENERAL_ID = 1;
    public static final int CUENTA_CARTERA_CREDITOS_ID = 2;
    public static final int CUENTA_INTERESES_DEVENGADOS_ID = 3;
    public static final int CUENTA_INTERESES_MORATORIOS_ID = 4;
    public static final int CUENTA_GASTOS_RECUPERADOS_ID = 5;

    //Rutas de Vistas FXML
    public static final String VIEW_LOGIN="/views/LoginView.fxml";
    public static final String VIEW_MAIN_LAYOUT = "/views/MainLayout.fxml";
    public static final String VIEW_SOCIO = "/views/SocioView.fxml";
    public static final String VIEW_SIMULADOR = "/views/CreditoSimuladorView.fxml";
    public static final String VIEW_DICTAMEN = "/views/DictamenComiteView.fxml";
    public static final String VIEW_CAJA = "/views/VentanillaCajaView.fxml";
    public static final String VIEW_CONTABILIDAD = "/views/ContabilidadView.fxml";
    public static final String VIEW_AUDITORIA = "/views/AuditoriaView.fxml";
    }

