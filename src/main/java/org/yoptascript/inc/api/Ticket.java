package org.yoptascript.inc.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.yoptascript.inc.other.PDFCreator;
import org.yoptascript.inc.sql.Statements;

import java.sql.SQLException;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

//import org.yoptascript.inc.other.EmailNotificator;

@Secured
@Path("/ticket")
public class Ticket {

    private Statements statements;
    private PDFCreator creator;

    Ticket() {
    }

//    @Path("/insertTicket")
//    @POST
//    @Produces("application/pdf")
//    public Response insertTicket(@FormParam("ownerN") String ownerN,
//                                 @FormParam("ownerS") String ownerS, @FormParam("price") double price,
//
//                                 @FormParam("docId") int docId, @FormParam("usrId") String usrId,
//                                 @FormParam("agentId") int agentId, @FormParam("depId") String deptId, @FormParam("destId") String destId,
//                                 @FormParam("depTime") String date) {
//        statements = new Statements();
//        statements.connect();
        //TODO: make usrId as email and check it on db, depId to depName, destId to destName and also add date to check the schedule
//        try {
//            statements.insertTicket(ownerN, ownerS, price, docId,
//                    usrId, agentId, deptId, destId);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        List<String> ticket = Arrays.asList(ownerN, ownerS, Double.toString(price), Integer.toString(docId), usrId,
//                Integer.toString(agentId));
//        String name = ownerN + ownerS + usrId + deptId + destId + ".pdf";
//        System.out.println(name);
//        creator = new PDFCreator(name, ticket);
//        creator.createPdf();
//        File file = new File(name);
//        EmailNotificator notificator = new EmailNotificator();
//        notificator.sendPdf("aida.eduard@nu.edu.kz", "Ticket", "Pdf ticket", name);
//        System.out.println(file.getPath());
//        creator = null;
//        statements.disconnect();
//        Response.ResponseBuilder res = Response.ok(fileInputStream, MediaType.APPLICATION_OCTET_STREAM);
//        res.header("Content-Disposition", "attachment; filename="+name);
//        return Response.ok().build();
//    }

    @Path("/insertTicket")
    @POST
    public Response insertTicket(@FormParam("ownerN") String ownerN,
                                 @FormParam("ownerS") String ownerS, @FormParam("price") double price,
                                 @FormParam("docId") int docId, @FormParam("usrId") String usrId,
                                 @FormParam("agentId") int agentId, @FormParam("deptId") String deptId, @FormParam("destId") String destId,
                                 @FormParam("date") String date) {
        statements = new Statements();
        statements.connect();
        try {
            statements.insertTicket(ownerN, ownerS, price, docId,
                    usrId, agentId, deptId, destId, date);
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
        } finally {
            statements.disconnect();
        }
        return Response.ok().build();
    }

    @Secured
    @Path("/changeTicket")
    @PUT
    public Response changeTicket(@FormParam("ticketId") int ticketId, @FormParam("ownerN") String ownerN,
                                 @FormParam("ownerS") String ownerS, @FormParam("price") double price,
                                 @FormParam("docId") int docId, @FormParam("usrId") int usrId,
                                 @FormParam("agentId") int agentId, @FormParam("deptId") int deptId, @FormParam("destId") int destId,
                                 @CookieParam("role") String role) {
        if (role.equalsIgnoreCase("agent")) {
            statements = new Statements();
            statements.connect();
            try {
                statements.changeTicket(ticketId, ownerN, ownerS, price, docId,
                    usrId, agentId, deptId, destId);
            } catch (SQLException e) {
              return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
            } finally {
              statements.disconnect();
            }
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Secured
    @GET
    @Path("{ticketId: [0-9]+}")
    public Response getTicket(@PathParam("ticketId") int ticketId, @CookieParam("role") String role) {
        if (role.equalsIgnoreCase("agent")) {
            statements = new Statements();
            statements.connect();
            JsonObject json = new JsonObject();
            try {
                json = statements.getTicket(ticketId);
            } catch (SQLException e) {
              return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
            } finally {
              statements.disconnect();
            }
            return Response.ok(json.toString()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Secured
    @Path("/all")
    @GET
    public Response getAllTickets(@CookieParam("username") String username, @CookieParam("role") String role) {
        if (role.equalsIgnoreCase("agent")) {
            statements = new Statements();
            statements.connect();
            JsonArray json = new JsonArray();
            try {
                json = statements.getAllTickets(username);
            } catch (SQLException e) {
              return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
            } finally {
              statements.disconnect();
            }
            return Response.ok(json.toString()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Secured
    @Path("/allPastTickets")
    @GET
    public Response getAllPastTicketsOfUser(@CookieParam("username") String username, @CookieParam("role") String role) {
      if (role.equalsIgnoreCase("passenger")) {
        statements = new Statements();
        statements.connect();
        JsonArray json = new JsonArray();
        try {
          json = statements.getAllPastTicketsOfUser(username);
        } catch (SQLException e) {
          return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
        } finally {
          statements.disconnect();
        }
        return Response.ok(json.toString()).build();
      } else {
        return Response.status(Response.Status.FORBIDDEN).build();
      }
    }

    @Secured
    @Path("/allFutureTickets")
    @GET
    public Response getAllFutureTicketsOfUser(@CookieParam("username") String username, @CookieParam("role") String role) {
      if (role.equalsIgnoreCase("passenger")) {
        statements = new Statements();
        statements.connect();
        JsonArray json = new JsonArray();
        try {
          json = statements.getAllFutureTicketsOfUser(username);
        } catch (SQLException e) {
          return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
        } finally {
          statements.disconnect();
        }
        return Response.ok(json.toString()).build();
      } else {
        return Response.status(Response.Status.FORBIDDEN).build();
      }
    }

    @Secured
    @DELETE
    @Path("{ticketId: [0-9]+}")
    public Response deleteTicket(@PathParam("ticketId") int ticketId, @CookieParam("role") String role) {
        statements = new Statements();
        statements.connect();
        try {
            statements.deleteTicket(ticketId); // can return boolean if deleted
        } catch (SQLException e) {
          return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
        } finally {
          statements.disconnect();
        }
        return Response.ok().build();
    }

    @Secured
    @Path("/checkTicket")
    @GET
    public Response checkTicketAvailability(@QueryParam("dept") String dept, @QueryParam("dest") String dest,
                                            @QueryParam("t") int train, @QueryParam("date") String date, @CookieParam("role") String role) {
      if (role.equalsIgnoreCase("agent")) {
        statements = new Statements();
        statements.connect();
        boolean check = false;
        try {
          check = statements.checkTicket(dept, dest, train, date);
        } catch (SQLException e) {
          return Response.status(Response.Status.BAD_REQUEST).header("err", e).build();
        } finally {
          statements.disconnect();
        }
        JsonObject json = new JsonObject();
        json.addProperty("available", check);
        return Response.ok(json.toString()).build();
      } else {
        return Response.status(Response.Status.FORBIDDEN).build();
      }
    }
}

