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
        PreparedStatement statement = conn.prepareStatement("select ST.name, ST2.name, SCH.departureTime, SCH2.arrivalTime, SCH.trainId, SCH.availability, SCH.routeIsClosed \n"
            + "from STATION ST, STATION ST2, Schedule SCH, Schedule SCH2\n"
            + "where ST.name = ? and ST2.name = ? and date(SCH.departureTime) = ? and SCH.stationId = ST.stationId and SCH.trainId = SCH2.trainId and SCH.departureTime < SCH2.arrivalTime and date(SCH.departureTime) = date(SCH2.arrivalTime);");
        statement.setString(1, dept);
        statement.setString(2, dest);
        statement.setString(3, date);
        ResultSet rs = statement.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        boolean isAvailable = true;
        while (rs.next()) {
            int columns = rsmd.getColumnCount();
            JsonObject jsob = new JsonObject();
            jsob.addProperty("name1", rs.getString(1));
            jsob.addProperty("name2", rs.getString(2));
            jsob.addProperty("departureTime", rs.getString(3));
            jsob.addProperty("arrivalTime", rs.getString(4));
            jsob.addProperty("trainId", rs.getInt(5));
            if (rs.getInt(6) <= 0 || rs.getInt(7) == 1) {
                isAvailable = false;
            }
            json.add(jsob);
        }
        rs.close();
        JsonObject jsob = new JsonObject();
        jsob.addProperty("opened", isAvailable);
        json.add(jsob);
        return json;
    }

    public JsonArray getRouteStations(String dept, String dest, String date) throws SQLException {
        if (dept == null || dest == null || date == null) {
            throw new SQLException("not enough info");
        }
        PreparedStatement statement = conn.prepareStatement("select S1.name\n"
            + "                from Railway65.SCHEDULE Sch, Railway65.STATION S1,\n"
            + "                (select Sch.trainId as trainId, Sch1.arrivalTime as endT, Sch.departureTime as startT from Railway65.SCHEDULE Sch, Railway65.SCHEDULE Sch1, \n"
            + "                Railway65.STATION st, Railway65.STATION st1 where  date(Sch.departureTime)=?\n"
            + "                and date(Sch1.arrivalTime)=? and Sch.stationId=st.stationId and st.name=?\n"
            + "                and Sch1.stationId=st1.stationId and st1.name=? and Sch1.trainId=Sch.trainId and \n"
            + "                Sch.departureTime<Sch1.arrivalTime) as b\n"
            + "                where Sch.trainId=b.trainId and Sch.stationId=S1.stationId\n"
            + "                and Sch.departureTime >= b.startT and Sch.arrivalTime <=b.endT ;");
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
        rs.close();
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
        rs.close();
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
        boolean res = rs.getInt(1) > 0;
        rs.close();
        return res;
    }

    public void insertTicket(String ownerN, String ownerS, double price, int docId,
                             String usrId, int agentId, String deptId, String destId, String date) throws SQLException {
        if (ownerN == null || ownerS == null || price < 0 || docId < 0 || usrId == null ||
                agentId < 0 || destId == null || deptId == null || date == null) {
            throw new SQLException("not enough info");
        }
        PreparedStatement statement = conn.prepareStatement("insert into TICKET (TicketOwnerName, TicketOwnerSurname, price, documentID, USER_userId, EMPLOYEE_employeeId, Schedule_scheduleId, ScheduleIdArrival) values (?, ?, ?, ?, "
            + "(select userId from USER where email = ?), ?, "
            + "(select scheduleId from SCHEDULE SCH, STATION ST where name = ? and date(departureTime) = date(?) and SCH.stationId = ST.stationId), "
            + "(select scheduleId from SCHEDULE SCH, STATION ST where name = ? and date(arrivalTime) = date(?) and SCH.stationId = ST.stationId));");
        statement.setString(1, ownerN);
        statement.setString(2, ownerS);
        statement.setDouble(3, price);
        statement.setInt(4, docId);
        statement.setString(5, usrId);
        statement.setInt(6, agentId);
        statement.setString(7, deptId);
        statement.setString(8, date);
        statement.setString(9, destId);
        statement.setString(10, date);
        statement.execute();
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
        statement.execute();
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
        rs.close();
        return json;
    }

    public JsonArray getAllPastTicketsOfUser(String username) throws SQLException {
        if (username == null) {
            throw new SQLException("no info");
        }
        PreparedStatement statement = conn.prepareStatement("select T.ticketId, SCH1.departureTime, ST1.name, SCH2.arrivalTime, ST2.name, SCH1.trainId\n"
                + "from TICKET T, SCHEDULE SCH1, SCHEDULE SCH2, STATION ST1, STATION ST2, USER U\n"
                + "where T.USER_userId = U.userId and U.email = ? and T.Schedule_scheduleID = SCH1.scheduleId and SCH1.stationId = ST1.stationId and"
                + " SCH2.stationId = ST2.stationId and SCH1.trainId = SCH2.trainId and SCH2.scheduleId = T.ScheduleIdArrival and SCH1.departureTime <= SCH2.arrivalTime and SCH2.departureTime < now();");
        statement.setString(1, username);
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
        rs.close();
        return json;
    }

    public JsonArray getAllFutureTicketsOfUser(String username) throws SQLException {
        if (username == null) {
            throw new SQLException("no info");
        }
        PreparedStatement statement = conn.prepareStatement("select T.ticketId, SCH1.departureTime, ST1.name, SCH2.arrivalTime, ST2.name, SCH1.trainId\n"
                + "from TICKET T, SCHEDULE SCH1, SCHEDULE SCH2, STATION ST1, STATION ST2, USER U\n"
                + "where T.USER_userId = U.userId and U.email = ? and T.Schedule_scheduleID = SCH1.scheduleId and SCH1.stationId = ST1.stationId and SCH2.stationId = ST2.stationId and SCH1.trainId = SCH2.trainId and SCH2.scheduleId = T.ScheduleIdArrival and SCH1.departureTime <= SCH2.arrivalTime and SCH1.departureTime > now();");
        statement.setString(1, username);
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
        rs.close();
        return json;
    }

    public void deleteTicket(int ticketId) throws SQLException {
        if (ticketId < 0) {
            throw new SQLException("no info");
        }
        PreparedStatement statement = conn.prepareStatement("delete from TICKET where ticketId = ?");
        statement.setInt(1, ticketId);
        statement.execute(); // should it return a boolean?
    }

    public boolean createUser(String email, String pass, String fname, String lname) throws SQLException {
        if (email == null || pass == null || fname == null || lname == null) {
            throw new SQLException();
        }
        PreparedStatement statement1 = conn.prepareStatement("select FName, LName from USER where email = ?;");
        statement1.setString(1, email);
        ResultSet rs = statement1.executeQuery();
        boolean isCreated = false;
        if (!rs.next()) {
          PreparedStatement
              statement =
              conn.prepareStatement(
                  "insert into USER (FName, LName, email, password) values (?, ?, ?, ?);");
          statement.setString(1, fname);
          statement.setString(2, lname);
          statement.setString(3, email);
          statement.setString(4, pass);
          statement.executeUpdate();
          isCreated = true;
        }
        rs.close();
        return isCreated;
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
        while (rs.next()) {
            json.addProperty("userId", rs.getString(1));
            json.addProperty("FName", rs.getString(2));
            json.addProperty("LName", rs.getString(3));
            json.addProperty("email", rs.getString(4));
        }
        rs.close();
        return json;
    }

    public JsonArray getAllTickets(String agentEmail) throws SQLException {
        //TODO: check this statement
        PreparedStatement statement = conn.prepareStatement("select ticketId, TicketOwnerName, TicketOwnerSurname, price, USER_userId, SCH.departureTime,\n"
            + "ST.name, SCH1.arrivalTime, ST1.name\n"
            + "from TICKET, USER, EMPLOYEE, SCHEDULE SCH, SCHEDULE SCH1, STATION ST, STATION ST1\n"
            + "where employeeId = userId and email = ? and STATION_stationId = ST.stationId and Schedule_scheduleId = SCH.scheduleId and ScheduleIdArrival = SCH1.scheduleId and\n"
            + "SCH.stationId = ST.stationId and SCH1.stationId = ST1.stationId order by SCH.departureTime asc;");
        statement.setString(1, agentEmail);
        ResultSet rs = statement.executeQuery();
        JsonArray json = new JsonArray();
        while (rs.next()) {
            JsonObject jsob = new JsonObject();
            jsob.addProperty("ticketId", rs.getInt(1));
            jsob.addProperty("ownerN", rs.getString(2));
            jsob.addProperty("ownerS", rs.getString(3));
            jsob.addProperty("price", rs.getInt(4));
            jsob.addProperty("passengerID", rs.getInt(5));
            jsob.addProperty("departureTime", rs.getString(6));
            jsob.addProperty("departureName", rs.getString(7));
            jsob.addProperty("arrivalTime", rs.getString(8));
            jsob.addProperty("arrivalName", rs.getString(9));
            json.add(jsob);
        }
        rs.close();
        return json;
    }

    public JsonArray ticketAgent(String email) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("select t.ticketId,t.TicketOwnerName,t.TicketOwnerSurname,\n" +
            "t.price,t.USER_userId,s.departureTime from TICKET t, Schedule s, USER u, EMPLOYEE e\n" +
            "where u.email=? and u.status='agent' and \n" +
            "e.employeeId=u.userId and e.STATION_stationId=s.stationId and t.Schedule_scheduleId=s.scheduleId;");
        statement.setString(1, email);
        ResultSet rs= statement.executeQuery();
        JsonArray j = new JsonArray();
        while (rs.next()) {
            JsonObject json=new JsonObject();
            json.addProperty("ticketId", rs.getInt(1));
            json.addProperty("ticketOwnerName", rs.getString(2));
            json.addProperty("ticketOwnerSurname", rs.getString(3));
            json.addProperty("price", rs.getInt(4));
            json.addProperty("userId", rs.getInt(5));
            json.addProperty("departureTime", rs.getString(6));
            j.add(json);
        }
        return j;
    }

    public String getRole(String email) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("select status from USER where email = ?;");
        statement.setString(1, email);
        ResultSet rs = statement.executeQuery();
        rs.next();
        String res = rs.getString(1);
        rs.close();
        return res;
    }

    public JsonArray getEmployees(String email) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("select E.employeeId, U.FName, U.LName, E.salary, E.StartOfWork, E.EndOfWork, E.STATION_stationId from USER U, USER U1, EMPLOYEE E, EMPLOYEE E1 "
            + "where U.status = 'agent' and U.userId = E.employeeId and E1.employeeId = U1.userId and U1.email = ? and E.STATION_stationId = E1.STATION_stationId;");
        statement.setString(1, email);
        JsonArray json = new JsonArray();
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            JsonObject jsob = new JsonObject();
            jsob.addProperty("emplId", rs.getString(1));
            jsob.addProperty("emplName", rs.getString(2));
            jsob.addProperty("emplSurname", rs.getString(3));
            //jsob.addProperty("email", rs.getString(4));
            jsob.addProperty("salary", rs.getInt(4));
            jsob.addProperty("startWork", rs.getString(5));
            jsob.addProperty("endWork", rs.getString(6));
            //jsob.addProperty("hoursOfWorkPerWeek", rs.getInt(11));
            jsob.addProperty("station", rs.getInt(7));
            json.add(jsob);
        }
        rs.close();
        return json;
    }

    public JsonArray getSchedules(String username) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("select arrivalTime, departureTime, trainId, scheduleId, STATION.name, availability, routeIsClosed, maintenanceR from SCHEDULE, EMPLOYEE, USER, STATION "
            + "where ? = email and userId = employeeId and STATION_stationId = SCHEDULE.stationId and STATION_stationId = STATION.stationId;");
        statement.setString(1, username);
        JsonArray json = new JsonArray();
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            JsonObject jsob = new JsonObject();
            jsob.addProperty("arrivalTime", rs.getString(1));
            jsob.addProperty("departureTime", rs.getString(2));
            jsob.addProperty("trainId", rs.getInt(3));
            jsob.addProperty("scheduleId", rs.getInt(4));
            jsob.addProperty("departureName", rs.getString(5));
            jsob.addProperty("availability", rs.getInt(6));
            jsob.addProperty("routeIsClosed", rs.getBoolean(7));
            jsob.addProperty("maintenanceR", rs.getString(8));
            json.add(jsob);
        }
        rs.close();
        return json;
    }

    public void changeEmployee(int id, int salary, String start, String end)
        throws SQLException {
      PreparedStatement statement = conn.prepareStatement("update EMPLOYEE set salary = ?, StartOfWork = ?, EndOfWork = ? where employeeId = ?");
      statement.setInt(1, salary);
      statement.setString(2, start);
      statement.setString(3, end);
      statement.setInt(4, id);
      statement.execute();
    }

    public void createRoute(int trainNumber, String from, String to, String departureTime, String arrivalTime) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("insert into SCHEDULE values (?, ?, ?, STATION.stationId, ?, ?, ?) where STATION.stationName = ?;");
        //TODO: add 2 schedules, but second one should be without departureTime + if next route is added, it should be
        statement.setString(1, arrivalTime);
        statement.setString(2, departureTime);
        statement.setInt(3, trainNumber);
        statement.setString(4, from);
        statement.setString(5, to);
        statement.execute();
        PreparedStatement statement1 = conn.prepareStatement("");
    }

