package org.yoptascript.inc.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class MySqlConnection {
  // init database constants
  private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
  private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/railway65";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "Akamaka99";
  private static final String MAX_POOL = "250";

  // init connection object
  private Connection connection;
  // init properties object
  private Properties properties;

  public MySqlConnection(){
    connection = null;
    properties = null;
  }

  // create properties
  private Properties getProperties() {
    if (properties == null) {
      properties = new Properties();
      properties.setProperty("user", USERNAME);
      properties.setProperty("password", PASSWORD);
      properties.setProperty("MaxPooledStatements", MAX_POOL);
      properties.setProperty("serverTimezone", "UTC");
    }
    return properties;
  }

  // connect database
  public Connection connect() {
    if (connection == null) {
      try {
        Class.forName(DATABASE_DRIVER);
        connection = DriverManager.getConnection(DATABASE_URL, getProperties());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return connection;
  }

  // disconnect database
  public void disconnect() {
    if (connection != null) {
      try {
        connection.close();
        connection = null;
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
