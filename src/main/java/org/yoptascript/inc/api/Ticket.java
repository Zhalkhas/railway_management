package org.yoptascript.inc.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.yoptascript.inc.sql.Statements;

import java.sql.SQLException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/ticket")
public class Ticket {

    private Statements statements;

    Ticket() {
    }

    @Path("/insertTicket")
    @POST
    public Response insertTicket(@FormParam("ownerN") String ownerN,
                                 @FormParam("ownerS") String ownerS, @FormParam("price") double price,
                                 @FormParam("docId") int docId, @FormParam("usrId") int usrId,
                                 @FormParam("agentId") int agentId, @FormParam("depId") int deptId, @FormParam("destId") int destId) {
        statements = new Statements();
        statements.connect();
        try {
            statements.insertTicket(ownerN, ownerS, price, docId,
                    usrId, agentId, deptId, destId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statements.disconnect();
        return Response.ok().build();
    }

    //TODO: past tickets and future tickets

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

    @Path("/allTickets")
    @GET
    public Response getAllTicketsOfUser(@QueryParam("userId") int userId) {
        statements = new Statements();
        statements.connect();
        JsonArray json = new JsonArray();
        try {
            json = statements.getAllTicketsOfUser(userId);
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
}
