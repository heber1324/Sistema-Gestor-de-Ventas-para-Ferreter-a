// server/rmi/ClienteService.java
package server.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import server.model.ClienteDTO;

public interface ClienteService extends Remote {

    List<ClienteDTO> listarClientes(String filtroNombre) throws RemoteException;
    
    List<ClienteDTO> listarClientes() throws RemoteException;

    List<ClienteDTO> listarClientesAdmin(String filtroNombre) throws RemoteException;

    ClienteDTO obtenerClientePorId(int idCliente) throws RemoteException;

    int insertarCliente(ClienteDTO cliente) throws RemoteException;

    boolean actualizarCliente(ClienteDTO cliente) throws RemoteException;

    boolean cambiarEstadoCliente(int idCliente, int nuevoActivo) throws RemoteException;
}
