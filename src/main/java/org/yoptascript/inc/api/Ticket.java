package org.yoptascript.inc.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.yoptascript.inc.other.PDFCreator;
import org.yoptascript.inc.sql.Statements;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/ticket")
public class Ticket {

    private Statements statements;
    private PDFCreator creator;

    Ticket() {
    }

    @Path("/insertTicket")
    @POST
    @Produces("application/pdf")
    public Response insertTicket(@FormParam("ownerN") String ownerN,
                                 @FormParam("ownerS") String ownerS, @FormParam("price") double price,
                                 @FormParam("docId") int docId, @FormParam("usrId") int usrId,
                                 @FormParam("agentId") int agentId, @FormParam("depId") int deptId, @FormParam("destId") int destId,
                                 @FormParam("date") String date) {
        statements = new Statements();
        statements.connect();
        //TODO: make usrId as email and check it on db, depId to depName, destId to destName and also add date to check the schedule
        try {
            statements.insertTicket(ownerN, ownerS, price, docId,
                    usrId, agentId, deptId, destId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> ticket = Arrays.asList(ownerN, ownerS, Double.toString(price), Integer.toString(docId), Integer.toString(usrId),
                Integer.toString(agentId));
        String name = ownerN + ownerS + usrId + deptId + destId + ".pdf";
        creator = new PDFCreator(name, ticket);
        File file = new File(name);
        statements.disconnect();
        return Response.ok(file).build();
    }

    @Path("/changeTicket")
    @PUT
    public Response changeTicket(@FormParam("ticketId") int ticketId, @FormParam("ownerN") String ownerN,
                                 @FormParam("ownerS") String ownerS, @FormParam("price") double price,
                                 @FormParam("docId") int docId, @FormParam("usrId") int usrId,
                                 @FormParam("agentId") int agentId, @FormParam("deptId") int deptId, @FormParam("destId") int destId) {
        statements = new Statements();
        statements.connect();
        try {
            statements.changeTicket(ticketId, ownerN, ownerS, price, docId,
                    usrId, agentId, deptId, destId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statements.disconnect();
        return Response.ok().build();
    }

    @GET
    @Path("{ticketId: [0-9]+}")
    public Response getTicket(@PathParam("ticketId") int ticketId) {
        statements = new Statements();
        statements.connect();
        JsonObject json = new JsonObject();
        try {
            json = statements.getTicket(ticketId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statements.disconnect();
        return Response.ok(json.toString()).build();
    }

    @Path("/all")
    @GET
    public Response getAllTickets() {
        JsonArray array = new JsonArray();
        //for(int i = 0; i < 10; i++){
        JsonObject json = new JsonObject();
        System.out.println("before:"+json);
        json.addProperty("ticketId", 1);
        json.addProperty("ownerN", "a");
        json.addProperty("ownerS", "b");
        json.addProperty("price", 1);
        json.addProperty("passengerID", 1);
        json.addProperty("departureTime", 1);
        json.addProperty("departureName", 1);
        json.addProperty("arrivalTime", 1);
        json.addProperty("arrivalName", 1);
        System.out.println(json);
        array.add(json);
        System.out.println(array);
        //}
        return Response.ok(array.toString()).build();
    }

    @Path("/allPastTickets")
    @GET
    public Response getAllPastTicketsOfUser(@QueryParam("userId") int userId) {
        statements = new Statements();
        statements.connect();
        JsonArray json = new JsonArray();
        try {
            json = statements.getAllPastTicketsOfUser(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statements.disconnect();
        return Response.ok(json.toString()).build();
    }

    @Path("/allFutureTickets")
    @GET
    public Response getAllFutureTicketsOfUser(@QueryParam("userId") int userId) {
        statements = new Statements();
        statements.connect();
        JsonArray json = new JsonArray();
        try {
            json = statements.getAllFutureTicketsOfUser(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statements.disconnect();
        return Response.ok(json.toString()).build();
    }

    @DELETE
    @Path("{ticketId: [0-9]+}")
    public Response deleteTicket(@PathParam("ticketId") int ticketId) {
        statements = new Statements();
        statements.connect();
        try {
            statements.deleteTicket(ticketId); // can return boolean if deleted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statements.disconnect();
        return Response.ok().build();
    }

    @Path("/checkTicket")
    @GET
    public Response checkTicketAvailability(@QueryParam("dept") String dept, @QueryParam("dest") String dest,
                                            @QueryParam("t") int train, @QueryParam("date") String date) {
        statements = new Statements();
        statements.connect();
        boolean check = false;
        try {
            check = statements.checkTicket(dept, dest, train, date);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statements.disconnect();
        JsonObject json = new JsonObject();
        json.addProperty("available", check);
        return Response.ok(json.toString()).build();
    }

    @Path("/all")
    @GET
    public Response getAllTickets() {
        JsonArray array = new JsonArray();
        for(int i = 0; i < 10; i++){
            JsonObject json = new JsonObject();
            json.addProperty("ticketId", i);
            json.addProperty("ownerN", "a");
            json.addProperty("ownerS", "b");
            json.addProperty("price", i);
            json.addProperty("passengerID", i);
            json.addProperty("departureTime", i);
            json.addProperty("departureName", i);
            json.addProperty("arrivalTime", i);
            json.addProperty("arrivalName", i);
            array.add(json);
        }
        return Response.ok(array).build();
    }
}

