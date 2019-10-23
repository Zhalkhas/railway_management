package org.yoptascript.inc.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.yoptascript.inc.sql.Statements;

import java.sql.SQLException;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/search")
public class Search {

    public Search() {
    }

    private Statements statements;

    @Path("/searchCity")
    @GET
    public Response searchStations(@QueryParam("q") String like) {
        statements = new Statements();
        statements.connect();
        Map<Integer, String> cities = null;
        try {
            cities = statements.getStations(like);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statements.disconnect();
        return Response.ok((new Gson()).toJson(cities)).build();
    }

    @Path("/search")
    @GET
    public Response getRouteStations(@QueryParam("dept") String dept, @QueryParam("dest") String dest, @QueryParam("date") String date){
        statements = new Statements();
        statements.connect();
        JsonArray routes = null;
        try {
            routes = statements.getRouteStations(dept, dest, date);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(routes.toString()).build();
    }

    @Path("/getRoutes")
    @GET
    public Response getRoutes(@QueryParam("dept") String dept,
                              @QueryParam("dest") String dest, @QueryParam("date") String date) {
        statements = new Statements();
        statements.connect();
        JsonArray routes = null;
        try {
            routes = statements.getRoutes(dept, dest, date);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.ok(routes.toString()).build();
    }
}
