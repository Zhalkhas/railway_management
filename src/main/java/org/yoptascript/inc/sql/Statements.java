package org.yoptascript.inc.sql;

import com.google.gson.JsonArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Statements {
    private MySqlConnection msqlcon;
    private Connection conn;

    public Statements() {
        msqlcon = new MySqlConnection();
        conn = msqlcon.connect();
    }

    public void connect() {
        if (conn == null) {
            msqlcon.connect();
        }
    }

    public void disconnect() {
        if (conn != null) {
            msqlcon.disconnect();
            conn = null;
        }
    }

    public void insertUser(int trainId, int capacity, String mainten, boolean av) throws SQLException {
        if (trainId == 0 || capacity == 0 || mainten == null || !av) {
            throw new SQLException("not enough info");
        }

        PreparedStatement statement = conn.prepareStatement("insert  into  TRAIN (trainId,capacity,availability,maintenanceT) values (?,?,?,?);");
        statement.setInt(2, capacity);
        statement.setString(4, mainten);
        statement.setInt(1, trainId);
        statement.setBoolean(3, av);
        statement.executeUpdate();
    }

    public JsonArray getRoutes(String dept, String dest, String date) {
        //TODO:implement function getRoutes()
        return new JsonArray();
    }

    public Map<Integer, String> getStations(String like) {
        //TODO:implement function getStations()
        return new HashMap<>();
    }

    public boolean checkTicket(String dept, String dest, int train) {
        //TODO:implement function checkTicket()
        return false;
    }

    public void insertTicket(int ticketId, String ownerN, String ownerS, double price, int docId,
                             int usrId, int agentId, int routeId, String date, String dateSched) {
        //TODO:implement function insertTicket()
    }
}