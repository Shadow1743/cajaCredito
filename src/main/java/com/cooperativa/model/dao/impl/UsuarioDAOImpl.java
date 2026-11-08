package com.cooperativa.model.dao.impl;

import com.cooperativa.config.DatabaseConfig;
import com.cooperativa.model.dao.UsuarioDAO;
import com.cooperativa.model.entity.Rol;
import com.cooperativa.model.entity.Usuario;
import com.cooperativa.security.PasswordHasher;

import java.sql.*;
import java.util.Optional;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Optional<Usuario> autenticarPorUsernameODui(String identificador, String passwordPlano) throws SQLException {
        String sql = """
            SELECT u.id_usuario, u.username, u.password_hash, u.nombre_completo, u.estado, r.nombre_rol
            FROM usuarios u
            INNER JOIN roles r ON u.id_rol = r.id_rol
            LEFT JOIN socios s ON s.id_usuario = u.id_usuario
            WHERE (u.username = ? OR u.dui = ? OR s.dui = ?) AND u.estado = 'ACTIVO'
            LIMIT 1
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identificador.trim());
            ps.setString(2, identificador.trim());
            ps.setString(3, identificador.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (PasswordHasher.verifyPassword(passwordPlano, storedHash)) {
                        Usuario u = new Usuario();
                        u.setIdUsuario(rs.getInt("id_usuario"));
                        u.setUsername(rs.getString("username"));
                        u.setNombreCompleto(rs.getString("nombre_completo"));
                        u.setActivo("ACTIVO".equalsIgnoreCase(rs.getString("estado")));
                        u.setRol(mapearRol(rs.getString("nombre_rol")));
                        return Optional.of(u);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public int registrarUsuario(String nombres, String apellidos, String dui, String correo, int idRol, String passwordTemporal) throws SQLException {
        String sql = """
            INSERT INTO usuarios (id_rol, username, dui, password_hash, nombre_completo, correo, estado)
            VALUES (?, ?, ?, ?, ?, ?, 'ACTIVO')
            """;
        String usernameGenerado = correo.contains("@") ? correo.split("@")[0].toLowerCase() : dui;
        String nombreCompleto = (nombres.trim() + " " + apellidos.trim()).trim();
        String hashSha256 = PasswordHasher.hashSHA256(passwordTemporal);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idRol);
            ps.setString(2, usernameGenerado);
            ps.setString(3, dui.trim());
            ps.setString(4, hashSha256);
            ps.setString(5, nombreCompleto);
            ps.setString(6, correo.trim());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    private Rol mapearRol(String nombreRolBD) {
        if ("ASESOR_CREDITO".equalsIgnoreCase(nombreRolBD)) {
            return Rol.OFICIAL_CREDITO;
        }
        try {
            return Rol.valueOf(nombreRolBD.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Rol.CAJERO;
        }
    }
}