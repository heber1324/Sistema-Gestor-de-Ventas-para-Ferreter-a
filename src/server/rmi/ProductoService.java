// server/rmi/ProductoService.java
package server.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import server.model.ProductoDTO;

public interface ProductoService extends Remote {

    // Ya lo usas para catálogos / ventas
    List<Object[]> listarProductosParaTabla() throws RemoteException;
    List<ProductoDTO> listarProductos(String filtroNombre) throws RemoteException;

    // ==== Nuevos métodos para administración ====
    List<ProductoDTO> listarProductosAdmin(String filtroNombre) throws RemoteException;

    ProductoDTO obtenerProductoPorId(int idProducto) throws RemoteException;

    int insertarProducto(ProductoDTO producto) throws RemoteException;

    boolean actualizarProducto(ProductoDTO producto) throws RemoteException;

    boolean cambiarEstadoProducto(int idProducto, int nuevoActivo) throws RemoteException;
}
