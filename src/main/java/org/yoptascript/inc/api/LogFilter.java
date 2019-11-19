package org.yoptascript.inc.api;

import org.apache.log4j.Logger;

import javax.ws.rs.container.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class LogFilter implements  ContainerResponseFilter {
    private Logger logger = Logger.getLogger("log4j");

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        logger.info(getLogEntry(containerRequestContext, containerResponseContext));
    }

    private String getLogEntry(ContainerRequestContext req, ContainerResponseContext resp){
        //TODO:implement normal logging
        StringBuilder logEntry = new StringBuilder();
        logEntry.append("<uri>").append(req.getUriInfo().getAbsolutePath().getPath()).append("</uri>");
        logEntry.append("<method>").append(req.getMethod()).append("<method>");
        logEntry.append("<req_headers>").append(req.getHeaders()).append("</req_headers>");
        logEntry.append("<resp_headers>").append(resp.getStringHeaders()).append("</resp_headers>");
        logEntry.append("<status>").append(resp.getStatus()).append("</status>");
        return new String(logEntry);
    }
}
