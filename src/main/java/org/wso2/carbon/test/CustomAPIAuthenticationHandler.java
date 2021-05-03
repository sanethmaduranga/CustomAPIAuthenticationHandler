package org.wso2.carbon.test;

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.rest.AbstractHandler;
import org.wso2.carbon.apimgt.gateway.handlers.security.APISecurityException;
import org.wso2.carbon.apimgt.gateway.handlers.Utils;
import org.apache.http.HttpStatus;

import java.util.Map;

public class CustomAPIAuthenticationHandler extends AbstractHandler {

    public boolean handleRequest(MessageContext messageContext) {
        try {
            if (authenticate(messageContext)) {
                return true;
            }
        } catch (APISecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean handleResponse(MessageContext messageContext) {
        return true;
    }

    public boolean authenticate(MessageContext synCtx) throws APISecurityException {
        Map headers = getTransportHeaders(synCtx);
        String authHeader = getAuthorizationHeader(headers);
        if (authHeader!= null && authHeader.startsWith("userName")) {
            return true;
        }
//        org.apache.axis2.context.MessageContext axis2MC = ((Axis2MessageContext) synCtx).getAxis2MessageContext();
//        axis2MC.setProperty("HTTP_SC",401);
        Utils.sendFault(synCtx, HttpStatus.SC_UNAUTHORIZED);
        return false;
    }

    private String getAuthorizationHeader(Map headers) {
        return (String) headers.get("Custom_Auth");
    }

    private Map getTransportHeaders(MessageContext messageContext) {
        return (Map) ((Axis2MessageContext) messageContext).getAxis2MessageContext().
                getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
    }
}
