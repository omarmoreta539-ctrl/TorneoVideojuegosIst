package Controlador;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.JFreeChart;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class GeneradorReporteEstadisticoPDF {

    public static void generarReporteGrafico(JFreeChart grafico, String fechaInicio, String fechaFin) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte Estadístico PDF");
        fileChooser.setSelectedFile(new File("Reporte_Estadistico_Partidos.pdf"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoGuardar = fileChooser.getSelectedFile();

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(archivoGuardar));
                document.open();

                Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                Paragraph titulo = new Paragraph("Reporte Estadístico de Partidos - eSports", fontTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                document.add(titulo);

                Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 12);
                Paragraph rango = new Paragraph("Período evaluado: " + fechaInicio + " al " + fechaFin, fontSub);
                rango.setAlignment(Element.ALIGN_CENTER);
                rango.setSpacingAfter(20);
                document.add(rango);

                BufferedImage bufferedImage = grafico.createBufferedImage(500, 300);
                Image imagenGrafico = Image.getInstance(bufferedImage, null);
                imagenGrafico.setAlignment(Element.ALIGN_CENTER);

                document.add(imagenGrafico);
                document.close();

                JOptionPane.showMessageDialog(null, "¡PDF generado con éxito!\nGuardado en: " + archivoGuardar.getAbsolutePath());

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + e.getMessage(), "Error PDF", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}