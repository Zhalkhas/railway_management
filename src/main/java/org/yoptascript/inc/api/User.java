package org.yoptascript.inc.api;

import com.google.gson.JsonObject;

import org.yoptascript.inc.sql.Statements;

import java.sql.SQLException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/user")
public class User {

    User() {
    }

    private Statements statements;

    @Secured
    @GET
    public Response isAuthorized(){
      return Response.ok(true).build();
    }

    @Path("/newUser")
    @POST
    public Response createUser(@FormParam("email") String email, @FormParam("password") String pass,
                               @FormParam("fname") String fname, @FormParam("lname") String lname) {
        statements = new Statements();
        statements.connect();
        try {
            statements.createUser(email, pass, fname, lname);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        statements.disconnect();
        return Response.ok().build();
    }

}
