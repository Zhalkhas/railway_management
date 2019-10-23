package org.yoptascript.inc.api;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.yoptascript.inc.sql.Statements;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.KeyPair;
import java.util.Date;
//import java.util.UUID;

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
            return Response.ok().header("Authorization", "Bearer " + token).build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).header("Exception", e.toString()).build();
        }
    }

    private String getToken(String username) {
        JwtBuilder jwts = Jwts.builder();
        jwts.setSubject(username);
        //long current = (new Date()).getTime();
        jwts.setIssuedAt(new Date());
        //jwts.setExpiration(new Date(current + 86400000)); //one day
        jwts.signWith(keys.getPrivate());
        //jwts.setId(String.valueOf(UUID.randomUUID()));
        return jwts.compact();
    }
}