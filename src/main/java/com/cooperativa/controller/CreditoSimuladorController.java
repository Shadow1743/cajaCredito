package com.cooperativa.controller;

import com.cooperativa.model.dao.impl.CreditoDAOImpl;
import com.cooperativa.model.dao.impl.SocioDAOImpl;
import com.cooperativa.model.entity.ConsultaEquifax;
import com.cooperativa.model.entity.CuotaAmortizacion;
import com.cooperativa.model.entity.Socio;
import com.cooperativa.security.UserSession;
import com.cooperativa.service.CreditoService;
import com.cooperativa.service.EquifaxService;
import com.cooperativa.util.AlertUtil;
import com.cooperativa.util.FormatterUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CreditoSimuladorController {

    @FXML private TextField txtBuscarSocioDui;
    @FXML private TextField txtMontoSolicitado;
    @FXML private ComboBox<String> cmbTipoGarantia;
    @FXML private TextField txtPlazoMeses;
    @FXML private TextField txtTasaInteres;
    @FXML private ComboBox<String> cmbCreditosActivos;
    @FXML private TextField txtEstadoCentralRiesgo;
    @FXML private TextField txtDestinoCredito;

    private final SocioDAOImpl socioDAO = new SocioDAOImpl();
    private final CreditoDAOImpl creditoDAO = new CreditoDAOImpl();
    private final CreditoService creditoService = new CreditoService();
    private final EquifaxService equifaxService = new EquifaxService();

    private Socio socioSeleccionado;

    @FXML
    public void initialize() {
        cmbTipoGarantia.setItems(FXCollections.observableArrayList("FIADOR", "PRENDARIA", "HIPOTECARIA"));
        cmbTipoGarantia.getSelectionModel().selectFirst();
        cmbCreditosActivos.setItems(FXCollections.observableArrayList("No posee créditos activos", "Sí posee créditos activos"));
        cmbCreditosActivos.getSelectionModel().selectFirst();
    }

    @FXML
    private void validarSocioYEquifax() {
        try {
            Optional<Socio> opt = socioDAO.buscarPorDuiOCodigo(txtBuscarSocioDui.getText().trim());
            if (opt.isEmpty()) {
                AlertUtil.mostrarError("Socio no encontrado", "No existe un socio registrado con ese DUI o código.");
                return;
            }
            socioSeleccionado = opt.get();
            ConsultaEquifax buro = equifaxService.consultarHistorialCrediticio(socioSeleccionado, null);
            txtEstadoCentralRiesgo.setText("Score: " + buro.getScore() + " | Riesgo: " + buro.getCategoriaRiesgo() + " (" + buro.getDictamenSugerido() + ")");
        } catch (Exception e) {
            AlertUtil.mostrarError("Error al consultar Equifax", e.getMessage());
        }
    }

    @FXML
    private void registrarSolicitud() {
        if (socioSeleccionado == null) {
            AlertUtil.mostrarError("Validación requerida", "Primero busque y evalúe al socio por DUI.");
            return;
        }

        try {
            BigDecimal monto = new BigDecimal(txtMontoSolicitado.getText().trim());
            int plazo = Integer.parseInt(txtPlazoMeses.getText().trim());
            BigDecimal tasaPorcentaje = new BigDecimal(txtTasaInteres.getText().trim());
            BigDecimal tasaDecimal = tasaPorcentaje.divide(new BigDecimal("100"));

            // Validar Cuota vs RCI 40% del Ingreso Mensual Neto (según parametros_sistema)
            BigDecimal cuotaEstimada = creditoService.calcularCuotaNivelada(monto, tasaDecimal, plazo);
            BigDecimal topeCuotaRci = FormatterUtil.roundBanking(socioSeleccionado.getSalarioMensual().multiply(new BigDecimal("0.40")));

            if (cuotaEstimada.compareTo(topeCuotaRci) > 0) {
                AlertUtil.mostrarError("Capacidad de Pago Excedida (RCI > 40%)",
                        "La cuota mensual estimada (" + FormatterUtil.formatUSD(cuotaEstimada) +
                                ") supera el 40% del ingreso neto del socio (" + FormatterUtil.formatUSD(topeCuotaRci) + ").");
                return;
            }

            List<CuotaAmortizacion> tabla = creditoService.generarTablaAmortizacion(monto, tasaDecimal, plazo, LocalDate.now());
            String codigoCredito = "CRED-" + System.currentTimeMillis() % 100000;
            int idAsesor = UserSession.getInstance().getUsuarioActivo() != null
                    ? UserSession.getInstance().getUsuarioActivo().getIdUsuario() : 1;

            creditoDAO.registrarSolicitudCreditoACID(
                    codigoCredito,
                    socioSeleccionado.getIdSocio(),
                    idAsesor,
                    monto,
                    tasaPorcentaje,
                    new BigDecimal("5.00"),
                    plazo,
                    "FRANCESA",
                    txtDestinoCredito.getText().trim(),
                    cmbTipoGarantia.getValue(),
                    monto,
                    tabla
            );

            AlertUtil.mostrarInfo("Solicitud Registrada",
                    "Crédito " + codigoCredito + " creado con éxito.\nCuota Nivelada: " + FormatterUtil.formatUSD(cuotaEstimada));
        } catch (Exception e) {
            AlertUtil.mostrarError("Error al registrar solicitud", e.getMessage());
        }
    }
}