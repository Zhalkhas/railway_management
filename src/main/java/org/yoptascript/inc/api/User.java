package org.yoptascript.inc.api;

import com.google.gson.JsonObject;

import org.yoptascript.inc.sql.Statements;

import java.sql.SQLException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/user")
public class User {

  public User() {
  }

  Statements statements;

  @Path("/newUser")
  @POST
  public Response createUser(@FormParam("email") String email, @QueryParam("password") String pass, @QueryParam("fname") String fname, @QueryParam("lname") String lname) {
    statements = new Statements();
    statements.connect();
    try {
      statements.createUser(email, pass, fname, lname);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    statements.disconnect();
    return Response.ok().build();
  }

  @Path("/login")
  @PUT
  public Response login(@FormParam("email") String email, @FormParam("pass") String pass) {
    statements = new Statements();
    statements.connect();
    JsonObject json = new JsonObject();
    try {
      json  = statements.login(email, pass);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    statements.disconnect();
    return Response.ok(json).build();
  }
}
