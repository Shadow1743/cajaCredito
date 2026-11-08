package com.cooperativa.dao;

import com.cooperativa.config.DatabaseConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    @DisplayName("Conexión JDBC: Debe establecer conexión activa con MySQL / MariaDB")
    void debeConectarABaseDeDatos() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection()) {
            assertNotNull(conn, "La conexión JDBC no debe ser nula");
            assertFalse(conn.isClosed(), "La conexión debe estar abierta");
        }
    }

    @Test
    @DisplayName("Catálogo y Roles: Debe verificar los 6 roles y las 7 cuentas contables de sistema_cajadecredito")
    void debeVerificarSemillasBaseDeDatos() throws Exception {
        String sqlRoles = "SELECT COUNT(*) FROM roles";
        String sqlCuentas = "SELECT COUNT(*) FROM cuentas_contables";
        String sqlParamUif = "SELECT valor FROM parametros_sistema WHERE clave_parametro = 'UMBRAL_LAVADO_DINERO'";

        try (Connection conn = DatabaseConfig.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlRoles);
                 ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(6, rs.getInt(1), "Deben existir exactamente los 6 roles RBAC en la base de datos");
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlCuentas);
                 ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertTrue(rs.getInt(1) >= 7, "Deben existir las 7 cuentas contables base del volcado SQL");
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlParamUif);
                 ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("10000.00", rs.getString("valor"), "El umbral UIF parametrizado debe ser 10000.00");
            }
        }
    }
}