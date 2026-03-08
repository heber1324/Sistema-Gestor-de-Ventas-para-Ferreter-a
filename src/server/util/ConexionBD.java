package server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    
    private static final String URL = 
    "jdbc:mysql://localhost:3306/ferreteria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";       // cámbialo si usas otro usuario
    private static final String PASSWORD = "12345"; // pon tu contraseña

    // Método que devuelve una conexión activa
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
