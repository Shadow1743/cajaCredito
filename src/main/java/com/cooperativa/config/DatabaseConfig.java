package com.cooperativa.config;

import java.io.IOExeption;
import java.io.imputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

    public class DatabaseConfig {

        private static final Properties properties = new Properties();

        static{
            try(InputStream input = DatabaseConfig.class.getResourceAstream("/database.properties")){
                if (input == null){
                    throw new RuntimeExecption("No se encontró el archivo database.properties en resources.");
                }
                properties.load(input);
            } catch (IOExeption e){
                throw  new Excep0tionInInitializerError("Error al cargar configuración JDBC:" + e.getMessage());
            }
        }

        public static  Connection getConnection() throws SQLException {
            return DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password")
            );
        }
}