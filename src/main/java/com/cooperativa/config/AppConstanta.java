package com.cooperativa.config;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AppConstants {

    private AppConstants() {}

    // Redondeo Bancario Estándar
    public static final int DECIMAL_SCALE = 2;
    public static final int CALCULATION_SCALE = 8;
    public static final RoundingMode BANKING_ROUNDING = RoundingMode.HALF_EVEN;

    // Reglas de Negocio y Parámetros del Sistema
    public static final BigDecimal TOPE_RCI_PORCENTAJE = new BigDecimal("0.40");      // 40% sobre sueldo neto
    public static final BigDecimal MULTIPLICADOR_SALARIO_MAX = new BigDecimal("3.00"); // 3x salario
    public static final BigDecimal LTV_HIPOTECA_MAX = new BigDecimal("0.60");          // 60% valúo hipoteca
    public static final BigDecimal UMBRAL_UIF_EFECTIVO = new BigDecimal("10000.00");   // >= $10,000.00 USD

    // Tasas Escalonadas Anuales
    public static final BigDecimal TASA_MICROCREDITO = new BigDecimal("0.1800");
    public static final BigDecimal TASA_CONSUMO = new BigDecimal("0.1400");
    public static final BigDecimal TASA_HIPOTECARIA = new BigDecimal("0.0950");
    public static final BigDecimal TASA_MORA_ANUAL = new BigDecimal("0.0500");

    // IDs de Cuentas Contables según volcado de sistema_cajadecredito.sql
    public static final int CUENTA_CAJA_GENERAL_ID = 1;           // 110101
    public static final int CUENTA_BANCOS_LOCALES_ID = 2;         // 110102
    public static final int CUENTA_CARTERA_CREDITOS_ID = 3;       // 110301
    public static final int CUENTA_PROVISION_INCOBRABLES_ID = 4;  // 110401
    public static final int CUENTA_GASTOS_PROVISION_ID = 5;       // 410201
    public static final int CUENTA_INGRESOS_INTERESES_ID = 6;     // 510101
    public static final int CUENTA_INGRESOS_MORA_ID = 7;          // 510102

    // Rutas de Vistas FXML
    public static final String VIEW_LOGIN = "/views/LoginView.fxml";
    public static final String VIEW_MAIN_LAYOUT = "/views/MainLayout.fxml";
    public static final String VIEW_REGISTRO_USUARIO = "/views/RegistroUsuarioView.fxml";
    public static final String VIEW_SOCIO = "/views/SocioView.fxml";
    public static final String VIEW_SIMULADOR = "/views/CreditoSimuladorView.fxml";
    public static final String VIEW_DICTAMEN = "/views/DictamenComiteView.fxml";
    public static final String VIEW_CAJA = "/views/VentanillaCajaView.fxml";
    public static final String VIEW_CONTABILIDAD = "/views/ContabilidadView.fxml";
    public static final String VIEW_AUDITORIA = "/views/AuditoriaView.fxml";
}