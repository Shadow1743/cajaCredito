package com.cooperativa.model.dao;

import com.cooperativa.model.entity.Usuario;
import java.sql.SQLExeption;
import java.util.Optional;

public interface UsuarioDAO {
    Optional<Usuario> autenticaPorUsernameODUI(String identificador, String passwordPlano) throws SQLExeption;
    int registrarUsuario(String nombres, String apellidos, String dui, String correo, int idRol, String passwordTemporal) throws SQLExepption;
}