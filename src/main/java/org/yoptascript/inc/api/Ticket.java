package org.yoptascript.inc.api;

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
    @POST()
    public Response insertTicket(@FormParam("ticketId") int ticketId, @FormParam("ownerN") String ownerN,
                                 @FormParam("ownerS") String ownerS, @FormParam("price") double price,
                                 @FormParam("docId") int docId, @FormParam("usrId") int usrId,
                                 @FormParam("agentId") int agentId, @FormParam("schedId") int schedId) {
        statements = new Statements();
        statements.connect();
        try {
            statements.insertTicket(ticketId, ownerN, ownerS, price, docId,
                    usrId, agentId, schedId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statements.disconnect();
        return Response.ok().build();
    }

    @Path("/checkTicket")
    @GET()
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
