package org.yoptascript.inc.other;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class PDFCreator {
  private Document document;
  private String name;
  private List<String> ticketInfo;
  private PdfWriter pdfWriter;

  public PDFCreator(String name, List<String> ticketInfo) {
    document = new Document();
    this.name = name;
    this.ticketInfo = ticketInfo;

  }

  public void createPdf() {
    try {
      pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(name));
      document.open();
      document.addAuthor("Yoptascript.inc");
      document.addTitle(name);
      for (String s : ticketInfo) {
        document.add(new Paragraph(s));
      }

      document.close();
      pdfWriter.close();
    } catch (DocumentException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
