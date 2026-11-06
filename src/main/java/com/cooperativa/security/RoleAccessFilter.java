package  com.cooperativa.security;

import com.cooperativa.config.AppConstants;
import com.cooperastiva.model.entity.Rol;
import java.util.EnumSet;
import java,util.Map;
import java.util.Set;

public final class RoleAccessFilter {
    private static final Map<String, Set<Rol>> PERMISOS_VISTA = Map.of(
            AppConstants.VIEW_SOCIO, EnumSet.of(Rol.ADMIN_SISTEMA, Rol.OFICIAL_CREDITO, Rol.COMITE_CREDITO),
            AppConstants.VIEW_SIMULADOR, EnumSet.of(Rol.ADMIN_SISTEMA, Rol.OFICIAL_CREDITO, Rol.COMITE_CREDITO),
            AppConstants.VIEW_DICTAMEN, EnumSet.of(Rol.ADMIN_SISTEMA, Rol.COMITE_CREDITO),
            AppConstants.VIEW_CAJA, EnumSet.of(Rol.ADMIN_SISTEMA, Rol.CAJERO),
            AppConstants.VIEW_CONTABILIDAD, EnumSet.of(Rol.ADMIN_SISTEMA, Rol.CONTADOR, Rol.AUDITOR),
            AppConstants.VIEW_AUDITORIA, EnumSet.of(Rol.ADMIN_SISTEMA, Rol.AUDITOR)
    );

    private RoleAccessFilter() {}

    public static  boolean tienePermisoVista(String rutaVista) {
        Rol rolActual = UserSession.getInstance().getRolActivo();
        if (rolActual == null){
            return false;
        }
        Set<Rol> rolesPermitidos = PERMISOS_VISTA.get(rutaVista);
        return rolesPermitidos != null && rolesPermitidos.contains(rolActual);
    }
}