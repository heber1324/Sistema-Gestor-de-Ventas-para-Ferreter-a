// server/rmi/UsuarioService.java
package server.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import server.model.UsuarioDTO;

public interface UsuarioService extends Remote {

    // Para login
    UsuarioDTO login(String usuario, String password) throws RemoteException;

    // Para administración
    List<UsuarioDTO> listarUsuarios(String filtro) throws RemoteException;

    UsuarioDTO obtenerUsuarioPorId(int idUsuario) throws RemoteException;

    int insertarUsuario(UsuarioDTO usuario) throws RemoteException;

    boolean actualizarUsuario(UsuarioDTO usuario) throws RemoteException;

    boolean cambiarEstadoUsuario(int idUsuario, int nuevoActivo) throws RemoteException;
}
