package client.util;

import java.util.ArrayList;
import java.util.List;

public class Carrito {

    public static class Item {
        public int id;
        public String nombre;
        public int cantidad;
        public double precio;

        public Item(int id, String nombre, int cantidad, double precio) {
            this.id = id;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precio = precio;
        }

        public double getSubtotal() {
            return cantidad * precio;
        }
    }

    private static List<Item> items = new ArrayList<>();

    public static List<Item> getItems() {
        return items;
    }

    public static void agregar(Item item) {
        // Si ya existe el producto en el carrito, solo aumenta la cantidad
        for (Item i : items) {
            if (i.id == item.id) {
                i.cantidad += item.cantidad;
                return;
            }
        }
        items.add(item);
    }

    public static void eliminar(int idProducto) {
        items.removeIf(i -> i.id == idProducto);
    }

    public static void vaciar() {
        items.clear();
    }

    public static double total() {
        return items.stream().mapToDouble(Item::getSubtotal).sum();
    }
}
