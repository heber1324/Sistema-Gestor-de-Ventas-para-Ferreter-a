// server/rmi/ProductoServiceImpl.java
package server.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import server.dao.ProductoDAO;
import server.model.ProductoDTO;

public class ProductoServiceImpl extends UnicastRemoteObject implements ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoServiceImpl() throws RemoteException {
        super();
        this.productoDAO = new ProductoDAO();
    }

    @Override
    public List<Object[]> listarProductosParaTabla() throws RemoteException {
        return productoDAO.listarProductosParaTabla();
    }
    
        @Override
    public List<ProductoDTO> listarProductos(String filtroNombre) throws RemoteException {
        return productoDAO.listarProductos(filtroNombre);
    }

    @Override
    public List<ProductoDTO> listarProductosAdmin(String filtroNombre) throws RemoteException {
        return productoDAO.listarProductosAdmin(filtroNombre);
    }

    @Override
    public ProductoDTO obtenerProductoPorId(int idProducto) throws RemoteException {
        return productoDAO.obtenerProductoPorId(idProducto);
    }

    @Override
    public int insertarProducto(ProductoDTO producto) throws RemoteException {
        return productoDAO.insertarProducto(producto);
    }

    @Override
    public boolean actualizarProducto(ProductoDTO producto) throws RemoteException {
        return productoDAO.actualizarProducto(producto);
    }

    @Override
    public boolean cambiarEstadoProducto(int idProducto, int nuevoActivo) throws RemoteException {
        return productoDAO.cambiarEstadoProducto(idProducto, nuevoActivo);
    }
}

