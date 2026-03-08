// server/rmi/BitacoraService.java
package server.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import server.model.BitacoraOperacionDTO;

public interface BitacoraService extends Remote {

    List<BitacoraOperacionDTO> listarVentasEnBitacora(
            String fechaInicio,
            String fechaFin,
            String filtroUsuario
    ) throws RemoteException;
}