//    public void createRoute(String[] stations, String[] arrT, String[] depT, int trainId) throws SQLException {
//        int len=stations.length;
//        PreparedStatement statement0 = conn.prepareStatement("select capacity from Train where trainId=?;");
//        statement0.setInt(1,trainId);
//        ResultSet rs = statement0.executeQuery();
//        int cap = rs.getInt(1);
//        PreparedStatement statement2 = conn.prepareStatement("select max(scheduleId) from SCHEDULE;");
//        ResultSet rd = statement2.executeQuery();
//        int scId = rd.getInt(1);
//        scId++;
//        for (int i = 0; i < len; i++){
//            PreparedStatement statement1 = conn.prepareStatement("select stationId from STATION where name=?;");
//            statement1.setString(1,stations[i]);
//            ResultSet rs1 = statement1.executeQuery();
//            int stId=rs.getInt(1);
//
//            PreparedStatement statement=conn.prepareStatement("insert into SCHEDULE (arrivalTime,departureTime," +
//                "trainId,scheduleId,stationId,availability,routeIsClosed,maintenanceR) values (?,?,?,?,?,?,?,?);");
//            statement.setString(1, arrT[i]);
//            statement.setString(2,depT[i]);
//            statement.setInt(3,trainId);
//            statement.setInt(5,stId);
//            statement.setInt(6, cap);
//            statement.setString(7, "false");
//            statement.setString(8, "good");
//            statement.setInt(4, scId) ;
//            scId++;
//        }
//    }

    public void deleteRoute(int id) throws SQLException {
      PreparedStatement statement = conn.prepareStatement("delete from SCHEDULE where scheduleId = ?");
      statement.setInt(1, id);
      statement.execute();
    }

    public boolean createStation(String name, String username) throws SQLException {
      PreparedStatement statement1 = conn.prepareStatement("select stationId from STATION where name = ?;");
      statement1.setString(1, name);
      ResultSet rs = statement1.executeQuery();
      boolean isCreated = false;
      if (!rs.next()) {
        PreparedStatement
            statement2 =
            conn.prepareStatement(
                "insert into station (name, managerId) values (?, "
                    + "(select employeeId from EMPLOYEE, USER where email = ? and userId = employeeId));");
        statement2.setString(1, name);
        statement2.setString(2, username);
        statement2.execute();
        isCreated = true;
      }
      rs.close();
      return isCreated;
    }
    public ResultSet notifyChangesPass (int id) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("select u.email, s.arrivalTime," +
                " s.departureTime from USER u, TICKET t, SCHEDULE s where s.scheduleId=t.Schedule_scheduleId and \n" +
                "t.Schedule_scheduleId= ? and t.USER_userId=u.userId union \n" +
                "select u.email, s.arrivalTime, s.departureTime from USER u, TICKET t, SCHEDULE s where s.scheduleId=t.Schedule_scheduleId and \n" +
                "t.Schedule_scheduleId=? and t.EMPLOYEE_employeeId=u.userId;;");
        statement.setInt(1, id);
        statement.setInt(2, id);
        ResultSet rs = statement.executeQuery();
        return rs;
    }

    public String getAgentEmail(String station) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("select email from USER, EMPLOYEE, STATION\n"
            + "where userId = employeeId and STATION_stationId = STATION.stationId and STATION.name = ?;");
        statement.setString(1, station);
        ResultSet rs = statement.executeQuery();
        rs.next();
        return rs.getString(1);
    }
}