package com.cooperativa.service;

import com.cooperativa.config.AppConstants;
import com.cooperativa.model.entity.CuotaAmortizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreditoServiceTest {

    private CreditoService creditoService;

    @BeforeEach
    void setUp() {
        creditoService = new CreditoService();
    }

    @Test
    @DisplayName("Regla 3x Salario: Debe aprobar monto <= 3 veces el salario y rechazar excesos")
    void debeValidarTopeTresVecesSalario() {
        BigDecimal salarioSocio = new BigDecimal("850.00"); // Tope 3x = $2,550.00

        assertTrue(creditoService.validarTopeTresVecesSalario(salarioSocio, new BigDecimal("2500.00")),
                "Un monto de $2,500.00 está dentro del tope de $2,550.00");
        assertTrue(creditoService.validarTopeTresVecesSalario(salarioSocio, new BigDecimal("2550.00")),
                "El límite exacto de $2,550.00 debe ser válido");
        assertFalse(creditoService.validarTopeTresVecesSalario(salarioSocio, new BigDecimal("2550.01")),
                "Un centavo por encima de 3x salario debe ser rechazado");
    }

    @Test
    @DisplayName("Regla 60% Hipoteca: Debe permitir máximo el 60% del valúo del inmueble")
    void debeValidarCoberturaHipotecaria60Porciento() {
        BigDecimal valuoInmueble = new BigDecimal("50000.00"); // 60% = $30,000.00

        assertTrue(creditoService.validarCoberturaHipotecaria60(valuoInmueble, new BigDecimal("30000.00")));
        assertFalse(creditoService.validarCoberturaHipotecaria60(valuoInmueble, new BigDecimal("30000.01")));
        assertFalse(creditoService.validarCoberturaHipotecaria60(BigDecimal.ZERO, new BigDecimal("1000.00")));
    }

    @Test
    @DisplayName("Tasas Escalonadas: Debe asignar 18%, 14% o 9.5% según el tramo del monto")
    void debeAsignarTasaEscalonadaSegunMonto() {
        assertEquals(AppConstants.TASA_MICROCREDITO, creditoService.obtenerTasaEscalonada(new BigDecimal("2500.00")));
        assertEquals(AppConstants.TASA_CONSUMO, creditoService.obtenerTasaEscalonada(new BigDecimal("5000.00")));
        assertEquals(AppConstants.TASA_HIPOTECARIA, creditoService.obtenerTasaEscalonada(new BigDecimal("15000.00")));
    }

    @Test
    @DisplayName("Tabla de Amortización: La suma del capital programado debe cuadrar exactamente con el monto otorgado")
    void debeCuadrarCapitalTotalEnTablaAmortizacion() {
        BigDecimal montoPrestamo = new BigDecimal("2500.00");
        BigDecimal tasaAnual = new BigDecimal("0.1400"); // 14% anual
        int plazoMeses = 24;

        List<CuotaAmortizacion> plan = creditoService.generarTablaAmortizacion(
                montoPrestamo, tasaAnual, plazoMeses, LocalDate.of(2026, 11, 8)
        );

        assertEquals(24, plan.size());

        BigDecimal sumaCapital = plan.stream()
                .map(CuotaAmortizacion::getCapitalProgramado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(montoPrestamo, sumaCapital,
                "La suma de abonos a capital de las 24 cuotas debe ser exactamente $2,500.00 sin pérdida de centavos");
    }
}