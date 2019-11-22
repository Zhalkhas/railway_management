package org.yoptascript.inc.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.yoptascript.inc.other.LogEntry;
import org.yoptascript.inc.sql.Statements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Secured
@Path("/manager")
public class Manager {
    private Statements statements;

    Manager() {
    }

    @Secured
    @Path("/schedules")
    @GET
    public Response getSchedules(@CookieParam("role") String role, @CookieParam("username") String username) {
        if (role.equalsIgnoreCase("manager")) {
            statements = new Statements();
            statements.connect();
            JsonArray json = new JsonArray();
            try {
                json = statements.getSchedules(username);
            } catch (SQLException e) {
                return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
            } finally {
                statements.disconnect();
            }
            return Response.ok(json.toString()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Secured
    @Path("/employees")
    @GET
    public Response getEmployees(@CookieParam("role") String role, @CookieParam("username") String email) {
        if (role.equalsIgnoreCase("manager")) {
            statements = new Statements();
            statements.connect();
            JsonArray json = new JsonArray();
            try {
                json = statements.getEmployees(email);
            } catch (SQLException e) {
                return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
            } finally {
                statements.disconnect();
            }
            return Response.ok(json.toString()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Secured
    @PUT
    @Path("/changeEmployee")
    public Response changeEmployee(@CookieParam("role") String role, @FormParam("emplId") int id, @FormParam("salary") int salary, @FormParam("start") String start,
                                   @FormParam("end") String end) {
        if (role.equalsIgnoreCase("manager")) {
            statements = new Statements();
            statements.connect();
            try {
                statements.changeEmployee(id, salary, start, end);
            } catch (SQLException e) {
                return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
            } finally {
                statements.disconnect();
            }
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Secured
    @Path("/createRoute")
    @POST
    public Response createRoute(@FormParam("trainNumber") int trainNumber, @FormParam("from") String from, @FormParam("to") String to,
                                @FormParam("departureTime") String departureTime, @FormParam("arrivalTime") String arrivalTime,
                                @CookieParam("role") String role) {
        if (role.equalsIgnoreCase("manager")) {
            statements = new Statements();
            statements.connect();
            //TODO: finish, all times are dateTimes
            try {
                statements.createRoute(trainNumber, from, to, departureTime, arrivalTime);
            } catch (SQLException e) {
                return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
            } finally {
                statements.disconnect();
            }
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Secured
    @POST
    @Path("/createStation")
    public Response createStation(@CookieParam("role") String role, @CookieParam("username") String username, @FormParam("name") String name) {
        if (role.equalsIgnoreCase("manager")) {
            statements = new Statements();
            statements.connect();
            boolean isCreated = false;
            try {
                isCreated = statements.createStation(name, username);
            } catch (SQLException e) {
                return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
            } finally {
                statements.disconnect();
            }
            return isCreated ? Response.ok().build() : Response.status(Response.Status.CONFLICT).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Secured
    @Path("/deleteRoute")
    @DELETE
    public Response deleteRoute(@FormParam("id") int id, @CookieParam("role") String role) {
        if (role.equalsIgnoreCase("manager")) {
            statements = new Statements();
            statements.connect();
            try {
                statements.deleteRoute(id);
            } catch (SQLException e) {
                return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
            } finally {
                statements.disconnect();
            }
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Secured
    @Path("/log")
    @GET
    public Response getLogs(@CookieParam("role") String role) {
        if (role.equalsIgnoreCase("manager")) {
            try {

                BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("catalina.home")+"/logs/logging.log"));
                StringBuilder builder = new StringBuilder();
                String currentLine = reader.readLine();
                int line = 1;
                while (currentLine != null) {
                    builder.append(currentLine);
                    currentLine = reader.readLine();
                    line++;
                }
                reader.close();
                LogEntry[] logEntries = new LogEntry[line];
                line = 0;
                Document log = Jsoup.parse(new String(builder));
                Elements logEntry = log.body().select("log");
                for (Element e : logEntry) {
                    String date = "";
                    String level = "";
                    String uri = "";
                    String method = "";
                    String req_headers = "";
                    String resp_headers = "";
                    String status = "";
                    for (Element el : e.children()) {
                        String selector = el.cssSelector();
                        if (selector.contains("date")) {
                            date = el.ownText();
                        } else if (selector.contains("level")) {
                            level = el.ownText();
                        } else if (selector.contains("uri")) {
                            uri = el.ownText();
                        } else if (selector.contains("method")) {
                            method = el.ownText();
                        } else if (selector.contains("req_headers")) {
                            req_headers = el.ownText();
                        } else if (selector.contains("resp_headers")) {
                            resp_headers = el.ownText();
                        } else if (selector.contains("status")) {
                            status = el.ownText();
                        }
                    }
                  logEntries[line] = new LogEntry(date, level, uri, method, req_headers, resp_headers, status);
                  line++;
                }
                Gson gson = new Gson();
                String json = gson.toJson(logEntries, LogEntry[].class);
                return Response.ok(json).build();
            } catch (Exception e) {
              e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
