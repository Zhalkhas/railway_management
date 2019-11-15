package org.yoptascript.inc.api;

import com.google.gson.JsonArray;

import org.yoptascript.inc.sql.Statements;

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

  Manager() {}

  @Secured
  @Path("/schedules")
  @GET
  public Response getSchedules(@CookieParam("role") String role) {
    if (role.equalsIgnoreCase("manager")) {
      statements = new Statements();
      statements.connect();
      JsonArray json = new JsonArray();
      try {
        json = statements.getSchedules();
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
  public Response getEmployees(@CookieParam("role") String role) {
    if (role.equalsIgnoreCase("manager")) {
      statements = new Statements();
      statements.connect();
      JsonArray json = new JsonArray();
      try {
        json = statements.getEmployees();
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
  public Response changeEmployee(@CookieParam("role") String role, @FormParam("salary") int salary, @FormParam("start") String start,
                                 @FormParam("start") String end, @FormParam("hPerWeek") int hPerWeek) {
    if (role.equalsIgnoreCase("manager")) {
      statements = new Statements();
      statements.connect();
      try {
        statements.changeEmployee(salary, start, end, hPerWeek);
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
}
