package server.rmi;

import server.model.VentaDTO;
import server.model.VentaItemDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface VentaService extends Remote {

    List<VentaDTO> listarVentas(String estado, String filtroCliente) throws RemoteException;
    
    int registrarVentaDesdePedido(int idPedido, int idUsuario) throws RemoteException;

    List<VentaItemDTO> obtenerDetalleVenta(int idVenta) throws RemoteException;

    boolean cancelarVenta(int idVenta) throws RemoteException;

    // 🔹 Nuevo:
    int registrarVenta(VentaDTO venta) throws RemoteException;
}
