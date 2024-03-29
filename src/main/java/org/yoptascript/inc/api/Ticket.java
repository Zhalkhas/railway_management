package org.yoptascript.inc.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.yoptascript.inc.other.EmailNotificator;
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
                                 @FormParam("docId") int docId,
                                 @FormParam("schId") int deptId, @FormParam("schId2") int destId,
                                 @FormParam("deptId") String from, @FormParam("destId") String to,
                                 @FormParam("date") String date, @CookieParam("username") String email) {
        statements = new Statements();
        statements.connect();
        try {
            statements.insertTicket(ownerN, ownerS, price, docId,
                    email, deptId, destId, date);
            EmailNotificator notificator = new EmailNotificator();
            notificator.sendEdit(email, "YOUR TICKET", "You booked a ticket from: " + from + ", to: " + to + ", departure time: " + date + ", for: " + ownerN + " " + ownerS);
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
                                 @FormParam("ownerS") String ownerS, @FormParam("docId") int docId,
                                 @CookieParam("role") String role) {
        if (role.equalsIgnoreCase("agent")) {
            statements = new Statements();
            statements.connect();
            try {
                String email = statements.changeTicket(ticketId, ownerN, ownerS, docId);
                EmailNotificator notificator = new EmailNotificator();
                notificator.sendEdit(email, "TICKET WAS CHANGED", "Please check your ticket#" + ticketId + ", agent made some changes to it");
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
//          JsonArray json = new JsonArray();
//          for (int i = 0; i < 10; i++) {
//            JsonObject jsob = new JsonObject();
//            jsob.addProperty("ticketId", i);
//            jsob.addProperty("ownerN", "name" + i);
//            jsob.addProperty("ownerS", "surname" + i);
//            jsob.addProperty("price", i * 10000);
//            jsob.addProperty("passengerID", i * 10 + i * 20);
//            jsob.addProperty("departureTime", "18:00");
//            jsob.addProperty("departureName", "city" + i);
//            jsob.addProperty("arrivalTime", "23:59");
//            jsob.addProperty("arrivalName", "city" + 100*i);
//            json.add(jsob);
//          }
            return Response.ok(json.toString()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @Secured
    @Path("/allPastTickets")
    @GET
    public Response getAllPastTicketsOfUser(@CookieParam("username") String username, @CookieParam("role") String role) {
      if (role.equalsIgnoreCase("user")) {
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
      if (role.equalsIgnoreCase("user")) {
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

