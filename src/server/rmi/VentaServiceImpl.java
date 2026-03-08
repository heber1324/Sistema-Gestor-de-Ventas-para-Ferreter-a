package server.rmi;

import server.dao.VentaDAO;
import server.model.VentaDTO;
import server.model.VentaItemDTO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class VentaServiceImpl extends UnicastRemoteObject implements VentaService {

    private VentaDAO ventaDAO;

    public VentaServiceImpl() throws RemoteException {
        super();
        ventaDAO = new VentaDAO();
    }

    @Override
    public List<VentaDTO> listarVentas(String estado, String filtroCliente) throws RemoteException {
        return ventaDAO.listarVentas(estado, filtroCliente);
    }
    
    @Override
public int registrarVentaDesdePedido(int idPedido, int idUsuario) throws RemoteException {
    return ventaDAO.registrarVentaDesdePedido(idPedido, idUsuario);
}


    @Override
    public List<VentaItemDTO> obtenerDetalleVenta(int idVenta) throws RemoteException {
        return ventaDAO.obtenerDetalleVenta(idVenta);
    }

    @Override
    public boolean cancelarVenta(int idVenta) throws RemoteException {
        return ventaDAO.cancelarVenta(idVenta);
    }

    @Override
    public int registrarVenta(VentaDTO venta) throws RemoteException {
        return ventaDAO.insertarVenta(venta);
    }
}
