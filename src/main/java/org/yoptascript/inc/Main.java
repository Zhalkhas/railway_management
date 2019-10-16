package org.yoptascript.inc;

import java.sql.SQLException;

public class Main {
  public static void main(String[] args) {
    Statements s = new Statements();
    try {
      s.insertUser(1, 2, "lol", true);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
