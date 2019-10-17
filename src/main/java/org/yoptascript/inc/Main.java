package org.yoptascript.inc;

import org.yoptascript.inc.sql.Statements;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class Main {
  public static void main(String[] args) {
    Statements s = new Statements();
//    try {
//      JsonArray res = s.getRoutes("Kyzylorda", "Almaty", "2019-10-02");
//      Gson gson = new GsonBuilder().setPrettyPrinting().create();
//      System.out.println(gson.toJson(res));
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
    try {
      Map<Integer, String> res = s.getStations("A");
      System.out.println(Arrays.asList(res));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
