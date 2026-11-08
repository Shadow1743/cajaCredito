package com.cooperativa.security;

import com.cooperativa.model.entity.Rol;
import com.cooperativa.model.entity.Usuario;

public final class UserSession {

    private static  UserSession instance;
    private Usuario ususarioActivo;

    private UserSession(){}

    public static  synchronized UserSession getInstance(){
        if (instance == null){
            instance = new UserSession();
        }
        return instance;
    }

    public void iniciarSesion( {
      this.ususarioActivo = null;
    }

    public Usuario getUsusarioActivo() {
        return ususarioActivo;
    }

    public Rol getRolActivo(){
        return ususarioActivo != null ? ususarioActivo,getRol() : null;
    }

    public boolean isAutenticado() {
        return usuarioActivo != null;
    }
}