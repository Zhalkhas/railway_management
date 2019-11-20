package org.yoptascript.inc.api;

import com.google.gson.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.yoptascript.inc.certs.KeysReader;
import org.yoptascript.inc.other.EmailNotificator;
import org.yoptascript.inc.sql.Statements;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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

    @Path("/newUser")
    @POST
    public Response createUser(@FormParam("email") String email, @FormParam("password") String pass,
                               @FormParam("fname") String fname, @FormParam("lname") String lname) {
        statements = new Statements();
        statements.connect();
        boolean isCreated = false;
        try {
            isCreated = statements.createUser(email, pass, fname, lname);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
        } finally {
            statements.disconnect();
        }
        JsonObject user = new JsonObject();
        user.addProperty("u", email);
        user.addProperty("p", pass);
        return isCreated ? Response.ok(user.toString()).build() : Response.status(Response.Status.CONFLICT).build();
    }

    @Secured
    @POST
    @Path("/newMessage")
    public Response sendMessage(@CookieParam("role") String role, @CookieParam("username") String email,
                                @FormParam("message") String message, @FormParam("station") String station) {
        statements = new Statements();
        statements.connect();
        String agentMail = "";
        try {
            agentMail = statements.getAgentEmail(station);
        } catch(SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
        } finally {
            statements.disconnect();
        }
        EmailNotificator notificator = new EmailNotificator();
        notificator.sendEdit(agentMail, "CHANGES IN TICKET", message);
        return Response.ok().build();
    }

    @Secured
    @GET
    @Path("/profile")
    public Response profile(@CookieParam("role") String role) {
        JsonObject json = new JsonObject();
        switch (role) {
            case ("user"):
                json.addProperty("path","passenger_profile.html");
                break;
            case ("agent"):
                json.addProperty("path","agent.html");
                break;
            case ("manager"):
                json.addProperty("path", "manager.html");
                break;
        }
        return Response.ok(json.toString()).build();
    }
}
