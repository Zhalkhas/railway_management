package org.yoptascript.inc.api;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.yoptascript.inc.certs.KeysReader;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.net.URI;
import java.rmi.MarshalledObject;
import java.util.Date;
import java.util.Map;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        String token = "";

        Map<String, Cookie> cookies = requestContext.getCookies();
        for (Map.Entry<String, Cookie> cookie : cookies.entrySet()) {
            if (cookie.getKey().toLowerCase().equals("token")) {
                token = cookie.getValue().getValue();
                break;
            }
        }
        if (!token.equals("")) {
            try {
                validateToken(token);
            } catch (Exception e) {
                abortWithUnauthorized(path, method, requestContext);
            }
        } else {
            abortWithUnauthorized(path, method, requestContext);
        }
    }

    private void abortWithUnauthorized(String path, String method, ContainerRequestContext requestContext) {
        if (path.equals("user") && method.toLowerCase().equals("get")) {
            requestContext.abortWith(Response.ok("null").build());

        } else {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    private void validateToken(String token) throws Exception {
        Jws<Claims> jws;
        jws = Jwts.parser()
                .setSigningKey((new KeysReader("pub.der", "priv.der"))
                        .getPublicKey())
                .parseClaimsJws(token);
        Claims claims = jws.getBody();
        System.out.println(claims);
    }
}
