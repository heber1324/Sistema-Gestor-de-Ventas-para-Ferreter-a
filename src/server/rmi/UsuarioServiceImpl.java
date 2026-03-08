// server/rmi/UsuarioServiceImpl.java
package server.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import server.dao.UsuarioDAO;
import server.model.UsuarioDTO;

public class UsuarioServiceImpl extends UnicastRemoteObject implements UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioServiceImpl() throws RemoteException {
        super();
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override
    public UsuarioDTO login(String usuario, String password) throws RemoteException {
        return usuarioDAO.login(usuario, password);
    }

    @Override
    public List<UsuarioDTO> listarUsuarios(String filtro) throws RemoteException {
        return usuarioDAO.listarUsuarios(filtro);
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorId(int idUsuario) throws RemoteException {
        return usuarioDAO.obtenerUsuarioPorId(idUsuario);
    }

    @Override
    public int insertarUsuario(UsuarioDTO usuario) throws RemoteException {
        return usuarioDAO.insertarUsuario(usuario);
    }

    @Override
    public boolean actualizarUsuario(UsuarioDTO usuario) throws RemoteException {
        return usuarioDAO.actualizarUsuario(usuario);
    }

    @Override
    public boolean cambiarEstadoUsuario(int idUsuario, int nuevoActivo) throws RemoteException {
        return usuarioDAO.cambiarEstadoUsuario(idUsuario, nuevoActivo);
    }
}
