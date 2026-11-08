package com.cooperativa.model.dao.impl;

import com.cooperativa.config.DatabaseConfig;
import com.cooperativa.model.dao.CreditoDAO;
import com.cooperativa.model.entity.Credito;
import com.cooperativa.model.entity.CuotaAmortizacion;
import com.cooperativa.model.entity.SolicitudCredito;
import com.cooperativa.util.FormatterUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreditoDAOImpl  implements  CreditoDAO{

    @override
    public boolean poseeCreditosActivos(int idSocio) throws SQLExeptio{
        String sql ="SELECT COUNT(*) FROM creditos WHERE id_socio = ? AND estado_activo = 'VIGEMTE'";
        try (Connection conn= DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSocio);
            try (ResulSet rs = ps.executeQuery()){
                return rs.next() && rs.getInt(1) > 0;
            }
     @override
     public String cosultarEstadoEquifax(int idSocio) throw  SQLExeption;
            String sql= "SELECT score_crediticio, historico_moras FROM referencias "
        }

    }
}