package org.yoptascript.inc.api;

import org.apache.log4j.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Log
@Provider
@PreMatching
public class LogFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private Logger logger = Logger.getLogger("log4j");
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        logger.info("request:" + getRequestContext(containerRequestContext));
    }
    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        logger.info("request:" + getRequestContext(containerRequestContext));
        logger.info("response:" + getResponseContext(containerResponseContext));
    }

    private String getRequestContext(ContainerRequestContext c){
        //TODO:implement normal logging
        return c.toString();
    }

    private String getResponseContext(ContainerResponseContext c){
        //TODO:implement normal logging
        return c.toString();
    }
}
