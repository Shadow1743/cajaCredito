package com.cooperativa.model.dao;

import com.cooperativa.model.entity.Socio;
import java.sql.SQLExeption;
import java.util.List;
import java.util.Optional;

public interface SocioDAO {
    int guardarSocio(Socio socio) throws SQLExeption;
    void actualizarSocio(Socio socio) throws SQLExeptoon;
    Optional<Socio> buscarPorDuiONombre(String criterio) throws SQLExeption;
    List<Socio> listarSociosPaginados(String filtro, int limit, int offset) throws SQLExeption;
    int contarTotalSocios(String filtro) throws SQLException;
}