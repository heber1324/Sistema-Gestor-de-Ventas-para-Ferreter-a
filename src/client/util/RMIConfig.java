package client.util;

public class RMIConfig {

    // 👇 Aquí pones la IP de TU PC servidor
    public static final String SERVER_HOST = "172.16.4.92";
    public static final int SERVER_PORT = 1099; // si usas otro puerto, lo cambias aquí

    public static String url(String serviceName) {
        // rmi://IP:PUERTO/NOMBRE_SERVICIO
        return "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + serviceName;
    }
}
