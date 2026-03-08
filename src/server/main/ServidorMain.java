package server.main;

import server.rmi.UsuarioService;
import server.rmi.UsuarioServiceImpl;
import server.rmi.PedidoService;
import server.rmi.PedidoServiceImpl;
import server.rmi.VentaService;
import server.rmi.VentaServiceImpl;
import server.rmi.ClienteService;
import server.rmi.ClienteServiceImpl;



import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import server.rmi.BitacoraService;
import server.rmi.BitacoraServiceImpl;
import server.rmi.ProductoService;
import server.rmi.ProductoServiceImpl;

public class ServidorMain {

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);

            UsuarioService usuarioService = new UsuarioServiceImpl();
            Naming.rebind("rmi://0.0.0.0/UsuarioService", usuarioService);

            PedidoService pedidoService = new PedidoServiceImpl();
            Naming.rebind("rmi://0.0.0.0/PedidoService", pedidoService);
            
            ProductoService productoService = new ProductoServiceImpl();
            Naming.rebind("rmi://0.0.0.0/ProductoService", productoService);
            
            VentaService ventaService = new VentaServiceImpl();
            Naming.rebind("rmi://0.0.0.0/VentaService", ventaService);
            
            ClienteService clienteService = new ClienteServiceImpl();
            Naming.rebind("rmi://0.0.0.0/ClienteService", clienteService);
            
            BitacoraService bitacoraService = new BitacoraServiceImpl();
            Naming.rebind("rmi://0.0.0.0/BitacoraService", bitacoraService);



            System.out.println("Servidor RMI de ferretería listo...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
