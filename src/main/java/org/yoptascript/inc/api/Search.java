package org.yoptascript.inc.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.yoptascript.inc.sql.Statements;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/search")
public class Search {

    Statements statements;

    @Path("/searchCity")
    @GET
    public Response searchStations(@QueryParam("q") String like) {
        statements = new Statements();
        statements.connect();
        Map<Integer, String> cities = statements.getStations(like);
        statements.disconnect();
        return Response.ok(new Gson().toJson(cities)).build();
    }

    @Path("/getRoutes")
    @GET
    public Response getRoutes(@QueryParam("dept") String dept,
                              @QueryParam("dest") String dest, @QueryParam("date") String date) {
        statements = new Statements();
        statements.connect();
        JsonArray routes = statements.getRoutes(dept, dest, date);
        return Response.ok(routes.toString()).build();
    }
}
