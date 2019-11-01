package org.yoptascript.inc.api;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.yoptascript.inc.sql.Statements;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.KeyPair;
import java.util.Date;

@Path("/auth")
public class Auth {
    private KeyPair keys;

    Auth(KeyPair keys) {
        this.keys = keys;
    }

    @Path("/login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("u") String username, @FormParam("p") String pass) {
        try {
            Statements statements = new Statements();
            statements.login(username, pass);
            String token = getToken(username);
            NewCookie newcookie = new NewCookie("token", token, "/","","", 3600*24, false);
            return Response.ok().cookie(newcookie).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @GET
    @Path("/logout")
    public Response logout(){
        NewCookie cookie = new NewCookie("token", null, "/", null, null, 0, false, true);
        return Response.ok().cookie(cookie).build();
    }

    private String getToken(String username) {
        JwtBuilder jwts = Jwts.builder();
        jwts.setSubject(username);
        jwts.setIssuedAt(new Date());
        jwts.signWith(keys.getPrivate());
        return jwts.compact();
    }
}