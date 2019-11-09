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

//    public void insertUser(int trainId, int capacity, String mainten, boolean av) throws SQLException {
//        if (trainId == 0 || capacity == 0 || mainten == null || !av) {
//            throw new SQLException("not enough info");
//        }
//
//        PreparedStatement statement = conn.prepareStatement("insert into TRAIN (trainId,capacity,availability,maintenanceT) values (?,?,?,?);");
//        statement.setInt(2, capacity);
//        statement.setString(4, mainten);
//        statement.setInt(1, trainId);
//        statement.setBoolean(3, av);
//        statement.executeUpdate();
//    }

    public JsonArray getRoutes(String dept, String dest, String date) throws SQLException {
        //TODO: show price, unique routes
        if (dept == null || dest == null || date == null) {
            throw new SQLException("not enough info");
        }
        JsonArray json = new JsonArray();
        PreparedStatement statement = conn.prepareStatement("select ST.name, ST2.name as arrival, SCH.departureTime, SCH2.arrivalTime, SCH.trainId \n"

            + "from STATION ST, STATION ST2, Schedule SCH, Schedule SCH2\n"
            + "where ST.name = ? and ST2.name = ? and date(SCH.departureTime) = ? and SCH.stationId = ST.stationId and SCH.trainId = SCH2.trainId and SCH.departureTime < SCH2.arrivalTime and date(SCH.departureTime) = date(SCH2.arrivalTime);");
        statement.setString(1, dept);
        statement.setString(2, dest);
        statement.setString(3, date);
        ResultSet rs = statement.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            int columns = rsmd.getColumnCount();
            JsonObject jsob = new JsonObject();
            for (int i = 1; i <= columns; i++) {
                String colName = rsmd.getColumnName(i);
                String name = "";
                if (colName.equals("name")) {
                    name = "name" + i;
                } else {
                    name = colName;
                }
                jsob.addProperty(name, rs.getObject(i).toString());
            }
            json.add(jsob);
        }
        return json;
    }

    public JsonArray getRouteStations(String dept, String dest, String date) throws SQLException {
        if (dept == null || dest == null || date == null) {
            throw new SQLException("not enough info");
        }
        PreparedStatement statement = conn.prepareStatement("select Sch.stationId, S3.name, Sch.arrivalTime\n"
                + "from railway65.SCHEDULE Sch, railway65.STATION S1, railway65.STATION S2, railway65.STATION S3,\n"
                + "(select Sch.trainId from railway65.SCHEDULE Sch where date(Sch.departureTime)=?) as b\n"
                + "where Sch.trainid=b.trainId and date(Sch.departureTime)=? and S1.name=? and S2.name=? and S3.stationId = Sch.stationId\n"
                + "group by Sch.stationId order by Sch.arrivalTime asc;");
        statement.setString(1, date);
        statement.setString(2, date);
        statement.setString(3, dept);
        statement.setString(4, dest);
        JsonArray json = new JsonArray();
        ResultSet rs = statement.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            JsonObject jsob = new JsonObject();
            jsob.addProperty(rsmd.getColumnName(1), rs.getInt(1));
            jsob.addProperty(rsmd.getColumnName(2), rs.getString(2));
            jsob.addProperty(rsmd.getColumnName(3), rs.getString(3));
            json.add(jsob);
        }
        return json;
    }

    public Map<Integer, String> getStations(String like) throws SQLException {
        if (like == null) {
            throw new SQLException("not enough info");
        }
        PreparedStatement statement = conn.prepareStatement("select stationId, name from STATION where name like ?;");
        statement.setString(1, like + "%");
        ResultSet rs = statement.executeQuery();
        HashMap<Integer, String> res = new HashMap<>();
        while (rs.next()) {
            res.put(rs.getInt(1), rs.getString(2));
        }
        return res;
    }

    public boolean checkTicket(String dept, String dest, int train, String date) throws SQLException {
        if (dept == null || dest == null || train < 0) {
            throw new SQLException("not enough info");
        }
        PreparedStatement statement = conn.prepareStatement("select SCH.availability from Schedule SCH, Schedule SCH2, STATION ST, STATION ST2\n"
                + "where SCH.stationId = ST.stationId and SCH2.stationId = ST2.stationID and ST.name = ? and ST2.name = ? and SCH.trainId = SCH2.trainId and SCH.trainId = ? and date(SCH.departureTime) like ?;");
        statement.setString(1, dept);
        statement.setString(2, dest);
        statement.setInt(3, train);
        statement.setString(4, date + "%");
        ResultSet rs = statement.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    public void insertTicket(String ownerN, String ownerS, double price, int docId,
                             int usrId, int agentId, int deptId, int destId) throws SQLException {
        if (ownerN == null || ownerS == null || price < 0 || docId < 0 || usrId < 0 ||
                agentId < 0 || destId < 0 || deptId < 0) {
            throw new SQLException("not enough info");
        }
        PreparedStatement statement = conn.prepareStatement("insert into TICKET (TicketOwnerName, TicketOwnerSurname, price, documentID, passsengerID, AGENT_EMPLOYEE_employeeId, Schedule_scheduleId, ScheduleIdArrival) values (?, ?, ?, ?, ?, ?, ?, ?);");
        statement.setString(1, ownerN);
        statement.setString(2, ownerS);
        statement.setDouble(3, price);
        statement.setInt(4, docId);
        statement.setInt(5, usrId);
        statement.setInt(6, agentId);
        statement.setInt(7, deptId);
        statement.setInt(8, destId);
        statement.executeQuery();
    }

    public void changeTicket(int ticketId, String ownerN, String ownerS, double price, int docId,
                             int usrId, int agentId, int deptId, int destId) throws SQLException {
        if (ticketId < 1 || ownerN == null || ownerS == null || price < 0 || docId < 0 || usrId < 0 ||
                agentId < 0 || deptId < 0 || destId < 0) {
            throw new SQLException("not enough info");
        }
        PreparedStatement statement = conn.prepareStatement("update TICKET set TicketOwnerName = ?, TicketOwnerSurname = ?, price = ?, documentID = ?, passsengerID = ?, AGENT_EMPLOYEE_employeeId = ?, Schedule_scheduleId = ?, ScheduleIdArrival = ? where ticketId = ?;");
        statement.setString(1, ownerN);
        statement.setString(2, ownerS);
        statement.setDouble(3, price);
        statement.setInt(4, docId);
        statement.setInt(5, usrId);
        statement.setInt(6, agentId);
        statement.setInt(7, deptId);
        statement.setInt(8, destId);
        statement.setInt(9, ticketId);
        statement.executeQuery();
    }

    public JsonObject getTicket(int ticketId) throws SQLException {
        if (ticketId < 1) {
            throw new SQLException("not enough info");
        }
        PreparedStatement statement = conn.prepareStatement("select * from TICKET where ticketId = ?");
        statement.setInt(1, ticketId);
        ResultSet rs = statement.executeQuery();
        JsonObject json = new JsonObject();
        while (rs.next()) {
            json.addProperty("ticketId", rs.getInt(1));
            json.addProperty("ownerN", rs.getString(2));
            json.addProperty("ownerS", rs.getString(3));
            json.addProperty("price", rs.getString(4));
            json.addProperty("docId", rs.getString(5));
            json.addProperty("userId", rs.getString(6));
            json.addProperty("agentId", rs.getString(7));
            json.addProperty("deptId", rs.getString(8));
            json.addProperty("destId", rs.getString(9));
        }
        return json;
    }

    public JsonArray getAllPastTicketsOfUser(int userId) throws SQLException {
        if (userId < 1) {
            throw new SQLException("no info");
        }
        PreparedStatement statement = conn.prepareStatement("select T.ticketId, SCH1.departureTime, ST1.name, SCH2.arrivalTime, ST2.name, SCH1.trainId\n"
                + "from TICKET T, SCHEDULE SCH1, SCHEDULE SCH2, STATION ST1, STATION ST2, USER U\n"

                + "where T.passengerID = U.userId and U.userId = ? and T.Schedule_scheduleID = SCH1.scheduleId and SCH1.stationId = ST1.stationId and SCH2.stationId = ST2.stationId and SCH1.trainId = SCH2.trainId and SCH2.scheduleId = T.ScheduleIdArrival and SCH1.departureTime <= SCH2.arrivalTime and SCH2.departureTime < now();");
        statement.setInt(1, userId);
        ResultSet rs = statement.executeQuery();
        JsonArray json = new JsonArray();
        while (rs.next()) {
            JsonObject jsob = new JsonObject();
            jsob.addProperty("ticketId", rs.getInt(1));
            jsob.addProperty("depTime", rs.getString(2));
            jsob.addProperty("depName", rs.getString(3));
            jsob.addProperty("arrivalTime", rs.getString(4));
            jsob.addProperty("arrivalName", rs.getString(5));
            jsob.addProperty("trainId", rs.getString(6));
            json.add(jsob);
        }
        return json;
    }

    public JsonArray getAllFutureTicketsOfUser(int userId) throws SQLException {
        if (userId < 1) {
            throw new SQLException("no info");
        }
        PreparedStatement statement = conn.prepareStatement("select T.ticketId, SCH1.departureTime, ST1.name, SCH2.arrivalTime, ST2.name, SCH1.trainId\n"
                + "from TICKET T, SCHEDULE SCH1, SCHEDULE SCH2, STATION ST1, STATION ST2, USER U\n"
                + "where T.passengerID = U.userId and U.userId = ? and T.Schedule_scheduleID = SCH1.scheduleId and SCH1.stationId = ST1.stationId and SCH2.stationId = ST2.stationId and SCH1.trainId = SCH2.trainId and SCH2.scheduleId = T.ScheduleIdArrival and SCH1.departureTime <= SCH2.arrivalTime and SCH1.departureTime > now();");
        statement.setInt(1, userId);
        ResultSet rs = statement.executeQuery();
        JsonArray json = new JsonArray();
        while (rs.next()) {
            JsonObject jsob = new JsonObject();
            jsob.addProperty("ticketId", rs.getInt(1));
            jsob.addProperty("depTime", rs.getString(2));
            jsob.addProperty("depName", rs.getString(3));
            jsob.addProperty("arrivalTime", rs.getString(4));
            jsob.addProperty("arrivalName", rs.getString(5));
            jsob.addProperty("trainId", rs.getString(6));
            json.add(jsob);
        }
        return json;
    }

    public void deleteTicket(int ticketId) throws SQLException {
        if (ticketId < 0) {
            throw new SQLException("no info");
        }
        PreparedStatement statement = conn.prepareStatement("delete from TICKET where ticketId = ?");
        statement.setInt(1, ticketId);
        statement.executeQuery(); // should it return a boolean?
    }

    public void createUser(String email, String pass, String fname, String lname) throws SQLException {
        if (email == null || pass == null || fname == null || lname == null) {
            throw new SQLException();
        }
        PreparedStatement statement = conn.prepareStatement("insert into USER (FName, LName, email, password) values (?, ?, ?, ?);");
        statement.setString(1, fname);
        statement.setString(2, lname);
        statement.setString(3, email);
        statement.setString(4, pass);
        statement.executeUpdate();
    }

    public JsonObject login(String email, String pass) throws SQLException {
        if (email == null || pass == null) {
            throw new SQLException("not enough info");
        }
        PreparedStatement statement = conn.prepareStatement("select userId ,FName, LName, email from USER\n"
                + "where email = ? and password = ?;");
        statement.setString(1, email);
        statement.setString(2, pass);
        ResultSet rs = statement.executeQuery();
        JsonObject json = new JsonObject();
        //TODO: profile page should also show all tickets of passenger
        while (rs.next()) {
            json.addProperty("userId", rs.getString(1));
            json.addProperty("FName", rs.getString(2));
            json.addProperty("LName", rs.getString(3));
            json.addProperty("email", rs.getString(4));
        }
        return json;
    }

    public JsonArray getAllTickets() throws SQLException {
        //TODO: finish method for tickets of agent
        PreparedStatement statement = conn.prepareStatement("select * from TICKET;");
        statement.executeQuery();
        return new JsonArray();
    }
}