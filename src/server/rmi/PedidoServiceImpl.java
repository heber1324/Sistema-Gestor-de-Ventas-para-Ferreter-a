package server.rmi;

import server.dao.PedidoDAO;
import server.model.PedidoDTO;
import server.model.PedidoItemDTO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class PedidoServiceImpl extends UnicastRemoteObject implements PedidoService {

    private PedidoDAO pedidoDAO;

    public PedidoServiceImpl() throws RemoteException {
        super();
        pedidoDAO = new PedidoDAO();
    }

    @Override
    public int registrarPedido(PedidoDTO pedido) throws RemoteException {
        return pedidoDAO.insertarPedido(pedido);
    }

    @Override
    public List<PedidoDTO> listarPedidos(String estado, String filtroNombreCliente) throws RemoteException {
        return pedidoDAO.listarPedidos(estado, filtroNombreCliente);
    }

    @Override
    public List<PedidoItemDTO> obtenerDetallePedido(int idPedido) throws RemoteException {
        return pedidoDAO.obtenerDetallePedido(idPedido);
    }

    @Override
    public boolean actualizarEstadoPedido(int idPedido, String nuevoEstado) throws RemoteException {
        return pedidoDAO.actualizarEstadoPedido(idPedido, nuevoEstado);
    }
}
