package com.cooperativa.security;

import com.cooperativa.config.AppConstants;
import com.cooperativa.model.entity.Rol;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class RoleAccessFilter {

    private static final Map<String, Set<Rol>> PERMISOS_VISTA = Map.of(
            AppConstants.VIEW_REGISTRO_USUARIO, EnumSet.of(Rol.ROLE_ADMIN),
            AppConstants.VIEW_SOCIO, EnumSet.of(Rol.ROLE_ADMIN, Rol.ROLE_ASESOR, Rol.ROLE_GERENCIA),
            AppConstants.VIEW_SIMULADOR, EnumSet.of(Rol.ROLE_ADMIN, Rol.ROLE_ASESOR, Rol.ROLE_GERENCIA),
            AppConstants.VIEW_DICTAMEN, EnumSet.of(Rol.ROLE_ADMIN, Rol.ROLE_GERENCIA),
            AppConstants.VIEW_CAJA, EnumSet.of(Rol.ROLE_ADMIN, Rol.ROLE_CAJERO),
            AppConstants.VIEW_CONTABILIDAD, EnumSet.of(Rol.ROLE_ADMIN, Rol.ROLE_CONTADOR, Rol.ROLE_AUDITOR),
            AppConstants.VIEW_AUDITORIA, EnumSet.of(Rol.ROLE_ADMIN, Rol.ROLE_AUDITOR)
    );

    private RoleAccessFilter() {}

    public static boolean tienePermisoVista(String rutaVista) {
        Rol rolActual = UserSession.getInstance().getRolActivo();
        if (rolActual == null) {
            return false;
        }
        Set<Rol> rolesPermitidos = PERMISOS_VISTA.get(rutaVista);
        return rolesPermitidos != null && rolesPermitidos.contains(rolActual);
    }
}