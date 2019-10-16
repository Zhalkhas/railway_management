package org.yoptascript.inc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Statements {
  MySqlConnection msqlcon;
  Connection conn;
  public Statements(){
    msqlcon = new MySqlConnection();
    conn = msqlcon.connect();
  }
  public void insertUser(int trainId,int capacity, String mainten, boolean av ) throws SQLException {
    if (trainId==0||capacity==0||mainten==null||av==false){throw new SQLException ("not enough info");
    }

    PreparedStatement statement= conn.prepareStatement("insert  into  TRAIN (trainId,capacity,availability,maintenanceT) values (?,?,?,?);");
    statement.setInt(2, capacity);
    statement.setString(4,mainten);
    statement.setInt(1,trainId);
    statement.setBoolean(3, av);
    statement.executeUpdate();
  }
}