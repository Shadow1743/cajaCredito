package com.cooperativa.service;

import com.cooperativa.util.FormatterUtil;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PDFExportService {

    public void exportarReporteCierreContable(
            String rutaArchivo,
            LocalDate fechaInicio,
            LocalDate fechaCierre,
            BigDecimal carteraColocada,
            BigDecimal recaudadoCobros,
            BigDecimal disponibleCaja,
            String estadoEjercicio,
            String notasAuditoria) throws Exception {

        Document document = new Document(PageSize.LETTER);
        PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
        document.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        Paragraph titulo = new Paragraph("CAJA DE CRÉDITO - RESUMEN Y CIERRE CONTABLE", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph("Periodo: " + fechaInicio + " al " + fechaCierre, subFont));
        document.add(Chunk.NEWLINE);

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.addCell("Total Cartera Colocada ($):");
        tabla.addCell(FormatterUtil.formatUSD(carteraColocada));
        tabla.addCell("Total Recaudado en Cobros ($):");
        tabla.addCell(FormatterUtil.formatUSD(recaudadoCobros));
        tabla.addCell("Disponible en Caja / Bancos ($):");
        tabla.addCell(FormatterUtil.formatUSD(disponibleCaja));
        tabla.addCell("Estado del Ejercicio:");
        tabla.addCell(estadoEjercicio);

        document.add(tabla);
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Notas de Auditoría o Cierre de Caja:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        document.add(new Paragraph(notasAuditoria != null && !notasAuditoria.isBlank() ? notasAuditoria : "Sin novedades registradas.", subFont));

        document.close();
    }
}