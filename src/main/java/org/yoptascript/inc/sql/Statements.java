package org.yoptascript.inc.sql;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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

        PreparedStatement statement = conn.prepareStatement("insert into TRAIN (trainId,capacity,availability,maintenanceT) values (?,?,?,?);");
        statement.setInt(2, capacity);
        statement.setString(4, mainten);
        statement.setInt(1, trainId);
        statement.setBoolean(3, av);
        statement.executeUpdate();
    }

    public JsonArray getRoutes(String dept, String dest, String date) throws SQLException {
        //TODO: show destination, price, train id
        if (dept == null || dest == null || date == null) {
            throw new SQLException("not enough info");
        }
        JsonArray json = new JsonArray();
        PreparedStatement statement = conn.prepareStatement("select DEP.departure, DEP.dateTimeDep, DES.detetimeDest from DEPARTURE DEP, DEPARTURE DEP2, DESTINATION DES where DEP.departure = ? and DEP2.departure = ? and DEP2.STATION_stationIdDep = DES.STATION_stationIdDest and date(DEP.datetimeDep) = ?;");
        statement.setString(1, dept);
        statement.setString(2, dest);
        statement.setString(3, date);
        ResultSet rs = statement.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        while(rs.next()) {
            int columns = rsmd.getColumnCount();
            JsonObject jsob = new JsonObject();
            for (int i = 1; i <= columns; i++) {
                String colName = rsmd.getColumnName(i);
                jsob.addProperty(colName, rs.getObject(colName).toString());
            }
            json.add(jsob);
        }
        return json;
    }

    public Map<Integer, String> getStations(String like) throws SQLException{
        if (like == null) {
            throw new SQLException("not enough info");
        }
        PreparedStatement statement = conn.prepareStatement("select stationId, name from STATION where name like ?;");
        statement.setString(1, like + "%");
        ResultSet rs = statement.executeQuery();
        HashMap<Integer, String> res = new HashMap<>();
        while(rs.next()) {
            res.put(rs.getInt(1), rs.getString(2));
        }
        return res;
    }

    public boolean checkTicket(String dept, String dest, int train) {
        //TODO:implement function checkTicket()
        return false;
    }

    public void insertTicket(int ticketId, String ownerN, String ownerS, double price, int docId,
                             int usrId, int agentId, int routeId, String date, String dateSched) throws SQLException {
        if (ticketId == -1 || ownerN == null || ownerS == null || price == -1.0 || docId == -1 || usrId == -1 ||
            agentId == -1 || routeId == -1 || date == null || dateSched == null) {
            throw new SQLException("not enough info");
        }
        PreparedStatement statement = conn.prepareStatement("insert into TICKET (ticketId, TicketOwnerName, TicketOwnerSurname, price, documentID, PASSENGER_USER_userId, AGENT_EMPLOYEE_employeeId, SCHEDULE_ROUTE_routeId, date, SCHEDULE_datetimeSchedule) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        statement.setInt(1, ticketId);
        statement.setString(2, ownerN);
        statement.setString(3, ownerS);
        statement.setDouble(4, price);
        statement.setInt(5, docId);
        statement.setInt(6, usrId);
        statement.setInt(7, agentId);
        statement.setInt(8, routeId);
        statement.setString(9, date);
        statement.setString(10, dateSched);
        statement.executeQuery();
    }
}