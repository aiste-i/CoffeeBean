package org.coffee.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;


public final class BaseUrlProvider {

    public String getBaseUrl() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();


        String protocol = request.getScheme();


        String serverName = request.getServerName();


        int serverPort = request.getServerPort();


        String port = (serverPort == 80 || serverPort == 443) ? "" : ":" + serverPort;

        return protocol + "://" + serverName + port;
    }
}
