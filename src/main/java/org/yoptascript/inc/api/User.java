package org.yoptascript.inc.api;

import com.google.gson.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.apache.log4j.Level;
import org.yoptascript.inc.certs.KeysReader;
import org.yoptascript.inc.sql.Statements;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
@Path("/user")
public class User {

    User() {
    }

    private Statements statements;

    @Secured
    @GET
    public Response isAuthorized(@CookieParam("token") String token) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        Jws<Claims> jws;
        jws = Jwts.parser()
                .setSigningKey((new KeysReader("pub.der", "priv.der"))
                        .getPublicKey())
                .parseClaimsJws(token);
        Claims claims = jws.getBody();
        System.out.println(claims.getSubject());
        return Response.ok(claims.getSubject()).build();
    }

    @Path("/logs")
    @GET
    //@Secured
    public Response toggleLog(){
        Level level = (Logger.getLogger("log4j").getLevel() == Level.OFF) ? Level.ALL : Level.OFF;
        Logger.getLogger("log4j").setLevel(level);
        return Response.ok((level == Level.OFF) ? "off":"all").build();
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
        JsonObject user = new JsonObject();
        user.addProperty("u", email);
        user.addProperty("p", pass);
        return Response.ok(user.toString()).build();
    }
}
