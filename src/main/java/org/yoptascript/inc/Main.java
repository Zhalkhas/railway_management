package org.yoptascript.inc;

import org.yoptascript.inc.sql.Statements;

import java.sql.SQLException;

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
//    try {
//      Map<Integer, String> res = s.getStations("A");
//      System.out.println(Arrays.asList(res));
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }

    try {
      boolean res = s.checkTicket("Kyzylorda", "Aqtau", 300, "2019-11");
      System.out.println(res);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
