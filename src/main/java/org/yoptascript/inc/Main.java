package org.yoptascript.inc;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.yoptascript.inc.sql.Statements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Main {
  public static void main(String[] args) {
    Statements s = new Statements();
//    try {
//      JsonArray res = s.getRouteStations("Kyzylorda", "Almaty", "2019-11-04");
//      Gson gson = new GsonBuilder().setPrettyPrinting().create();
//      System.out.println(gson.toJson(res));
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
    Document document = new Document();
    try {
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("NewFile.pdf"));
      document.open();
      document.add(new Paragraph("New paragraph"));
      document.close();
      writer.close();
    } catch (DocumentException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
//    try {
//      new KeysReader("pub.der", "priv.der").getPublicKey();
//      new KeysReader("pub.der", "priv.der").getPrivateKey();
//    } catch (NoSuchAlgorithmException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    } catch (InvalidKeySpecException e) {
//      e.printStackTrace();
//    }

//    try {
//      Map<Integer, String> res = s.getStations("A");
//      System.out.println(Arrays.asList(res));
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }

//    try {
//      boolean res = s.checkTicket("Kyzylorda", "Aqtau", 300, "2019-11");
//      System.out.println(res);
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
  }
}
