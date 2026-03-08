// server/rmi/ClienteServiceImpl.java
package server.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import server.dao.ClienteDAO;
import server.model.ClienteDTO;

public class ClienteServiceImpl extends UnicastRemoteObject implements ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteServiceImpl() throws RemoteException {
        super();
        this.clienteDAO = new ClienteDAO();
    }

    @Override
    public List<ClienteDTO> listarClientes(String filtroNombre) throws RemoteException {
        return clienteDAO.listarClientes(filtroNombre);
    }
    
    @Override
    public List<ClienteDTO> listarClientes() throws RemoteException {
        return clienteDAO.listarClientes();
    }

    @Override
    public List<ClienteDTO> listarClientesAdmin(String filtroNombre) throws RemoteException {
        return clienteDAO.listarClientesAdmin(filtroNombre);
    }

    @Override
    public ClienteDTO obtenerClientePorId(int idCliente) throws RemoteException {
        return clienteDAO.obtenerClientePorId(idCliente);
    }

    @Override
    public int insertarCliente(ClienteDTO cliente) throws RemoteException {
        return clienteDAO.insertarCliente(cliente);
    }

    @Override
    public boolean actualizarCliente(ClienteDTO cliente) throws RemoteException {
        return clienteDAO.actualizarCliente(cliente);
    }

    @Override
    public boolean cambiarEstadoCliente(int idCliente, int nuevoActivo) throws RemoteException {
        return clienteDAO.cambiarEstadoCliente(idCliente, nuevoActivo);
    }
}
