package com.cooperativa.controller;

import com.cooperativa.security.UserSession;
import com.cooperativa.service.ContabilidadService;
import com.cooperativa.service.PDFExportService;
import com.cooperativa.util.AlertUtil;
import com.cooperativa.util.FormatterUtil;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContabilidadController {

    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaCierre;
    @FXML private TextField txtTotalCartera;
    @FXML private TextField txtTotalRecaudado;
    @FXML private TextField txtDisponibleCaja;
    @FXML private TextField txtEstadoEjercicio;
    @FXML private TextField txtNotasAuditoria;

    private final ContabilidadService contabilidadService = new ContabilidadService();
    private final PDFExportService pdfExportService = new PDFExportService();

    private BigDecimal carteraActual = BigDecimal.ZERO;
    private BigDecimal recaudadoActual = BigDecimal.ZERO;
    private BigDecimal disponibleActual = BigDecimal.ZERO;

    @FXML
    public void initialize() {
        dpFechaInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dpFechaCierre.setValue(LocalDate.now());
        actualizarMetricasContables();
    }

    @FXML
    private void actualizarMetricasContables() {
        try {
            LocalDate inicio = dpFechaInicio.getValue();
            LocalDate fin = dpFechaCierre.getValue();

            carteraActual = contabilidadService.calcularCarteraColocada(inicio, fin);
            recaudadoActual = contabilidadService.calcularRecaudadoCobros(inicio, fin);
            disponibleActual = contabilidadService.calcularDisponibleCajaBancos();

            txtTotalCartera.setText(FormatterUtil.formatUSD(carteraActual));
            txtTotalRecaudado.setText(FormatterUtil.formatUSD(recaudadoActual));
            txtDisponibleCaja.setText(FormatterUtil.formatUSD(disponibleActual));
            txtEstadoEjercicio.setText("Cuadrado / Activo");
        } catch (Exception e) {
            AlertUtil.mostrarError("Error al calcular saldos contables", e.getMessage());
        }
    }

    @FXML
    private void generarReporteContable() {
        try {
            int idUsuario = UserSession.getInstance().getUsuarioActivo() != null
                    ? UserSession.getInstance().getUsuarioActivo().getIdUsuario() : 1;

            contabilidadService.ejecutarCierreContable(idUsuario, txtNotasAuditoria.getText().trim());

            String archivoDestino = "Reporte_Cierre_Contable_" + LocalDate.now() + ".pdf";
            pdfExportService.exportarReporteCierreContable(
                    archivoDestino,
                    dpFechaInicio.getValue(),
                    dpFechaCierre.getValue(),
                    carteraActual,
                    recaudadoActual,
                    disponibleActual,
                    txtEstadoEjercicio.getText(),
                    txtNotasAuditoria.getText()
            );

            AlertUtil.mostrarInfo("Cierre Contable Generado",
                    "Se registró la bitácora de auditoría y se exportó el archivo: " + archivoDestino);
        } catch (Exception e) {
            AlertUtil.mostrarError("Error al generar reporte", e.getMessage());
        }
    }
}Si