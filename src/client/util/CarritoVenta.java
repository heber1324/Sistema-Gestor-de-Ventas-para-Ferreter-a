package client.util;

import java.util.ArrayList;
import java.util.List;

public class CarritoVenta {

    public static class Item {
        public int idProducto;
        public String nombre;
        public int cantidad;
        public double precioUnitario;
        public double descuento;

        public Item(int idProducto, String nombre, int cantidad, double precioUnitario, double descuento) {
            this.idProducto = idProducto;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.descuento = descuento;
        }

        public double getSubtotal() {
            return (precioUnitario - descuento) * cantidad;
        }
    }

    private static final List<Item> items = new ArrayList<>();

    public static void agregar(Item item) {
        items.add(item);
    }

    public static void limpiar() {
        items.clear();
    }

    public static List<Item> getItems() {
        return items;
    }

    public static double getTotal() {
        return items.stream().mapToDouble(Item::getSubtotal).sum();
    }
}
