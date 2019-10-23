package org.yoptascript.inc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.yoptascript.inc.certs.KeysReader;
import org.yoptascript.inc.sql.Statements;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class Main {
  public static void main(String[] args) {
    Statements s = new Statements();
    try {
      JsonArray res = s.getRoutes("Kyzylorda", "Almaty", "2019-11-04");
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      System.out.println(gson.toJson(res));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      new KeysReader("pub.der", "priv.der").getPublicKey();
      new KeysReader("pub.der", "priv.der").getPrivateKey();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
    }

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
