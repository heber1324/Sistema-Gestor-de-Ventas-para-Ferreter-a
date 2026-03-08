// server/rmi/BitacoraServiceImpl.java
package server.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import server.dao.BitacoraDAO;
import server.model.BitacoraOperacionDTO;

public class BitacoraServiceImpl extends UnicastRemoteObject implements BitacoraService {

    private final BitacoraDAO bitacoraDAO;

    public BitacoraServiceImpl() throws RemoteException {
        super();
        this.bitacoraDAO = new BitacoraDAO();
    }

    @Override
    public List<BitacoraOperacionDTO> listarVentasEnBitacora(
            String fechaInicio,
            String fechaFin,
            String filtroUsuario
    ) throws RemoteException {
        return bitacoraDAO.listarVentasEnBitacora(fechaInicio, fechaFin, filtroUsuario);
    }
}
