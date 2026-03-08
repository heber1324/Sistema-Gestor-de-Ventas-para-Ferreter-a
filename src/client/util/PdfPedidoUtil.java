package client.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;

public class PdfPedidoUtil {

    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");

    /**
     * Genera un PDF con el detalle de un pedido.
     *
     * @param idPedido      folio del pedido
     * @param nombreCliente nombre del cliente (o "Público general")
     * @param items         productos del carrito
     * @param totalIgnorado parámetro que dejamos para compatibilidad, pero recalculamos internamente
     * @param destino       archivo donde se guardará el PDF
     */
    public static void generarPdfPedido(int idPedido,
                                        String nombreCliente,
                                        List<client.util.Carrito.Item> items,
                                        double totalIgnorado,
                                        File destino) throws Exception {

        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(doc, new FileOutputStream(destino));
        doc.open();

        // Título
        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph titulo = new Paragraph("Comprobante de pedido", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Ferretería X - Sistema de práctica"));
        doc.add(new Paragraph(" "));

        // Datos generales del pedido
        doc.add(new Paragraph("Folio de pedido: " + idPedido));
        doc.add(new Paragraph("Cliente: " +
                (nombreCliente == null || nombreCliente.trim().isEmpty()
                        ? "Público general"
                        : nombreCliente.trim())));
        doc.add(new Paragraph(" "));

        // Tabla de detalle
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1.2f, 4f, 2f, 2f});

        agregarCeldaEncabezado(tabla, "Cant.");
        agregarCeldaEncabezado(tabla, "Producto");
        agregarCeldaEncabezado(tabla, "P. Unitario");
        agregarCeldaEncabezado(tabla, "Importe");

        double subtotal = 0.0;

        for (client.util.Carrito.Item it : items) {
            tabla.addCell(String.valueOf(it.cantidad));
            tabla.addCell(it.nombre);
            tabla.addCell("$ " + DF.format(it.precio));
            tabla.addCell("$ " + DF.format(it.getSubtotal()));
            subtotal += it.getSubtotal();
        }

        doc.add(tabla);

        // Cálculo de IVA y total
        double iva = subtotal * 0.16;
        double total = subtotal + iva;

        doc.add(new Paragraph(" "));

        Paragraph pSubtotal = new Paragraph(
                "Subtotal: $ " + DF.format(subtotal),
                new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL)
        );
        pSubtotal.setAlignment(Element.ALIGN_RIGHT);
        doc.add(pSubtotal);

        Paragraph pIva = new Paragraph(
                "IVA (16%): $ " + DF.format(iva),
                new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL)
        );
        pIva.setAlignment(Element.ALIGN_RIGHT);
        doc.add(pIva);

        Paragraph pTotal = new Paragraph(
                "Total a pagar: $ " + DF.format(total),
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)
        );
        pTotal.setAlignment(Element.ALIGN_RIGHT);
        doc.add(pTotal);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Gracias por su preferencia.",
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)));

        doc.close();
    }

    private static void agregarCeldaEncabezado(PdfPTable tabla, String texto) {
        Font f = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(texto, f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(cell);
    }
}
