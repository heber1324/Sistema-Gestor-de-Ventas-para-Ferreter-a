package server.rmi;

import server.model.PedidoDTO;
import server.model.PedidoItemDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PedidoService extends Remote {

    int registrarPedido(PedidoDTO pedido) throws RemoteException;

    List<PedidoDTO> listarPedidos(String estado, String filtroNombreCliente) throws RemoteException;

    List<PedidoItemDTO> obtenerDetallePedido(int idPedido) throws RemoteException;

    boolean actualizarEstadoPedido(int idPedido, String nuevoEstado) throws RemoteException;
}
